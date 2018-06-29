package com.waldo.inventory.database;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.ImageType;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.classes.DbErrorObject;
import com.waldo.inventory.database.classes.DbQueue;
import com.waldo.inventory.database.classes.DbQueueObject;
import com.waldo.inventory.database.interfaces.ImageChangedListener;
import com.waldo.inventory.database.settings.settingsclasses.ImageServerSettings;
import com.waldo.inventory.managers.LogManager;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.Utils.Statics.QueryType.*;
import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.scriptResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ImageDbAccess {

    private static final LogManager LOG = LogManager.LOG(ImageDbAccess.class);

    private static final String IMAGE_QUEUE_WORKER = "Image queue worker";
    private static final String IMAGE_ERROR_WORKER = "Image error worker";


    // Singleton
    private static final ImageDbAccess INSTANCE = new ImageDbAccess();

    public static ImageDbAccess imDb() {
        return INSTANCE;
    }

    private ImageDbAccess() {
    }

    private BasicDataSource imageDataSource;

    private boolean initialized = false;
    private String loggedUser = "";

    // Images
    private DbQueue<DbQueueObject> imageWorkList;
    private DbQueue<DbErrorObject> imageNonoList;
    private ImageQueueWorker imageQueueWorker;
    private ImageErrorWorker imageErrorWorker;

    // Listener
    private List<ImageChangedListener> imageChangedListeners = new ArrayList<>();


    public void init() throws SQLException {
        initialized = false;
        ImageServerSettings s = settings().getImageServerSettings();
        if (s != null) {
            // TODO get from settings
            imageDataSource = new BasicDataSource();
            imageDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            imageDataSource.setUrl(s.getImageDbSettings().createMySqlUrl() + "?zeroDateTimeBehavior=convertToNull&connectTimeout=5000&socketTimeout=30000");
            imageDataSource.setUsername(s.getImageDbSettings().getDbUserName());
            imageDataSource.setPassword(s.getImageDbSettings().getDbUserPw());

            initialized = testConnection(imageDataSource);

            LOG.info("Image database initialized with connection: " + s.getImageDbSettings().createMySqlUrl());
        }
    }

    public static boolean testConnection(BasicDataSource dataSource) throws SQLException {
        String sql = "SELECT 1;";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void startImageWorkers() {
        imageWorkList = new DbQueue<>(100);
        imageQueueWorker = new ImageQueueWorker(IMAGE_QUEUE_WORKER);
        imageQueueWorker.execute();

        imageNonoList = new DbQueue<>(100);
        imageErrorWorker = new ImageErrorWorker(IMAGE_ERROR_WORKER);
        imageErrorWorker.execute();
    }

    public void close() {
        if (imageDataSource != null) {
            Status().setMessage("Closing down");
            if (imageQueueWorker != null) {
                imageQueueWorker.keepRunning = false;
                imageWorkList.stop();
            }
            if (imageErrorWorker != null) {
                imageErrorWorker.keepRunning = false;
                imageNonoList.stop();
            }
        }
    }

    public void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }


    /*
     *                  Workers
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private class ImageQueueWorker extends SwingWorker<Integer, String> {

        volatile boolean keepRunning = true;
        private final String name;

        ImageQueueWorker(String name) {
            this.name = name;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            Thread.currentThread().setName(name);
            while (keepRunning) {

                DbQueueObject queueObject = imageWorkList.take();
                if (queueObject != null) {
                    try (Connection connection = getConnection()) {
                        // Open db
                        try (PreparedStatement stmt = connection.prepareStatement("BEGIN;")) {
                            stmt.execute();
                        }

                        try {
                            boolean hasMoreWork;
                            do {
                                DbImage dbImage = (DbImage) queueObject.getObject();
                                Statics.QueryType type = queueObject.getQueryType();
                                switch (type) {
                                    case Insert: {
                                        String sql = dbImage.getScript(dbImage.getImageType(), DbObject.SQL_INSERT);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                                            DatabaseHelper.insert(stmt, dbImage);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbImage) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbImage, e, Insert, sql);
                                                imageNonoList.put(object);
                                            }
                                        }
                                    }
                                    break;
                                    case Update: {
                                        String sql = dbImage.getScript(dbImage.getImageType(), DbObject.SQL_UPDATE);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                                            DatabaseHelper.update(stmt, dbImage);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbImage) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbImage, e, Update, sql);
                                                imageNonoList.put(object);
                                            }
                                        }
                                        break;
                                    }
                                    case Delete: {
                                        String sql = dbImage.getScript(dbImage.getImageType(), DbObject.SQL_DELETE);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                                            DatabaseHelper.delete(stmt, dbImage);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbImage) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbImage, e, Delete, sql);
                                                imageNonoList.put(object);
                                            }
                                        }
                                        break;
                                    }
                                }
                                notifyListeners(type, dbImage);

                                if (imageWorkList.size() > 0) {
                                    queueObject = imageWorkList.take();
                                    hasMoreWork = true;
                                } else {
                                    hasMoreWork = false;
                                }
                            } while (hasMoreWork && keepRunning);

                            // Close db
                            try (PreparedStatement stmt = connection.prepareStatement("commit;")) {
                                stmt.execute();
                            }
                        } catch (Exception e) {
                            System.out.print("ROLLING BACK DB :" + e);
                            try (PreparedStatement stmt = connection.prepareStatement("rollback;")) {
                                stmt.execute();
                            }
                            throw e;
                        }
                    }
                }
            }
            return 0;
        }

        @Override
        protected void process(List<String> chunks) {
            System.out.println(chunks);
        }

        @Override
        protected void done() {

        }
    }

    private class ImageErrorWorker extends SwingWorker<Integer, String> {

        volatile boolean keepRunning = true;
        private final String name;

        public ImageErrorWorker(String name) {
            this.name = name;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            Thread.currentThread().setName(name);
            while (keepRunning) {
                DbErrorObject error = imageNonoList.take();
                if (error != null) {
                    System.err.println(error);
                }
            }
            return 0;
        }

        @Override
        protected void done() {

        }
    }


    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private BasicDataSource getImageDataSource() {
        return imageDataSource;
    }

    public static Connection getConnection() throws SQLException {
        return imDb().getImageDataSource().getConnection();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void addImageChangedListener(ImageChangedListener imageChangedListener) {
        if (!imageChangedListeners.contains(imageChangedListener)) {
            imageChangedListeners.add(imageChangedListener);
        }
    }

    public void removeImageChangedListener(ImageChangedListener imageChangedListener) {
        imageChangedListeners.remove(imageChangedListener);
    }

    private void notifyListeners(Statics.QueryType queryType, DbImage dbImage) {
        for (ImageChangedListener listener : imageChangedListeners) {
            switch (queryType) {
                case Insert:
                    listener.onInserted(dbImage);
                    break;
                case Update:
                    listener.onUpdated(dbImage);
                    break;
                case Delete:
                    listener.onDeleted(dbImage);
                    break;
            }
        }
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void insert(final DbImage image) {
        image.getAud().setInserted(loggedUser);
        if (initialized) {
            SwingUtilities.invokeLater(() -> {
                DbQueueObject toInsert = new DbQueueObject(image, Insert);
                try {
                    imageWorkList.put(toInsert);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void update(final DbImage image) {
        image.getAud().setUpdated(loggedUser);
        if (initialized) {
            SwingUtilities.invokeLater(() -> {
                DbQueueObject toUpdate = new DbQueueObject(image, Update);
                try {
                    imageWorkList.put(toUpdate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void delete(final DbImage image) {
        if (initialized) {
            SwingUtilities.invokeLater(() -> {
                DbQueueObject toDelete = new DbQueueObject(image, Delete);
                try {
                    imageWorkList.put(toDelete);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public DbImage fetch(final ImageType type, final long id) {
        DbImage image = null;
        if (initialized && (type != null) && (id > DbObject.UNKNOWN_ID)) {
            String sql = "SELECT * FROM " + type.getFolderName() + " WHERE id = " + id + ";";
            try (Connection connection = getConnection()) {
                try (PreparedStatement stmt = connection.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        image = new DbImage(ImageType.ItemImage);
                        image.setId(rs.getLong("id"));
                        image.setName(rs.getString("name"));
                        image.setImageType(rs.getInt("imageType"));
                        image.setImageIcon(DbImage.blobToImage(rs.getBlob("image")));
                        image.setInserted(true);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return image;
    }


    // Remove with more efficient!
    public List<DbImage> fetchAllImages(ImageType type) {
        List<DbImage> images = new ArrayList<>();

        DbImage d;
        String sql = scriptResource.readString(type.getFolderName() + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    d = new DbImage(type);
                    d.setId(rs.getLong("id"));
                    d.setName(rs.getString("name"));
                    d.setImageIcon(DbImage.blobToImage(rs.getBlob("image")));
                    d.setInserted(true);

                    images.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return images;
    }
}
