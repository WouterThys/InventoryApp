package com.waldo.inventory.database;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.classes.DbErrorObject;
import com.waldo.inventory.database.classes.DbQueue;
import com.waldo.inventory.database.classes.DbQueueObject;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.managers.LogManager;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
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
    private ImageDbAccess() {}

    private BasicDataSource imageDataSource;

    private boolean initialized = false;
    private String loggedUser = "";

    // Images
    private DbQueue<DbQueueObject> imageWorkList;
    private DbQueue<DbErrorObject> imageNonoList;
    private ImageQueueWorker imageQueueWorker;
    private ImageErrorWorker imageErrorWorker;


    public void init() throws SQLException {
        initialized = false;
        DbSettings s = settings().getDbSettings();
        if (s != null) {
            // TODO get from settings
            imageDataSource = new BasicDataSource();
            imageDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            imageDataSource.setUrl("jdbc:mysql:192.168.0.162/inventoryImages" + "?zeroDateTimeBehavior=convertToNull&connectTimeout=5000&socketTimeout=30000");
            imageDataSource.setUsername(s.getDbUserName());
            imageDataSource.setPassword(s.getDbUserPw());
            LOG.info("Image database initialized with connection: " + s.createMySqlUrl());
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
                    try (Connection connection = DatabaseAccess.getConnection()) {
                        // Open db
                        try (PreparedStatement stmt = connection.prepareStatement("BEGIN;")) {
                            stmt.execute();
                        }

                        try {
                            boolean hasMoreWork;
                            do {
                                DbObject dbo = queueObject.getObject();
                                Statics.QueryType type = queueObject.getQueryType();
                                switch (type) {
                                    case Insert: {

                                    }
                                    break;
                                    case Update: {

                                        break;
                                    }
                                    case Delete: {

                                        break;
                                    }
                                }

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
                    switch (error.getQueryType()) {
                        case Select:

                            break;
                        case Insert:

                            break;
                        case Update:

                            break;
                        case Delete:

                            break;
                    }
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
}
