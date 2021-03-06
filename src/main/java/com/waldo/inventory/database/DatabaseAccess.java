package com.waldo.inventory.database;

import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.classes.DbEvent;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.classes.dbclasses.Package;
import com.waldo.inventory.database.classes.DbErrorObject;
import com.waldo.inventory.database.classes.DbQueue;
import com.waldo.inventory.database.classes.DbQueueObject;
import com.waldo.inventory.database.interfaces.DbErrorListener;
import com.waldo.inventory.database.interfaces.DbExecuteListener;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.managers.LogManager;
import com.waldo.inventory.managers.TableManager;
import com.waldo.utils.DateUtils;
import com.waldo.utils.FileUtils;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.Utils.Statics.QueryType.*;
import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.scriptResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;
import static com.waldo.inventory.managers.CacheManager.cache;

public class DatabaseAccess {

    private static final LogManager LOG = LogManager.LOG(DatabaseAccess.class);

    private static final String QUEUE_WORKER = "Queue worker";
    private static final String ERROR_WORKER = "Error worker";

    // Singleton
    private static final DatabaseAccess INSTANCE = new DatabaseAccess();
    public static DatabaseAccess db() {
        return INSTANCE;
    }

    private BasicDataSource mainDataSource;

    private boolean initialized = false;
    private String loggedUser = "";
    private long cacheOnlyFakedId = 2;

    // Main objects
    private DbQueue<DbQueueObject> workList;
    private DbQueue<DbErrorObject> nonoList;
    private DbQueueWorker dbQueueWorker;
    private DbErrorWorker dbErrorWorker;

    private List<DbExecuteListener> executeListenerList = new ArrayList<>();

    // Events
    private DbErrorListener errorListener;

    private DatabaseAccess() {
    }

    public void init() throws SQLException {
        initialized = false;
        DbSettings s = settings().getDbSettings();
        if (s != null) {
            loggedUser = s.getDbUserName();
            if (!Main.CACHE_ONLY) {
                switch (s.getDbType()) {
                    case Online:
                        initMySql(s);
                        break;
                    case Local:
                        initSqLite(s);
                        break;
                }

                // Test
                initialized = testConnection(mainDataSource);
                switch (s.getDbType()) {
                    case Online:
                        TableManager.dbTm().init(mainDataSource, s);
                        Status().setDbConnectionText(initialized, s.getDbIp(), s.getDbName(), s.getDbUserName());
                        break;
                    case Local:
                        Status().setDbConnectionText(initialized, "DB BACKUP", s.getDbName(), s.getDbUserName());
                        break;
                }
            } else {
                Status().setDbConnectionText(false, "", "", "");
            }
        }
    }


    private void initMySql(DbSettings settings) {
        mainDataSource = new BasicDataSource(); // new MySqlDataSource
        mainDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        mainDataSource.setUrl(settings.createMySqlUrl() + "?zeroDateTimeBehavior=convertToNull&connectTimeout=20000&socketTimeout=30000");
        mainDataSource.setUsername(settings.getDbUserName());
        mainDataSource.setPassword(settings.getDbUserPw());
        LOG.info("Database initialized with connection: " + settings.createMySqlUrl());
    }

    private void initSqLite(DbSettings settings) {
        mainDataSource = new BasicDataSource();
        mainDataSource.setDriverClassName("org.sqlite.JDBC");
        mainDataSource.setUrl("jdbc:sqlite:" + settings.getDbName());
        mainDataSource.setUsername(settings.getDbUserName());
        mainDataSource.setPassword("");
        mainDataSource.setMaxIdle(10);
        mainDataSource.setPoolPreparedStatements(true);
        mainDataSource.setLogAbandoned(false);
        mainDataSource.setInitialSize(5);
        mainDataSource.setRemoveAbandonedTimeout(60);

        String sql = "PRAGMA foreign_keys=OFF;";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void reInit(DbSettings s) throws SQLException {
        initialized = false;
        if (s != null) {
            if (!Main.CACHE_ONLY) {
                // Test
                initialized = testConnection(mainDataSource);
                Status().setDbConnectionText(initialized, s.getDbIp(), s.getDbName(), s.getDbUserName());
                if (initialized) {
                    cache().clearCache();
                }
            }
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

    public static List<String> testDatabase(BasicDataSource dataSource) throws SQLException {
        List<String> missingTables = new ArrayList<>();
        int numberOfTables = scriptResource.readInteger("test.numberOfTables");
        String sql = scriptResource.readString("test.script");
        try (Connection connection = dataSource.getConnection()) {
            for (int i = 0; i < numberOfTables; i++) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    //String schemaName = mainDataSource.get();
//                    stmt.setString(1, schemaName);
//                    String tableName = scriptResource.readString("test.tableNames." + i);
//                    stmt.setString(2, tableName);
//
//                    try (ResultSet rs = stmt.executeQuery()) {
//                        if (!rs.next()) {
//                            missingTables.add(tableName);
//                        }
//                    }
                }
            }
        }
        return missingTables;
    }

    public void startBackgroundWorkers() {
        if (!Main.CACHE_ONLY) {
            workList = new DbQueue<>(1000);
            dbQueueWorker = new DbQueueWorker(QUEUE_WORKER);
            dbQueueWorker.execute();
            LOG.info("Database started thread: " + QUEUE_WORKER);

            nonoList = new DbQueue<>(1000);
            dbErrorWorker = new DbErrorWorker(ERROR_WORKER);
            dbErrorWorker.execute();
            LOG.info("Database started thread: " + ERROR_WORKER);
        }
    }

    public void addErrorListener(DbErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void addExecuteListener(DbExecuteListener listener) {
        if (!executeListenerList.contains(listener)) {
            executeListenerList.add(listener);
        }
    }

    public void removeExecuteListener(DbExecuteListener listener) {
        if (executeListenerList.contains(listener)) {
            executeListenerList.remove(listener);
        }
    }

    private void onExecuted(String sql) {
        for (DbExecuteListener listener : executeListenerList) {
            SwingUtilities.invokeLater(() -> listener.onExecuted(sql));
        }
    }

    private void onExecuteError(String sql, Throwable throwable) {
        for (DbExecuteListener listener : executeListenerList) {
            SwingUtilities.invokeLater(() -> listener.onExecuteError(sql, throwable));
        }
    }


    public void close() {
        if (mainDataSource != null) {
            Status().setMessage("Closing down");
            if (dbQueueWorker != null) {
                dbQueueWorker.keepRunning = false;
                workList.stop();
            }
            if (dbErrorWorker != null) {
                dbErrorWorker.keepRunning = false;
                nonoList.stop();
            }
            if (Main.DEBUG_MODE) {
                String info = "DBQUEUE -> INFO: \r\n";
                info += "\t- Handled " + workList.getCount() + " elements \r\n";
                info += "\t- Average time in list: " + workList.getSessionAverageTimeInQueue() + "ms \r\n";
                info += "\t- Max capacity: " + workList.getSessionMaxCapacity();
                System.out.println(info);
            }
        }
    }

    private void workerDone(String workerName) {
        System.out.println(workerName + " :thread done");
//        try {
//            if (mainDataSource != null) {
//                mainDataSource.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    /*
    *                  Workers
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private class DbQueueWorker extends SwingWorker<Integer, String> {

        volatile boolean keepRunning = true;
        private final String name;

        DbQueueWorker(String name) {
            this.name = name;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            Thread.currentThread().setName(name);
            while (keepRunning) {

                DbQueueObject queueObject = workList.take();
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
                                        String sql = dbo.getScript(DbObject.SQL_INSERT);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                                            DatabaseHelper.insert(stmt, dbo);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbo) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbo, e, Insert, sql);
                                                nonoList.put(object);
                                            }
                                        }
                                    }
                                    break;
                                    case Update: {
                                        String sql = dbo.getScript(DbObject.SQL_UPDATE);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                                            DatabaseHelper.update(stmt, dbo);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbo) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbo, e, Update, sql);
                                                nonoList.put(object);
                                            }
                                        }
                                        break;
                                    }
                                    case Delete: {
                                        String sql = dbo.getScript(DbObject.SQL_DELETE);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                                            DatabaseHelper.delete(stmt, dbo);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbo) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbo, e, Delete, sql);
                                                nonoList.put(object);
                                            }
                                        }
                                        break;
                                    }
                                    case Custom: {
                                        String sql = queueObject.getSql();
                                        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                                            stmt.execute();
                                            onExecuted(sql);
                                        } catch (SQLException e) {
                                            onExecuteError(sql, e);
                                        }
                                        break;
                                    }
                                }
                                cache().notifyListeners(type, dbo);

                                if (workList.size() > 0) {
                                    queueObject = workList.take();
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
            workerDone(name);
        }
    }

    private class DbErrorWorker extends SwingWorker<Integer, String> {

        volatile boolean keepRunning = true;
        private final String name;

        public DbErrorWorker(String name) {
            this.name = name;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            Thread.currentThread().setName(name);
            while (keepRunning) {
                DbErrorObject error = nonoList.take();
                if (error != null) {
                    switch (error.getQueryType()) {
                        case Select:
                            if (errorListener != null) {
                                errorListener.onSelectError(error.getObject(), error.getException(), error.getSql());
                            }
                            break;
                        case Insert:
                            if (errorListener != null) {
                                errorListener.onInsertError(error.getObject(), error.getException(), error.getSql());
                            }
                            break;
                        case Update:
                            if (errorListener != null) {
                                errorListener.onUpdateError(error.getObject(), error.getException(), error.getSql());
                            }
                            break;
                        case Delete:
                            if (errorListener != null) {
                                errorListener.onDeleteError(error.getObject(), error.getException(), error.getSql());
                            }
                            break;
                        case Custom:
                            if (errorListener != null) {

                            }
                    }
                }
            }
            return 0;
        }

        @Override
        protected void done() {
            workerDone(name);
        }
    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private BasicDataSource getMainDataSource() {
        return mainDataSource;
    }

    public static Connection getConnection() throws SQLException {
        return db().getMainDataSource().getConnection();
    }

    public boolean isInitialized() {
        return initialized;
    }


    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void insert(final DbObject object) {
        object.getAud().setInserted(loggedUser);
        if (!Main.CACHE_ONLY) {
            SwingUtilities.invokeLater(() -> {
                DbQueueObject toInsert = new DbQueueObject(object, Insert);
                try {
                    workList.put(toInsert);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else {
            // Just write it into cache
            object.setId(cacheOnlyFakedId);
            cacheOnlyFakedId++;
            object.tableChanged(Insert);
        }
    }

    public void update(final DbObject object) {
        object.getAud().setUpdated(loggedUser);
        if (!Main.CACHE_ONLY) {
            SwingUtilities.invokeLater(() -> {
                DbQueueObject toUpdate = new DbQueueObject(object, Update);
                try {
                    workList.put(toUpdate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else {
            // Just update into cache
            object.tableChanged(Update);
        }
    }

    public void delete(final DbObject object) {
        if (!Main.CACHE_ONLY) {
            SwingUtilities.invokeLater(() -> {
                DbQueueObject toDelete = new DbQueueObject(object, Delete);
                try {
                    workList.put(toDelete);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else {
            // Just delete
            object.tableChanged(Delete);
        }
    }

    public void execute(final String sql) {
        if (!Main.CACHE_ONLY && sql != null && !sql.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                DbQueueObject dbQueueObject = new DbQueueObject(sql);
                try {
                    workList.put(dbQueueObject);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    /*
     *                  FETCH DATA
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Item> fetchItems() {
        List<Item> items = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return items;
        }
        Status().setMessage("Fetching items from DB");
        Item i = null;
        String sql = scriptResource.readString(Item.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    i = new Item();
                    i.setId(rs.getLong("id"));
                    if (i.getId() > DbObject.UNKNOWN_ID) {
                        i.setName(rs.getString("name"));
                        i.setIconPath(rs.getString("iconPath"));
                        i.setImageId(rs.getLong("imageId"));
                        i.setAlias(rs.getString("alias"));
                        i.setDescription(rs.getString("description"));
                        i.setDivisionId(rs.getLong("divisionId"));
                        i.setLocalDataSheet(rs.getString("localDataSheet"));
                        i.setOnlineDataSheet(rs.getString("onlineDataSheet"));
                        i.setManufacturerId(rs.getLong("manufacturerId"));
                        i.setLocationId(rs.getLong("locationId"));
                        i.setAmount(rs.getInt("amount"));
                        i.setMinimum(rs.getInt("minimum"));
                        i.setMaximum(rs.getInt("maximum"));
                        i.setAmountType(rs.getInt("amountType"));
                        i.setPackageTypeId(rs.getLong("packageTypeId"));
                        i.setPins(rs.getInt("pins"));
                        i.setRating(rs.getFloat("rating"));
                        i.setDiscourageOrder(rs.getBoolean("discourageOrder"));
                        i.setAutoOrder(rs.getBoolean("autoOrder"));
                        i.setValue(rs.getDouble("value"), rs.getInt("multiplier"), rs.getString("unit"));
                        i.setIsSet(rs.getBoolean("isSet"));
                        i.setReplacementItemId(rs.getLong("replacementItemId"));
                        i.setRelatedItemId(rs.getLong("relatedItemId"));
                        i.setAutoOrderById(rs.getLong("autoOrderById")); // MUST BE AFTER setAmount() to disable auto ordering while fetching from DB

                        if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                            i.getAud().setInserted(rs.getString("insertedBy"), rs.getTimestamp("insertedDate"));
                            i.getAud().setUpdated(rs.getString("updatedBy"), rs.getTimestamp("updatedDate"));
                            i.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remark"), i.createRemarksFileName()));
                        } else {
                            i.getAud().setInserted(rs.getString("insertedBy"), DateUtils.sqLiteToDate(rs.getString("insertedDate")));
                            i.getAud().setUpdated(rs.getString("updatedBy"), DateUtils.sqLiteToDate(rs.getString("updatedDate")));
                            i.setRemarksFile(null);
                        }
                        i.setInserted(true);
                        items.add(i);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(i, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return items;
    }

    public List<Division> fetchDivisions() {
        List<Division> divisions = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return divisions;
        }
        Status().setMessage("Fetching Division from DB");
        Division d = null;
        String sql = scriptResource.readString(Division.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    d = new Division();
                    d.setId(rs.getLong("id"));
                    d.setName(rs.getString("name"));
                    d.setIconPath(rs.getString("iconPath"));
                    d.setParentDivisionId(rs.getLong("parentDivisionId"));
                    d.setCanHaveValue(rs.getBoolean("canHaveValue"));
                    d.setDisplayType(rs.getInt("displayType"));
                    d.setInserted(true);

                    divisions.add(d);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(d, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return divisions;
    }

    public List<Manufacturer> fetchManufacturers() {
        List<Manufacturer> manufacturers = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return manufacturers;
        }
        Status().setMessage("Fetching manufacturers from DB");
        Manufacturer m = null;
        String sql = scriptResource.readString(Manufacturer.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    m = new Manufacturer();
                    m.setId(rs.getLong("id"));
                    m.setName(rs.getString("name"));
                    m.setWebsite(rs.getString("website"));
                    m.setIconPath(rs.getString("iconpath"));
                    m.setImageId(rs.getLong("imageId"));

                    m.setInserted(true);
                    manufacturers.add(m);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(m, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return manufacturers;
    }

    public List<Location> fetchLocations() {
        List<Location> locations = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return locations;
        }
        Status().setMessage("Fetching locations from DB");
        Location l = null;
        String sql = scriptResource.readString(Location.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    l = new Location();
                    l.setId(rs.getLong("id"));
                    l.setName(rs.getString("name"));
                    l.setLocationTypeId(rs.getLong("locationTypeId"));
                    l.setRow(rs.getInt("row"));
                    l.setCol(rs.getInt("col"));
                    l.setAlias(rs.getString("alias"));

                    l.setInserted(true);
                    if (l.isUnknown()) {
                        l.setCanBeSaved(false);
                    }
                    locations.add(l);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(l, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return locations;
    }

    public List<LocationType> fetchLocationTypes() {
        List<LocationType> locationTypes = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return locationTypes;
        }
        Status().setMessage("Fetching location types from DB");
        LocationType l = null;
        String sql = scriptResource.readString(LocationType.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    l = new LocationType();
                    l.setId(rs.getLong("id"));
                    l.setName(rs.getString("name"));
                    l.setIconPath(rs.getString("iconPath"));
                    l.setLayoutDefinition(rs.getString("layoutDefinition"));
                    l.setLocationLabelId(rs.getLong("locationLabelId"));

                    l.setInserted(true);
                    locationTypes.add(l);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(l, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return locationTypes;
    }

    public List<ItemOrder> fetchItemOrders() {
        List<ItemOrder> itemOrders = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return itemOrders;
        }
        Status().setMessage("Fetching itemOrders from DB");
        ItemOrder o = null;
        String sql = scriptResource.readString(ItemOrder.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    o = new ItemOrder();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setIconPath(rs.getString("iconPath"));
                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        o.setDateOrdered(rs.getTimestamp("dateOrdered"));
                        o.setDateModified(rs.getTimestamp("dateModified"));
                        o.setDateReceived(rs.getTimestamp("dateReceived"));
                    } else {
                        o.setDateOrdered(DateUtils.sqLiteToDate(rs.getString("dateOrdered")));
                        o.setDateModified(DateUtils.sqLiteToDate(rs.getString("dateModified")));
                        o.setDateReceived(DateUtils.sqLiteToDate(rs.getString("dateReceived")));
                    }
                    o.setDistributorId(rs.getLong("distributorId"));
                    o.setVAT(rs.getDouble("VAT"));
                    o.setOrderReference(rs.getString("orderReference"));
                    o.setTrackingNumber(rs.getString("trackingNumber"));
                    o.setLocked(o.getOrderState() != Statics.OrderStates.Planned);
                    o.setAutoOrder(rs.getBoolean("isAutoOrder"));

                    o.setInserted(true);
                    if (o.getId() != DbObject.UNKNOWN_ID) {
                        itemOrders.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(o, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        itemOrders.add(0, ItemOrder.getUnknownOrder());
        //itemOrders.sort(new ItemOrder.SortAllOrders());

        return itemOrders;
    }

    public List<ItemOrderLine> fetchItemOrderLines() {
        List<ItemOrderLine> itemOrderLines = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return itemOrderLines;
        }
        Status().setMessage("Fetching order lines from DB");
        ItemOrderLine o = null;
        String sql = scriptResource.readString(ItemOrderLine.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    o = new ItemOrderLine();
                    o.setId(rs.getLong("id"));
                    o.setOrderId(rs.getLong("orderId"));
                    o.setAmount(rs.getInt("amount"));
                    o.setLineId(rs.getLong("itemId"));
                    o.setPending(rs.getBoolean("isPending"));
                    o.setCorrectedPrice(rs.getDouble("correctedPrice"), rs.getInt("priceUnits"));

                    o.setInserted(true);
                    if (o.getId() != DbObject.UNKNOWN_ID) {
                        itemOrderLines.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(o, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return itemOrderLines;
    }

    public List<PcbOrder> fetchPcbOrders() {
        List<PcbOrder> pcbOrders = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return pcbOrders;
        }
        Status().setMessage("Fetching PcbOrder from DB");
        PcbOrder o = null;
        String sql = scriptResource.readString(PcbOrder.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    o = new PcbOrder();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setIconPath(rs.getString("iconPath"));
                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        o.setDateOrdered(rs.getTimestamp("dateOrdered"));
                        o.setDateModified(rs.getTimestamp("dateModified"));
                        o.setDateReceived(rs.getTimestamp("dateReceived"));
                    } else {
                        o.setDateOrdered(DateUtils.sqLiteToDate(rs.getString("dateOrdered")));
                        o.setDateModified(DateUtils.sqLiteToDate(rs.getString("dateModified")));
                        o.setDateReceived(DateUtils.sqLiteToDate(rs.getString("dateReceived")));
                    }
                    o.setDistributorId(rs.getLong("distributorId"));
                    o.setVAT(rs.getDouble("VAT"));
                    o.setOrderReference(rs.getString("orderReference"));
                    o.setTrackingNumber(rs.getString("trackingNumber"));
                    o.setLocked(o.getOrderState() != Statics.OrderStates.Planned);
                    o.setAutoOrder(rs.getBoolean("isAutoOrder"));

                    o.setInserted(true);
                    if (o.getId() != DbObject.UNKNOWN_ID) {
                        pcbOrders.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(o, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return pcbOrders;
    }

    public List<PcbOrderLine> fetchPcbOrderLines() {
        List<PcbOrderLine> pcbOrderLines = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return pcbOrderLines;
        }
        Status().setMessage("Fetching PcbOrderLine from DB");
        PcbOrderLine o = null;
        String sql = scriptResource.readString(PcbOrderLine.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    o = new PcbOrderLine();
                    o.setId(rs.getLong("id"));
                    o.setOrderId(rs.getLong("orderId"));
                    o.setAmount(rs.getInt("amount"));
                    o.setLineId(rs.getLong("pcbId"));
                    o.setPending(rs.getBoolean("isPending"));
                    o.setCorrectedPrice(rs.getDouble("correctedPrice"), rs.getInt("priceUnits"));

                    o.setInserted(true);
                    if (o.getId() != DbObject.UNKNOWN_ID) {
                        pcbOrderLines.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(o, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return pcbOrderLines;
    }

    public List<Distributor> fetchDistributors() {
        List<Distributor> distributors = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return distributors;
        }
        Status().setMessage("Fetching distributors from DB");
        Distributor d = null;
        String sql = scriptResource.readString(Distributor.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    d = new Distributor();
                    d.setId(rs.getLong("id"));
                    d.setName(rs.getString("name"));
                    d.setIconPath(rs.getString("iconPath"));
                    d.setImageId(rs.getLong("imageId"));
                    d.setWebsite(rs.getString("website"));
                    d.setOrderLink(rs.getString("orderLink"));
                    d.setOrderFileFormatId(rs.getLong("orderFileFormatId"));
                    d.setDistributorType(rs.getInt("distributorType"));

                    d.setInserted(true);
                    if (d.getId() != DbObject.UNKNOWN_ID) {
                        distributors.add(d);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(d, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return distributors;
    }

    public List<DistributorPartLink> fetchDistributorParts() {
        List<DistributorPartLink> distributorPartLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return distributorPartLinks;
        }
        Status().setMessage("Fetching distributor parts from DB");
        DistributorPartLink pn = null;
        String sql = scriptResource.readString(DistributorPartLink.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    pn = new DistributorPartLink();
                    pn.setId(rs.getLong("id"));
                    pn.setName(rs.getString("name"));
                    pn.setIconPath(rs.getString("iconPath"));
                    pn.setDistributorId(rs.getLong("distributorId"));
                    pn.setItemId(rs.getLong("itemId"));
                    pn.setPcbId(rs.getLong("pcbId"));
                    pn.setReference(rs.getString("reference"));
                    pn.setPrice(rs.getDouble("price"), rs.getInt("priceUnits"));

                    pn.setInserted(true);
                    if (pn.getId() != DbObject.UNKNOWN_ID) {
                        distributorPartLinks.add(pn);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(pn, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return distributorPartLinks;
    }

    public List<DistributorOrderFlow> fetchDistributorOrderFlows() {
        List<DistributorOrderFlow> distributorOrderFlows = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return distributorOrderFlows;
        }
        Status().setMessage("Fetching distributor order flows from DB");
        DistributorOrderFlow dof = null;
        String sql = scriptResource.readString(DistributorOrderFlow.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    dof = new DistributorOrderFlow();
                    dof.setId(rs.getLong("id"));
                    dof.setName(rs.getString("name"));
                    dof.setIconPath(rs.getString("iconPath"));
                    dof.setDistributorId(rs.getLong("distributorId"));
                    dof.setSequenceNumber(rs.getInt("sequenceNumber"));
                    dof.setOrderState(rs.getInt("orderState"));
                    dof.setDescription(rs.getString("description"));
                    dof.setDateEntered(rs.getTimestamp("dateEntered"));

                    dof.setInserted(true);
                    if (dof.getId() != DbObject.UNKNOWN_ID) {
                        distributorOrderFlows.add(dof);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(dof, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return distributorOrderFlows;
    }

    public List<Package> fetchPackages() {
        List<Package> packages = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return packages;
        }
        Status().setMessage("Fetching packages from DB");
        Package pa = null;
        String sql = scriptResource.readString(Package.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    pa = new Package();
                    pa.setId(rs.getLong("id"));
                    pa.setName(rs.getString("name"));
                    pa.setIconPath(rs.getString("iconPath"));
                    pa.setDescription(rs.getString("description"));

                    pa.setInserted(true);
                    packages.add(pa);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(pa, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return packages;
    }

    public List<PackageType> fetchPackageTypes() {
        List<PackageType> packageTypes = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return packageTypes;
        }
        Status().setMessage("Fetching package types from DB");
        PackageType pt = null;
        String sql = scriptResource.readString(PackageType.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    pt = new PackageType();
                    pt.setId(rs.getLong("id"));
                    pt.setName(rs.getString("name"));
                    pt.setIconPath(rs.getString("iconPath"));
                    pt.setPackageId(rs.getLong("packageId"));
                    pt.setDefaultPins(rs.getInt("defaultPins"));
                    pt.setAllowOtherPinNumbers(rs.getBoolean("allowOtherPinNumbers"));
                    pt.setDescription(rs.getString("description"));

                    pt.setInserted(true);
                    packageTypes.add(pt);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(pt, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return packageTypes;
    }

    public List<Project> fetchProjects() {
        List<Project> projects = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return projects;
        }
        Status().setMessage("Fetching projects from DB");
        Project p = null;
        String sql = scriptResource.readString(Project.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new Project();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconPath"));
                    p.setImageId(rs.getLong("imageId"));
                    p.setMainDirectory(rs.getString("mainDirectory"));

                    p.setInserted(true);
                    if (p.getId() != DbObject.UNKNOWN_ID) {
                        projects.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return projects;
    }

    public List<ProjectCode> fetchProjectCodes() {
        List<ProjectCode> projectCodes = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return projectCodes;
        }
        Status().setMessage("Fetching ProjectCode from DB");
        ProjectCode p = null;
        String sql = scriptResource.readString(ProjectCode.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new ProjectCode();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));
                    p.setLanguage(rs.getString("language"));
                    p.setDirectory(rs.getString("directory"));
                    p.setProjectId(rs.getLong("projectId"));
                    p.setProjectIDEId(rs.getLong("projectIDEId"));
                    p.setDescription(rs.getString("description"));

                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        p.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remarks"), p.createRemarksFileName()));
                    } else {
                        p.setRemarksFile(null);
                    }

                    p.setInserted(true);
                    projectCodes.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return projectCodes;
    }

    public List<ProjectPcb> fetchProjectPcbs() {
        List<ProjectPcb> projectPcbs = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return projectPcbs;
        }
        Status().setMessage("Fetching ProjectPcb from DB");
        ProjectPcb p = null;
        String sql = scriptResource.readString(ProjectPcb.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new ProjectPcb();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));
                    p.setDirectory(rs.getString("directory"));
                    p.setProjectId(rs.getLong("projectId"));
                    p.setProjectIDEId(rs.getLong("projectIDEId"));
                    p.setDescription(rs.getString("description"));

                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        p.setLastParsedDate(rs.getTimestamp("lastParsedDate"));
                        p.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remarks"), p.createRemarksFileName()));
                    } else {
                        p.setLastParsedDate(DateUtils.sqLiteToDate(rs.getString("lastParsedDate")));
                        p.setRemarksFile(null);
                    }

                    p.setInserted(true);
                    projectPcbs.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            projectPcbs = null;
        }
        return projectPcbs;
    }

    public List<ProjectOther> fetchProjectOthers() {
        List<ProjectOther> projectOthers = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return projectOthers;
        }
        Status().setMessage("Fetching ProjectOther from DB");
        ProjectOther p = null;
        String sql = scriptResource.readString(ProjectOther.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new ProjectOther();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));
                    p.setDirectory(rs.getString("directory"));
                    p.setProjectId(rs.getLong("projectId"));
                    p.setProjectIDEId(rs.getLong("projectIDEId"));
                    p.setDescription(rs.getString("description"));

                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        p.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remarks"), p.createRemarksFileName()));
                    } else {
                        p.setRemarksFile(null);
                    }

                    p.setInserted(true);
                    projectOthers.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            projectOthers = null;
        }
        return projectOthers;
    }

    public List<PcbItemProjectLink> fetchPcbItemLinks() {
        List<PcbItemProjectLink> pcbItemProjectLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return pcbItemProjectLinks;
        }
        Status().setMessage("Fetching PcbItemProjectLink from DB");
        PcbItemProjectLink p = null;
        String sql = scriptResource.readString(PcbItemProjectLink.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new PcbItemProjectLink();
                    p.setId(rs.getLong("id"));
                    p.setPcbItemId(rs.getLong("pcbItemId"));
                    p.setProjectPcbId(rs.getLong("projectPcbId"));
                    p.setPcbItemItemLinkId(rs.getLong("pcbItemItemLinkId"));
                    p.setValue(rs.getString("value"));
                    p.setPcbItemReferences(rs.getString("pcbItemReferences"));
                    p.setPcbSheetName(rs.getString("pcbSheetName"));

                    p.setInserted(true);
                    pcbItemProjectLinks.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            pcbItemProjectLinks = null;
        }

        return pcbItemProjectLinks;
    }

    public List<CreatedPcb> fetchCreatedPcbs() {
        List<CreatedPcb> createdPcbs = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return createdPcbs;
        }
        Status().setMessage("Fetching CreatedPcbs from DB");
        CreatedPcb p = null;
        String sql = scriptResource.readString(CreatedPcb.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new CreatedPcb();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconPath"));
                    p.setProjectPcbId(rs.getLong("projectPcbId"));
                    p.setOrderId(rs.getLong("orderId"));
                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        p.setDateCreated(rs.getTimestamp("dateCreated"));
                    } else {
                        p.setDateCreated(DateUtils.sqLiteToDate(rs.getString("dateCreated")));
                    }
                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        p.setDateSoldered(rs.getTimestamp("dateSoldered"));
                    } else {
                        p.setDateSoldered(DateUtils.sqLiteToDate(rs.getString("dateSoldered")));
                    }
                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        p.setDateDestroyed(rs.getTimestamp("dateDestroyed"));
                    } else {
                        p.setDateDestroyed(DateUtils.sqLiteToDate(rs.getString("dateDestroyed")));
                    }

                    p.setInserted(true);
                    createdPcbs.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            createdPcbs = null;
        }

        return createdPcbs;
    }

    public List<CreatedPcbLink> fetchCreatedPcbLinks() {
        List<CreatedPcbLink> createdPcbLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return createdPcbLinks;
        }
        Status().setMessage("Fetching CreatedPcbLinks from DB");
        CreatedPcbLink p = null;
        String sql = scriptResource.readString(CreatedPcbLink.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new CreatedPcbLink();
                    p.setId(rs.getLong("id"));

                    p.setPcbItemProjectLinkId(rs.getLong("pcbItemProjectLinkId"));
                    p.setCreatedPcbId(rs.getLong("createdPcbId"));

                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        p.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remarks"), p.createRemarksFileName()));
                    } else {
                        p.setRemarksFile(null);
                    }

                    p.setInserted(true);
                    createdPcbLinks.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            createdPcbLinks = null;
        }

        return createdPcbLinks;
    }

    public List<SolderItem> fetchSolderItems() {
        List<SolderItem> solderItems = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return solderItems;
        }
        Status().setMessage("Fetching SolderItem from DB");
        SolderItem si = null;
        String sql = scriptResource.readString(SolderItem.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    si = new SolderItem();
                    si.setId(rs.getLong("id"));
                    si.setName(rs.getString("name"));

                    si.setCreatedPcbLinkId(rs.getLong("createdPcbLinkId"));
                    si.setUsedItemId(rs.getLong("usedItemId"));
                    si.setState(rs.getInt("state"));
                    si.setNumTimesSoldered(rs.getInt("numTimesSoldered"));
                    si.setNumTimesDesoldered(rs.getInt("numTimesDesoldered"));

                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        si.setSolderDate(rs.getTimestamp("solderDate"));
                    } else {
                        si.setSolderDate(DateUtils.sqLiteToDate(rs.getString("solderDate")));
                    }

                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        si.setDesolderDate(rs.getTimestamp("desolderDate"));
                    } else {
                        si.setDesolderDate(DateUtils.sqLiteToDate(rs.getString("desolderDate")));
                    }

                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        si.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remarks"), si.createRemarksFileName()));
                    } else {
                        si.setRemarksFile(null);
                    }

                    si.setInserted(true);
                    solderItems.add(si);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(si, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            solderItems = null;
        }

        return solderItems;
    }

    public List<ProjectIDE> fetchProjectIDEs() {
        List<ProjectIDE> projectIDES = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return projectIDES;
        }
        Status().setMessage("Fetching ProjectIDE from DB");
        ProjectIDE p = null;
        String sql = scriptResource.readString(ProjectIDE.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new ProjectIDE();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));
                    p.setImageId(rs.getLong("imageId"));
                    p.setProjectType(rs.getString("projectType"));
                    p.setOpenAsFolder(rs.getBoolean("openasfolder"));
                    p.setUseDefaultLauncher(rs.getBoolean("usedefaultlauncher"));
                    p.setLauncherPath(rs.getString("launcherpath"));
                    p.setExtension(rs.getString("extension"));
                    p.setMatchExtension(rs.getBoolean("matchextension"));
                    p.setUseParentFolder(rs.getBoolean("useparentfolder"));
                    p.setParserName(rs.getString("parsername"));

                    p.setInserted(true);
                    if (p.getId() != DbObject.UNKNOWN_ID) {
                        projectIDES.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return projectIDES;
    }

    public List<ParserItemLink> fetchParserItemLinks() {
        List<ParserItemLink> parserItemLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return parserItemLinks;
        }
        Status().setMessage("Fetching parser item links from DB");
        ParserItemLink p = null;
        String sql = scriptResource.readString(ParserItemLink.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new ParserItemLink();
                    p.setId(rs.getLong("id"));
                    p.setParserName(rs.getString("parserName"));
                    p.setPcbItemName(rs.getString("pcbItemName"));
                    p.setDivisionId(rs.getLong("divisionId"));

                    p.setInserted(true);
                    parserItemLinks.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return parserItemLinks;
    }

    public List<OrderFileFormat> fetchOrderFileFormats() {
        List<OrderFileFormat> orderFileFormats = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return orderFileFormats;
        }
        Status().setMessage("Fetching order file formats from DB");
        OrderFileFormat off = null;
        String sql = scriptResource.readString(OrderFileFormat.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    off = new OrderFileFormat();
                    off.setId(rs.getLong("id"));
                    off.setName(rs.getString("name"));
                    off.setSeparator(rs.getString("separator"));

                    off.setInserted(true);
                    orderFileFormats.add(off);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(off, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return orderFileFormats;
    }

    public List<PcbItem> fetchPcbItems() {
        List<PcbItem> pcbItems = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return pcbItems;
        }
        Status().setMessage("Fetching pcb items from DB");
        PcbItem pcbItem = null;
        String sql = scriptResource.readString(PcbItem.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    pcbItem = new PcbItem();
                    pcbItem.setId(rs.getLong("id"));
                    pcbItem.setFootprint(rs.getString("footprint"));
                    pcbItem.setLibrary(rs.getString("lib"));
                    pcbItem.setPartName(rs.getString("part"));

                    pcbItem.setInserted(true);
                    pcbItems.add(pcbItem);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(pcbItem, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return pcbItems;
    }

    public List<PcbItemItemLink> fetchKcItemLinks() {
        List<PcbItemItemLink> pcbItemItemLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return pcbItemItemLinks;
        }
        Status().setMessage("Fetching KcItemLinks from DB");
        PcbItemItemLink kil = null;
        String sql = scriptResource.readString(PcbItemItemLink.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    kil = new PcbItemItemLink();
                    kil.setId(rs.getLong("id"));
                    kil.setItemId(rs.getLong("itemId"));
                    kil.setMatch(rs.getByte("componentMatch"));
                    kil.setPcbItemId(rs.getLong("pcbItemId"));

                    kil.setInserted(true);
                    pcbItemItemLinks.add(kil);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(kil, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return pcbItemItemLinks;
    }

    public List<Log> fetchLogs() {
        List<Log> logs = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return logs;
        }
        Status().setMessage("Fetching logs from DB");
        Log l = null;
        String sql = scriptResource.readString(Log.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    l = new Log();
                    l.setId(rs.getLong("id"));
                    l.setLogType(rs.getInt("logtype"));
                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        l.setLogTime(rs.getTimestamp("logtime"));
                    } else {
                        l.setLogTime(DateUtils.sqLiteToDate(rs.getString("logtime")));
                    }
                    l.setLogClass(rs.getString("logclass"));
                    l.setLogMessage(rs.getString("logmessage"));
                    l.setLogException(rs.getString("logexception"));

                    l.setInserted(true);
                    logs.add(l);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(l, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return logs;
    }

    public List<DbHistory> fetchDbHistoryList() {
        List<DbHistory> dbHistoryList = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return dbHistoryList;
        }
        Status().setMessage("Fetching db history from DB");
        DbHistory dbh = null;
        String sql = scriptResource.readString(DbHistory.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    dbh = new DbHistory();
                    dbh.setId(rs.getLong("id"));
                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        dbh.setDate(rs.getTimestamp("date"));
                    } else {
                        dbh.setDate(DateUtils.sqLiteToDate(rs.getString("date")));
                    }
                    dbh.setDbQueryType(rs.getInt("dbAction"));
                    dbh.setDbObjectType(rs.getInt("dbObjectType"));
                    dbh.setDbObjectId(rs.getLong("dbObjectId"));

                    dbh.setInserted(true);
                    dbHistoryList.add(dbh);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(dbh, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return dbHistoryList;
    }

    public List<Set> fetchSets() {
        List<Set> sets = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return sets;
        }
        Status().setMessage("Fetching sets from DB");
        Set s = null;
        String sql = "SELECT * FROM items WHERE isSet = 1";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    s = new Set();
                    s.setId(rs.getLong("id"));
                    s.setName(rs.getString("name"));
                    s.setIconPath(rs.getString("iconPath"));
                    s.setAlias(rs.getString("alias"));
                    s.setDescription(rs.getString("description"));
                    s.setDivisionId(rs.getLong("divisionId"));
                    s.setLocalDataSheet(rs.getString("localDataSheet"));
                    s.setOnlineDataSheet(rs.getString("onlineDataSheet"));
                    s.setManufacturerId(rs.getLong("manufacturerId"));
                    s.setLocationId(rs.getLong("locationId"));
                    s.setAmount(rs.getInt("amount"));
                    s.setAmountType(rs.getInt("amountType"));
                    //s.setOrderState(rs.getInt("orderState"));
                    s.setPackageTypeId(rs.getLong("packageTypeId"));
                    s.setPins(rs.getInt("pins"));
                    s.setRating(rs.getFloat("rating"));
                    s.setDiscourageOrder(rs.getBoolean("discourageOrder"));
                    s.setValue(rs.getDouble("value"), rs.getInt("multiplier"), rs.getString("unit"));
                    s.setIsSet(true);

                    if (settings().getDbSettings().getDbType().equals(Statics.DbTypes.Online)) {
                        s.getAud().setInserted(rs.getString("insertedBy"), rs.getTimestamp("insertedDate"));
                        s.getAud().setUpdated(rs.getString("updatedBy"), rs.getTimestamp("updatedDate"));
                        s.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remark"), s.createRemarksFileName()));
                    } else {
                        s.getAud().setInserted(rs.getString("insertedBy"), DateUtils.sqLiteToDate(rs.getString("insertedDate")));
                        s.getAud().setUpdated(rs.getString("updatedBy"), DateUtils.sqLiteToDate(rs.getString("updatedDate")));
                        s.setRemarksFile(null);
                    }
                    s.setInserted(true);
                    sets.add(s);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(s, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return sets;
    }

    public List<SetItemLink> fetchSetItemLinks() {
        List<SetItemLink> setItemLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return setItemLinks;
        }
        Status().setMessage("Fetching set item links from DB");
        SetItemLink s = null;
        String sql = scriptResource.readString(SetItemLink.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    s = new SetItemLink();
                    s.setId(rs.getLong("id"));

                    s.setSetId(rs.getLong("setId"));
                    s.setItemId(rs.getLong("itemId"));

                    s.setInserted(true);
                    setItemLinks.add(s);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(s, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return setItemLinks;
    }

    public List<DbEvent> fetchDbEvents() {
        List<DbEvent> dbEvents = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return dbEvents;
        }
        Status().setMessage("Fetching events from DB");
        DbEvent e;
        String sql = scriptResource.readString("dbevents.sqlSelect.all");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    e = new DbEvent();
                    e.setName(rs.getString("EVENT_NAME"));
                    e.setDefiner(rs.getString("DEFINER"));
                    e.setDefinition(rs.getString("EVENT_DEFINITION"));
                    e.setType(rs.getString("EVENT_TYPE"));
                    e.setExecuteAt(rs.getTimestamp("EXECUTE_AT"));
                    e.setIntervalValue(rs.getInt("INTERVAL_VALUE"));
                    e.setIntervalField(rs.getString("INTERVAL_FIELD"));
                    e.setIntervalStarts(rs.getTimestamp("STARTS"));
                    e.setIntervalEnds(rs.getTimestamp("ENDS"));
                    e.setEnabled(rs.getString("STATUS").equals("ENABLED"));
                    e.setCreated(rs.getTimestamp("CREATED"));
                    e.setAltered(rs.getTimestamp("LAST_ALTERED"));
                    e.setLastExecuted(rs.getTimestamp("LAST_EXECUTED"));
                    e.setComment(rs.getString("EVENT_COMMENT"));

                    dbEvents.add(e);
                }
            }
        } catch (SQLException ex) {
            DbErrorObject object = new DbErrorObject(null, ex, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return dbEvents;
    }

    public List<Statistics> fetchStatistics() {
        List<Statistics> statistics = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return statistics;
        }
        Status().setMessage("Fetching Statistics from DB");
        Statistics s = null;
        String sql = scriptResource.readString(Statistics.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    s = new Statistics(
                            rs.getTimestamp("creationTime"),
                            rs.getInt("itemsCount"),
                            rs.getInt("locationsCount"),
                            rs.getInt("manufacturersCount"),
                            rs.getInt("distributorsCount"),
                            rs.getInt("packagesCount"),
                            rs.getInt("ordersCount"),
                            rs.getInt("projectsCount")
                    );
                    s.setId(rs.getLong("id"));

                    s.setInserted(true);
                    statistics.add(s);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(s, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return statistics;
    }

    public List<PendingOrder> fetchPendingOrders() {
        List<PendingOrder> pendingOrders = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return pendingOrders;
        }
        Status().setMessage("Fetching Pending orders from DB");
        PendingOrder p = null;
        String sql = scriptResource.readString(PendingOrder.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new PendingOrder();
                    p.setId(rs.getLong("id"));
                    p.setOriginalOrderId(rs.getLong("originalOrderId"));
                    p.setInserted(true);

                    pendingOrders.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return pendingOrders;
    }

    public List<LocationLabel> fetchLocationLabels() {
        List<LocationLabel> locationLabels = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return locationLabels;
        }
        Status().setMessage("Fetching LocationLabel orders from DB");
        LocationLabel ll = null;
        String sql = scriptResource.readString(LocationLabel.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    ll = new LocationLabel();
                    ll.setId(rs.getLong("id"));
                    ll.setName(rs.getString("name"));
                    ll.setIconPath(rs.getString("iconPath"));
                    ll.setImageId(rs.getLong("imageId"));
                    //

                    ll.setInserted(true);

                    locationLabels.add(ll);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(ll, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return locationLabels;
    }


    public List<LabelAnnotation> fetchLabelAnnotations() {
        List<LabelAnnotation> labelAnnotations = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return labelAnnotations;
        }
        Status().setMessage("Fetching LabelAnnotation orders from DB");
        LabelAnnotation la = null;
        String sql = scriptResource.readString(LabelAnnotation.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    la = new LabelAnnotation(rs.getLong("locationLabelId"));
                    la.setId(rs.getLong("id"));
                    la.setName(rs.getString("name"));

                    la.setType(rs.getInt("type"));
                    la.setLink(rs.getInt("link"));
                    la.setStartX(rs.getDouble("startX"));
                    la.setStartY(rs.getDouble("startY"));

                    la.setText(rs.getString("text"));
                    la.setTextFontName(rs.getString("textFontName"));
                    la.setTextFontSize(rs.getInt("textFontSize"));

                    la.setImagePath(rs.getString("imagePath"));
                    la.setImageH(rs.getDouble("imageH"));
                    la.setImageW(rs.getDouble("imageW"));

                    la.setInserted(true);

                    labelAnnotations.add(la);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(la, e, Select, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        return labelAnnotations;
    }
}
