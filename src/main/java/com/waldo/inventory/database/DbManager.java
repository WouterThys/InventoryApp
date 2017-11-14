package com.waldo.inventory.database;

import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.classes.Package;
import com.waldo.inventory.database.classes.DbErrorObject;
import com.waldo.inventory.database.classes.DbQueue;
import com.waldo.inventory.database.classes.DbQueueObject;
import com.waldo.inventory.database.interfaces.DbErrorListener;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.managers.LogManager;
import org.apache.commons.dbcp.BasicDataSource;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.scriptResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class DbManager {

    private static final LogManager LOG = LogManager.LOG(DbManager.class);

    public static final int OBJECT_INSERT = 0;
    public static final int OBJECT_UPDATE = 1;
    public static final int OBJECT_DELETE = 2;
    public static final int OBJECT_SELECT = 3;
    public static final int OBJECT_CACHE_CLEAR = 4;

    private static final String QUEUE_WORKER = "Queue worker";
    private static final String ERROR_WORKER = "Error worker";

    // Db
    private static final DbManager INSTANCE = new DbManager();
    public static DbManager db() {
        return INSTANCE;
    }
    private BasicDataSource dataSource;//MysqlDataSource dataSource;//BasicDataSource dataSource;
    private List<String> tableNames;
    private boolean initialized = false;
    private String loggedUser = "";
    private long cacheOnlyFakedId = 2;

    private DbQueue<DbQueueObject> workList;
    private DbQueue<DbErrorObject> nonoList;
    private DbQueueWorker dbQueueWorker;
    private DbErrorWorker dbErrorWorker;

    // Events
    private DbErrorListener errorListener;

    public List<DbObjectChangedListener<Item>> onItemsChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Category>> onCategoriesChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Product>> onProductsChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Type>> onTypesChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Manufacturer>> onManufacturerChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Order>> onOrdersChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Location>> onLocationsChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<LocationType>> onLocationTYpeChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<OrderItem>> onOrderItemsChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Distributor>> onDistributorsChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<DistributorPartLink>> onPartNumbersChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<PackageType>> onPackageTypesChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Project>> onProjectChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<ProjectIDE>> onProjectIDEChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<OrderFileFormat>> onOrderFileFormatChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<Package>> onPackageChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<SetItem>> onSetItemChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<PcbItem>> onPcbItemChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<PcbItemItemLink>> onPcbItemItemLinkChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<ProjectCode>> onProjectCodeChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<ProjectPcb>> onProjectPcbChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<ProjectOther>> onProjectOtherChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<PcbItemProjectLink>> onPcbItemProjectLinkChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<ParserItemLink>> onParserItemLinkChangedListenerList = new ArrayList<>();
    public List<DbObjectChangedListener<DistributorPartLink>> onDistributorPartLinkChangedListenerList = new ArrayList<>();

    // Part numbers...

    // Cached lists
    private List<Item> items;
    private List<Category> categories;
    private List<Product> products;
    private List<Type> types;
    private List<Manufacturer> manufacturers;
    private List<Location> locations;
    private List<LocationType> locationTypes;
    private List<Order> orders;
    private List<OrderItem> orderItems;
    private List<Distributor> distributors;
    private List<DistributorPartLink> distributorPartLinks;
    private List<PackageType> packageTypes;
    private List<Project> projects;
    private List<ProjectIDE> projectIDES;
    private List<OrderFileFormat> orderFileFormats;
    private List<Package> packages;
    private List<SetItem> setItems;
    private List<PcbItem> pcbItems;
    private List<PcbItemItemLink> pcbItemItemLinks;
    private List<PcbItemProjectLink> pcbItemProjectLinks;
    private List<Log> logs;
    private List<DbHistory> dbHistoryList;
    private List<ProjectCode> projectCodes;
    private List<ProjectPcb> projectPcbs;
    private List<ProjectOther> projectOthers;
    private List<ParserItemLink> parserItemLinks;

    private DbManager() {}

    public void init() throws SQLException{
        initialized = false;
        DbSettings s = settings().getDbSettings();
        if (s != null) {
            loggedUser = s.getDbUserName();
            if (!Main.CACHE_ONLY) {
                switch (s.getDbType()) {
                    case Statics.DbTypes.Online:
                        initMySql(s);
                        break;
                    case Statics.DbTypes.Local:
                        initSqLite(s);
                        break;
                }

                // Test
                initialized = testConnection(dataSource);
                Status().setDbConnectionText(initialized, s.getDbIp(), s.getDbName(), s.getDbUserName());
            } else {
                Status().setDbConnectionText(false, "", "", "");
            }
        }
    }

    private void initMySql(DbSettings settings) {
        dataSource = new BasicDataSource(); // new MySqlDataSource
        dataSource.setUrl(settings.createMySqlUrl() + "?zeroDateTimeBehavior=convertToNull&connectTimeout=5000&socketTimeout=30000");
        dataSource.setUsername(settings.getDbUserName());
        dataSource.setPassword(settings.getDbUserPw());
        LOG.info("Database initialized with connection: " + settings.createMySqlUrl());
    }

    private void initSqLite(DbSettings settings) {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName("net.sf.log4jdbc.DriverSpy");
            dataSource.setUrl("jdbc:log4jdbc:sqlite:" + settings.getDbName());
            dataSource.setUsername(settings.getDbUserName());
            dataSource.setPassword("");
            dataSource.setMaxIdle(10);
            dataSource.setMaxActive(10);
            dataSource.setPoolPreparedStatements(true);
            dataSource.setLogAbandoned(false);
            dataSource.setRemoveAbandoned(true);
            dataSource.setInitialSize(5);
            dataSource.setRemoveAbandonedTimeout(60);

                String sql = "PRAGMA foreign_keys=ON;";
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
                initialized = testConnection(dataSource);
                Status().setDbConnectionText(initialized, s.getDbIp(), s.getDbName(), s.getDbUserName());
                if (initialized) {
                    clearCache();
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
                    //String schemaName = dataSource.get();
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

    private void clearCache() {
        items = null; notifyListeners(OBJECT_CACHE_CLEAR, null, onItemsChangedListenerList);
        categories = null; notifyListeners(OBJECT_CACHE_CLEAR, null, onCategoriesChangedListenerList);
        products = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onProductsChangedListenerList);
        types = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onTypesChangedListenerList);
        manufacturers = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onManufacturerChangedListenerList);
        locations = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onLocationsChangedListenerList);
        locationTypes = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onLocationTYpeChangedListenerList);
        orders = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onOrdersChangedListenerList);
        orderItems = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onOrderItemsChangedListenerList);
        distributors = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onDistributorsChangedListenerList);
        distributorPartLinks = null;
        packageTypes = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onPackageTypesChangedListenerList);
        projects = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onProjectChangedListenerList);
        projectIDES = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onProjectIDEChangedListenerList);
        orderFileFormats = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onOrderFileFormatChangedListenerList);
        packages = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onPackageChangedListenerList);
        setItems = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onSetItemChangedListenerList);
        pcbItems = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onPcbItemChangedListenerList);
        pcbItemItemLinks = null;notifyListeners(OBJECT_CACHE_CLEAR, null, onPcbItemItemLinkChangedListenerList);
        logs = null;
    }

    public void close() {
        if(dataSource != null) {
            Status().setMessage("Closing down");
            if(dbQueueWorker != null) {
                dbQueueWorker.keepRunning = false;
                workList.stop();
            }
            if (dbErrorWorker != null) {
                dbErrorWorker.keepRunning = false;
                nonoList.stop();
            }
        }
    }

    private void workerDone(String workerName) {
        System.out.println(workerName + " :thread done");
//        try {
//            if (dataSource != null) {
//                dataSource.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    /*
     *                  GETTERS - SETTERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private BasicDataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return db().getDataSource().getConnection();
    }

    public List<String> getTableNames() throws SQLException {
        if (tableNames == null) {
            tableNames = new ArrayList<>();

            String sql = "SELECT * FROM main.sqlite_master WHERE type='table'";
            try (Connection connection = getConnection()) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        tableNames.add(rs.getString("name"));
                    }
                }
            }
        }
        return tableNames;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void addOnItemsChangedListener(DbObjectChangedListener<Item> dbObjectChangedListener) {
        if (!onItemsChangedListenerList.contains(dbObjectChangedListener)) {
            onItemsChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnCategoriesChangedListener(DbObjectChangedListener<Category> dbObjectChangedListener) {
        if (!onCategoriesChangedListenerList.contains(dbObjectChangedListener)) {
            onCategoriesChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProductsChangedListener(DbObjectChangedListener<Product> dbObjectChangedListener) {
        if (!onProductsChangedListenerList.contains(dbObjectChangedListener)) {
            onProductsChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnTypesChangedListener(DbObjectChangedListener<Type> dbObjectChangedListener) {
        if (!onTypesChangedListenerList.contains(dbObjectChangedListener)) {
            onTypesChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnManufacturerChangedListener(DbObjectChangedListener<Manufacturer> dbObjectChangedListener) {
        if (!onManufacturerChangedListenerList.contains(dbObjectChangedListener)) {
            onManufacturerChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnLocationsChangedListener(DbObjectChangedListener<Location> dbObjectChangedListener) {
        if (!onLocationsChangedListenerList.contains(dbObjectChangedListener)) {
            onLocationsChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnLocationTypeChangedListener(DbObjectChangedListener<LocationType> dbObjectChangedListener) {
        if (!onLocationTYpeChangedListenerList.contains(dbObjectChangedListener)) {
            onLocationTYpeChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnOrdersChangedListener(DbObjectChangedListener<Order> dbObjectChangedListener) {
        if (!onOrdersChangedListenerList.contains(dbObjectChangedListener)) {
            onOrdersChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnOrderItemsChangedListener(DbObjectChangedListener<OrderItem> dbObjectChangedListener) {
        if (!onOrderItemsChangedListenerList.contains(dbObjectChangedListener)) {
            onOrderItemsChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnDistributorChangedListener(DbObjectChangedListener<Distributor> dbObjectChangedListener) {
        if (!onDistributorsChangedListenerList.contains(dbObjectChangedListener)) {
            onDistributorsChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnPartNumbersChangedListener(DbObjectChangedListener<DistributorPartLink> dbObjectChangedListener) {
        if (!onPartNumbersChangedListenerList.contains(dbObjectChangedListener)) {
            onPartNumbersChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProjectChangedListener(DbObjectChangedListener<Project> dbObjectChangedListener) {
        if (!onProjectChangedListenerList.contains(dbObjectChangedListener)) {
            onProjectChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnPackageChangedListener(DbObjectChangedListener<Package> dbObjectChangedListener) {
        if (!onPackageChangedListenerList.contains(dbObjectChangedListener)) {
            onPackageChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnPackageTypeChangedListener(DbObjectChangedListener<PackageType> dbObjectChangedListener) {
        if (!onPackageTypesChangedListenerList.contains(dbObjectChangedListener)) {
            onPackageTypesChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProjectTypeChangedListener(DbObjectChangedListener<ProjectIDE> dbObjectChangedListener) {
        if (!onProjectIDEChangedListenerList.contains(dbObjectChangedListener)) {
            onProjectIDEChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnOrderFileFormatChangedListener(DbObjectChangedListener<OrderFileFormat> dbObjectChangedListener) {
        if (!onOrderFileFormatChangedListenerList.contains(dbObjectChangedListener)) {
            onOrderFileFormatChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnKcComponentChangedListener(DbObjectChangedListener<PcbItem> dbObjectChangedListener) {
        if (!onPcbItemChangedListenerList.contains(dbObjectChangedListener)) {
            onPcbItemChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnPcbItemItemLinkChangedListener(DbObjectChangedListener<PcbItemItemLink> dbObjectChangedListener) {
        if (!onPcbItemItemLinkChangedListenerList.contains(dbObjectChangedListener)) {
            onPcbItemItemLinkChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProjectCodeChangedListener(DbObjectChangedListener<ProjectCode> dbObjectChangedListener) {
        if (!onProjectCodeChangedListenerList.contains(dbObjectChangedListener)) {
            onProjectCodeChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProjectPcbChangedListener(DbObjectChangedListener<ProjectPcb> dbObjectChangedListener) {
        if (!onProjectPcbChangedListenerList.contains(dbObjectChangedListener)) {
            onProjectPcbChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnPcbItemLinkChangedListener(DbObjectChangedListener<PcbItemProjectLink> dbObjectChangedListener) {
        if (!onPcbItemProjectLinkChangedListenerList.contains(dbObjectChangedListener)) {
            onPcbItemProjectLinkChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProjectOtherChangedListener(DbObjectChangedListener<ProjectOther> dbObjectChangedListener) {
        if (!onProjectOtherChangedListenerList.contains(dbObjectChangedListener)) {
            onProjectOtherChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnParserItemLinkChangedListener(DbObjectChangedListener<ParserItemLink> dbObjectChangedListener) {
        if (!onParserItemLinkChangedListenerList.contains(dbObjectChangedListener)) {
            onParserItemLinkChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnDistributorPartLinkChangedListener(DbObjectChangedListener<DistributorPartLink> dbObjectChangedListener) {
        if (!onDistributorPartLinkChangedListenerList.contains(dbObjectChangedListener)) {
            onDistributorPartLinkChangedListenerList.add(dbObjectChangedListener);
        }
    }


    public void removeOnCategoriesChangedListener(DbObjectChangedListener<Category> dbObjectChangedListener) {
        if (onCategoriesChangedListenerList != null) {
            if (onCategoriesChangedListenerList.contains(dbObjectChangedListener)) {
                onCategoriesChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnProductsChangedListener(DbObjectChangedListener<Product> dbObjectChangedListener) {
        if (onProductsChangedListenerList != null) {
            if (onProductsChangedListenerList.contains(dbObjectChangedListener)) {
                onProductsChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnTypesChangedListener(DbObjectChangedListener<Type> dbObjectChangedListener) {
        if (onTypesChangedListenerList != null) {
            if (onTypesChangedListenerList.contains(dbObjectChangedListener)) {
                onTypesChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnOrdersChangedListener(DbObjectChangedListener<Order> dbObjectChangedListener) {
        if (onOrdersChangedListenerList != null) {
            if (onOrdersChangedListenerList.contains(dbObjectChangedListener)) {
                onOrdersChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }



    public <T extends DbObject> void notifyListeners(int changedHow, T object, List<DbObjectChangedListener<T>> listeners) {
        for (DbObjectChangedListener<T> l : listeners) {
            switch (changedHow) {
                case OBJECT_INSERT:
                    try {
                        SwingUtilities.invokeLater(() -> l.onInserted(object));
                    } catch (Exception e) {
                        LOG.error("Error after insert of " + object.getName(), e);
                    }
                    break;
                case OBJECT_UPDATE:
                    try {
                        SwingUtilities.invokeLater(() -> l.onUpdated(object));
                    } catch (Exception e) {
                        LOG.error("Error after update of " + object.getName(), e);
                    }
                    break;
                case OBJECT_DELETE:
                    try {
                        SwingUtilities.invokeLater(() -> l.onDeleted(object));
                    } catch (Exception e) {
                        LOG.error("Error after delete of " + object.getName(), e);
                    }
                    break;
                case OBJECT_CACHE_CLEAR:
                    try {
                        SwingUtilities.invokeLater(l::onCacheCleared);
                    } catch (Exception e) {
                        LOG.error("Error after clearing cache", e);
                    }
            }
        }
    }

    public void insert(DbObject object) {
        object.getAud().setInserted(loggedUser);
        if (!Main.CACHE_ONLY) {
            DbQueueObject toInsert = new DbQueueObject(object, OBJECT_INSERT);
            try {
                workList.put(toInsert);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Just write it into cache
            object.setId(cacheOnlyFakedId);
            cacheOnlyFakedId++;
            object.tableChanged(OBJECT_INSERT);
        }
    }

    public void update(DbObject object) {
        object.getAud().setUpdated(loggedUser);
        if (!Main.CACHE_ONLY) {
            DbQueueObject toUpdate = new DbQueueObject(object, OBJECT_UPDATE);
            try {
                workList.put(toUpdate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Just update into cache
            object.tableChanged(OBJECT_UPDATE);
        }
    }

    public void delete(DbObject object) {
        if (!Main.CACHE_ONLY) {
            DbQueueObject toDelete = new DbQueueObject(object, OBJECT_DELETE);
            try {
                workList.put(toDelete);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Just delete
            object.tableChanged(OBJECT_DELETE);
        }
    }


    /*
    *                  ITEMS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Item> getItems() {
        if (items == null) {
            updateItems();
        }
        return items;
    }

    private void updateItems() {
        items = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    i.setName(rs.getString("name"));
                    i.setIconPath(rs.getString("iconPath"));
                    i.setDescription(rs.getString("description"));
                    i.setPrice(rs.getDouble("price"));
                    i.setCategoryId(rs.getInt("categoryId"));
                    i.setProductId(rs.getInt("productId"));
                    i.setTypeId(rs.getInt("typeId"));
                    i.setLocalDataSheet(rs.getString("localDataSheet"));
                    i.setOnlineDataSheet(rs.getString("onlineDataSheet"));
                    i.setManufacturerId(rs.getLong("manufacturerId"));
                    i.setLocationId(rs.getLong("locationId"));
                    i.setAmount(rs.getInt("amount"));
                    i.setAmountType(rs.getInt("amountType"));
                    i.setOrderState(rs.getInt("orderState"));
                    i.setPackageTypeId(rs.getLong("packageTypeId"));
                    i.setPins(rs.getInt("pins"));
                    i.setRating(rs.getFloat("rating"));
                    i.setDiscourageOrder(rs.getBoolean("discourageOrder"));
                    i.setRemarks(rs.getString("remark"));
                    i.setSet(rs.getBoolean("isSet"));
                    i.setValue(rs.getDouble("value"), rs.getInt("multiplier"), rs.getString("unit"));
                    i.getAud().setInserted(rs.getString("insertedBy"), rs.getTimestamp("insertedDate"));
                    i.getAud().setUpdated(rs.getString("updatedBy"), rs.getTimestamp("updatedDate"));

                    i.setInserted(true);
                    items.add(i);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(i, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  CATEGORIES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Category> getCategories() {
        if (categories == null) {
            updateCategories();
        }
        return categories;
    }

    private void updateCategories() {
        categories = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
        }
        Status().setMessage("Fetching categories from DB");
        Category c = null;
        String sql = scriptResource.readString(Category.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    c = new Category();
                    c.setId(rs.getLong("id"));
                    c.setName(rs.getString("name"));
                    c.setIconPath(rs.getString("iconpath"));

                    c.setInserted(true);
                    if (c.getId() != DbObject.UNKNOWN_ID) {
                        categories.add(c);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(c, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        categories.add(0, Category.getUnknownCategory());
    }

    /*
    *                  PRODUCTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Product> getProducts() {
        if (products == null) {
            updateProducts();
        }
        return products;
    }

    private void updateProducts() {
        products = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
        }
        Status().setMessage("Fetching products from DB");
        Product p = null;
        String sql = scriptResource.readString(Product.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new Product();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));
                    p.setCategoryId(rs.getLong("categoryid"));

                    p.setInserted(true);
                    if (p.getId() != DbObject.UNKNOWN_ID) {
                        products.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        products.add(0, Product.getUnknownProduct());
    }

    /*
    *                  TYPES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Type> getTypes() {
        if (types == null) {
            updateTypes();
        }
        return types;
    }

    private void updateTypes() {
        types = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
        }
        Status().setMessage("Fetching types from DB");
        Type t = null;
        String sql = scriptResource.readString(Type.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    t = new Type();
                    t.setId(rs.getLong("id"));
                    t.setName(rs.getString("name"));
                    t.setIconPath(rs.getString("iconpath"));
                    t.setProductId(rs.getLong("productid"));

                    t.setInserted(true);
                    if (t.getId() != 1) {
                        types.add(t);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(t, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        types.add(0, Type.getUnknownType());
    }

    /*
    *                  MANUFACTURERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Manufacturer> getManufacturers() {
        if (manufacturers == null) {
            updateManufacturers();
        }
        return manufacturers;
    }

    private void updateManufacturers() {
        manufacturers = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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

                    m.setInserted(true);
                    if (m.getId() != DbObject.UNKNOWN_ID) {
                        manufacturers.add(m);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(m, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        manufacturers.add(0, Manufacturer.getUnknownManufacturer());
    }

    /*
    *                  LOCATIONS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public synchronized List<Location> getLocations()   {
        if (locations == null) {
            updateLocations();
        }
        return locations;
    }

    private void updateLocations() {
        locations = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
            DbErrorObject object = new DbErrorObject(l, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void deleteLocationsByType(long typeId) {
        String sql = scriptResource.readString("locations.sqlDeleteByType");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, typeId);
                stmt.execute();
            }
            locations = null;
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(null, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  LOCATION TYPES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<LocationType> getLocationTypes()   {
        if (locationTypes == null) {
            updateLocationTypes();
        }
        return locationTypes;
    }

    private void updateLocationTypes() {
        locationTypes = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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

                    l.setInserted(true);
                    locationTypes.add(l);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(l, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  ORDERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Order> getOrders()    {
        if (orders == null) {
            updateOrders();
        }
        return orders;
    }

    private void updateOrders()    {
        orders = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
        }
        Status().setMessage("Fetching orders from DB");
        Order o = null;
        String sql = scriptResource.readString(Order.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    o = new Order();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setIconPath(rs.getString("iconPath"));
                    o.setDateOrdered(rs.getTimestamp("dateOrdered"));
                    o.setDateModified(rs.getTimestamp("dateModified"));
                    o.setDateReceived(rs.getTimestamp("dateReceived"));
                    o.setDistributorId(rs.getLong("distributorId"));
                    o.setOrderReference(rs.getString("orderReference"));
                    o.setTrackingNumber(rs.getString("trackingNumber"));

                    o.setInserted(true);
                    if (o.getId() != DbObject.UNKNOWN_ID) {
                        orders.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(o, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        orders.add(0, Order.getUnknownOrder());
        orders.sort(new Order.SortAllOrders());
    }

    /*
    *                  ORDER ITEMS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<OrderItem> getOrderItems()    {
        if (orderItems == null) {
            updateOrderItems();
        }
        return orderItems;
    }

    private void updateOrderItems()    {
        orderItems = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
        }
        Status().setMessage("Fetching order items from DB");
        OrderItem o = null;
        String sql = scriptResource.readString(OrderItem.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    o = new OrderItem();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setOrderId(rs.getLong("orderId"));
                    o.setItemId(rs.getLong("itemId"));
                    o.setAmount(rs.getInt("amount"));
                    o.setDistributorPartId(rs.getLong("distributorPartId"));

                    o.setInserted(true);
                    if (o.getId() != DbObject.UNKNOWN_ID) {
                        orderItems.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(o, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public List<OrderItem> getOrderedItems(long orderId) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItem i : getOrderItems()) {
            if (i.getOrderId() == orderId || orderId == -1) {
                items.add(i);
            }
        }
        return items;
    }

    public void removeItemFromOrder(OrderItem orderItem) {
        if (Main.CACHE_ONLY) {
            return;
        }
        Status().setMessage("Removing \""+orderItem.getItem().toString()+"\" from \""+orderItem.getOrder().toString());

        String sql = scriptResource.readString("orderitems.sqlDeleteItemFromOrder");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, orderItem.getOrderId());
                stmt.setLong(2, orderItem.getItemId());
                stmt.execute();

            } catch (SQLException e) {
                Status().setError("Failed to detele item from order");
            }
        } catch (SQLException e) {
            Status().setError("Failed to detele item from order");
        }
    }

    /*
    *                  DISTRIBUTORS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Distributor> getDistributors()    {
        if (distributors == null) {
            updateDistributors();
        }
        return distributors;
    }

    private void updateDistributors()    {
        distributors = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    d.setWebsite(rs.getString("website"));
                    d.setOrderLink(rs.getString("orderLink"));
                    d.setOrderFileFormatId(rs.getLong("orderFileFormatId"));

                    d.setInserted(true);
                    if (d.getId() != DbObject.UNKNOWN_ID) {
                        distributors.add(d);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(d, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


    /*
    *                  PART NUMBERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<DistributorPartLink> getDistributorPartLinks()    {
        if (distributorPartLinks == null) {
            updateDistributorParts();
        }
        return distributorPartLinks;
    }

    private void updateDistributorParts()    {
        distributorPartLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    pn.setItemRef(rs.getString("distributorPartName"));

                    pn.setInserted(true);
                    if (pn.getId() != DbObject.UNKNOWN_ID) {
                        distributorPartLinks.add(pn);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(pn, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  PACKAGES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Package> getPackages()    {
        if (packages == null) {
            updatePackages();
        }
        return packages;
    }

    private void updatePackages()    {
        packages = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
            DbErrorObject object = new DbErrorObject(pa, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  PACKAGE TYPES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<PackageType> getPackageTypes()    {
        if (packageTypes == null) {
            updatePackageTypes();
        }
        return packageTypes;
    }

    private void updatePackageTypes()    {
        packageTypes = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
            DbErrorObject object = new DbErrorObject(pt, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  PROJECTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Project> getProjects()    {
        if (projects == null) {
            updateProjects();
        }
        return projects;
    }

    private void updateProjects()    {
        projects = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    p.setMainDirectory(rs.getString("mainDirectory"));

                    p.setInserted(true);
                    if (p.getId() != DbObject.UNKNOWN_ID) {
                        projects.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  PROJECT CODEs
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<ProjectCode> getProjectCodes()    {
        if (projectCodes == null) {
            updateProjectCodes();
        }
        return projectCodes;
    }

    private void updateProjectCodes()    {
        projectCodes = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    p.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remarks"), p.createRemarksFileName()));

                    p.setInserted(true);
                    projectCodes.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  PROJECT PCBs
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<ProjectPcb> getProjectPcbs()    {
        if (projectPcbs == null) {
            updateProjectPcbs();
        }
        return projectPcbs;
    }

    private void updateProjectPcbs()    {
        projectPcbs = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    p.setRemarksFile(FileUtils.blobToFile(rs.getBlob("remarks"), p.createRemarksFileName()));
                    p.setLastParsedDate(rs.getTimestamp("lastParsedDate"));

                    p.setInserted(true);
                    projectPcbs.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  PROJECT PCB ITEMS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<PcbItemProjectLink> getPcbItemProjectLinks()    {
        if (pcbItemProjectLinks == null) {
            updatePcbItemLinks();
        }
        return pcbItemProjectLinks;
    }

    private void updatePcbItemLinks()    {
        pcbItemProjectLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    p.setUsedCount(rs.getInt("usedCount"));
                    p.setPcbItemReferences(rs.getString("pcbItemReferences"));
                    p.setSheetName(rs.getString("sheetName"));

                    // Used and processed
                    p.setUsed(p.getUsedCount() > 0);
                    p.setProcessed(p.isUsed());

                    p.setInserted(true);
                    pcbItemProjectLinks.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  PROJECT IDES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<ProjectIDE> getProjectIDES()    {
        if (projectIDES == null) {
            updateProjectIDEs();
        }
        return projectIDES;
    }

    private void updateProjectIDEs()    {
        projectIDES = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
            DbErrorObject object = new DbErrorObject(p, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


    /*
    *                  PARSER ITEM LINK
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<ParserItemLink> getParserItemLinks()    {
        if (parserItemLinks == null) {
            updateParserItemLinks();
        }
        return parserItemLinks;
    }

    private void updateParserItemLinks()    {
        parserItemLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    p.setCategoryId(rs.getLong("categoryId"));
                    p.setProductId(rs.getLong("productId"));
                    p.setTypeId(rs.getLong("typeId"));

                    p.setInserted(true);
                    parserItemLinks.add(p);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(p, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


    /*
    *                  ORDER FILES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<OrderFileFormat> getOrderFileFormats()    {
        if (orderFileFormats == null) {
            updateOrderFileFormats();
        }
        return orderFileFormats;
    }

    private void updateOrderFileFormats()    {
        orderFileFormats = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
            DbErrorObject object = new DbErrorObject(off, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


    /*
    *                  SET ITEMS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<SetItem> getSetItems()    {
        if (setItems == null) {
            updateSetItems();
        }
        return setItems;
    }

    private void updateSetItems()    {
        setItems = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
        }
        Status().setMessage("Fetching set items from DB");
        SetItem si = null;
        String sql = scriptResource.readString(SetItem.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    si = new SetItem();
                    si.setId(rs.getLong("id"));
                    si.setName(rs.getString("name"));
                    si.setIconPath(rs.getString("iconPath"));
                    si.setAmount(rs.getInt("amount"));
                    si.setValue(new Value(rs.getDouble("value"), rs.getInt("multiplier"), rs.getString("unit")));
                    si.setItemId(rs.getLong("itemId"));
                    si.setLocationId(rs.getLong("locationId"));

                    si.setInserted(true);
                    setItems.add(si);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(si, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


    /*
    *                  PCB ITEMS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<PcbItem> getPcbItems()    {
        if (pcbItems == null) {
            updatePcbItems();
        }
        return pcbItems;
    }

    private void updatePcbItems()    {
        pcbItems = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
        }
        Status().setMessage("Fetching pcb items from DB");
        PcbItem kc = null;
        String sql = scriptResource.readString(PcbItem.TABLE_NAME + DbObject.SQL_SELECT_ALL);
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    kc = new PcbItem();
                    kc.setId(rs.getLong("id"));
                    kc.setValue(rs.getString("value"));
                    kc.setFootprint(rs.getString("footprint"));
                    kc.setLibrary(rs.getString("lib"));
                    kc.setPartName(rs.getString("part"));

                    kc.setInserted(true);
                    pcbItems.add(kc);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(kc, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public long findKcComponentId(String value, String footprint, String lib, String part) {
        long id = -1;
        String sql = scriptResource.readString(PcbItem.TABLE_NAME + DbObject.SQL_SELECT_ONE);

        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, value);
                stmt.setString(2, footprint);
                stmt.setString(3, lib);
                stmt.setString(4, part);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        id = rs.getLong("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }


    /*
    *                  PCB ITEM LINKS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<PcbItemItemLink> getPcbItemItemLinks()    {
        if (pcbItemItemLinks == null) {
            updateKcItemLinks();
        }
        return pcbItemItemLinks;
    }

    private void updateKcItemLinks()    {
        pcbItemItemLinks = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    kil.setSetItemId(rs.getLong("setItemId"));
                    kil.setIsSetItem(rs.getBoolean("isSetItem"));
                    kil.setMatch(rs.getByte("componentMatch"));
                    kil.setPcbItemId(rs.getLong("pcbItemId"));

                    kil.setInserted(true);
                    pcbItemItemLinks.add(kil);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(kil, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


    /*
    *                  LOGS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Log> getLogs()    {
        if (logs == null) {
            updateLogs();
        }
        return logs;
    }

    public void updateLogs()    {
        logs = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    l.setLogTime(rs.getTimestamp("logtime"));
                    l.setLogClass(rs.getString("logclass"));
                    l.setLogMessage(rs.getString("logmessage"));
                    l.setLogException(rs.getString("logexception"));

                    l.setInserted(true);
                    logs.add(l);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(l, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
    *                  DB HISTORY
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<DbHistory> getDbHistory() {
        if (dbHistoryList == null) {
            updateDbHistoryList();
        }
        return dbHistoryList;
    }

    private void updateDbHistoryList() {
        dbHistoryList = new ArrayList<>();
        if (Main.CACHE_ONLY) {
            return;
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
                    dbh.setDate(rs.getTimestamp("date"));
                    dbh.setDbAction(rs.getInt("dbAction"));
                    dbh.setDbObjectType(rs.getInt("dbObjectType"));
                    dbh.setDbObjectId(rs.getLong("dbObjectId"));

                    dbh.setInserted(true);
                    dbHistoryList.add(dbh);
                }
            }
        } catch (SQLException e) {
            DbErrorObject object = new DbErrorObject(dbh, e, OBJECT_SELECT, sql);
            try {
                nonoList.put(object);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }



    /*
    *                  OTHER
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public List<Order> getOrdersForManufacturer(long manufacturerId) {
        return null;
    }

    public List<Item> getItemsForManufacturer(long manufacturerId)    {
        List<Item> items = new ArrayList<>();
        for (Item item : getItems()) {
            if (item.getManufacturerId() == manufacturerId) {
                items.add(item);
            }
        }
        return items;
    }

    public List<Item> getItemsForCategory(long categoryId)    {
        List<Item> items = new ArrayList<>();
        for (Item item : getItems()) {
            if (item.getCategoryId() == categoryId) {
                items.add(item);
            }
        }
        return items;
    }

    public boolean isItemInCurrentOrders(long itemId) {
        for (OrderItem oi : getOrderItems()) {
            if (!oi.getOrder().isOrdered()) {
                if (oi.getItemId() == itemId) {
                    return true;
                }
            }
        }
        return false;
    }

//    public List<ProjectDirectory> getProjectDirectoryListForProject(long projectId) {
//        List<ProjectDirectory> directories = new ArrayList<>();
//        for (ProjectDirectory directory : getProjectDirectories()) {
//            if (directory.getProjectId() == projectId) {
//                directories.add(directory);
//            }
//        }
//        return directories;
//    }

//    public HashMap<ProjectIDE, List<File>> getProjectTypesForProjectDirectory(long directoryId) {
//        HashMap<ProjectIDE, List<File>> projectTypes = new HashMap<>();
//        for (ProjectTypeLink ptl : getProjectTypeLinks()) {
//            if(ptl.getProjectDirectoryId() == directoryId) {
//                if (projectTypes.containsKey(ptl.getProjectIDE())) {
//                    projectTypes.computeIfAbsent(ptl.getProjectIDE(), k -> new ArrayList<>());
//                } else {
//                    projectTypes.put(ptl.getProjectIDE(), new ArrayList<>());
//                }
//                projectTypes.get(ptl.getProjectIDE()).add(ptl.getFile());
//            }
//        }
//        return projectTypes;
//    }

//    public List<Project> getProjectForProjectType(long id) {
//        List<Project> projects = new ArrayList<>();
//        for(Project project : getProjects()) {
//            for (ProjectDirectory pd : project.getProjectDirectories()) {
//                for (ProjectIDE pt : pd.getProjectTypeMap().keySet()) {
//                    if (pt.getId() == id) {
//                        if (!projects.contains(project)) {
//                            projects.add(project);
//                        }
//                    }
//                }
//            }
//
//        }
//        return projects;
//    }

    public List<Log> getLogsByType(boolean info, boolean debug, boolean warn, boolean error) {
        List<Log> logList = new ArrayList<>();
        for (Log log : getLogs()) {
            switch (log.getLogType()) {
                case Statics.LogTypes.INFO:
                    if (info) logList.add(log);
                    break;
                case Statics.LogTypes.DEBUG:
                    if (debug) logList.add(log);
                    break;
                case Statics.LogTypes.WARN:
                    if (warn) logList.add(log);
                    break;
                case Statics.LogTypes.ERROR:
                    if (error) logList.add(log);
                    break;
            }
        }
        return logList;
    }


    /*
    *                  CLASSES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private class DbQueueWorker extends SwingWorker<Integer, String> {

        volatile boolean keepRunning = true;
        private String name;

        DbQueueWorker(String name) {
            this.name = name;
        }

        private void insert(PreparedStatement stmt, DbObject dbo) throws SQLException {
            dbo.addParameters(stmt);
            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                dbo.setId(rs.getLong(1));
            }

            // Listeners
            dbo.tableChanged(OBJECT_INSERT);

            // Log to db history
            if (!(dbo instanceof DbHistory)) {
                try {
                    DbHistory dbHistory = new DbHistory(OBJECT_INSERT, dbo);
                    dbHistory.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void update(PreparedStatement stmt, DbObject dbo) throws SQLException {
            int ndx = dbo.addParameters(stmt);
            if (ndx > 0) {
                stmt.setLong(ndx, dbo.getId());
                stmt.execute();
            }

            // Listeners
            dbo.tableChanged(OBJECT_UPDATE);

            // Log to db history
            if (!(dbo instanceof DbHistory)) {
                try {
                    DbHistory dbHistory = new DbHistory(OBJECT_UPDATE, dbo);
                    dbHistory.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void delete(PreparedStatement stmt, DbObject dbo) throws SQLException {
            stmt.setLong(1, dbo.getId());
            stmt.execute();
            dbo.setId(-1);// Not in database anymore

            // Listeners
            dbo.tableChanged(OBJECT_DELETE);

            // Log to db history
            if (!(dbo instanceof DbHistory)) {
                try {
                    DbHistory dbHistory = new DbHistory(OBJECT_DELETE, dbo);
                    dbHistory.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Integer doInBackground() throws Exception {
            while (keepRunning) {

                DbQueueObject queueObject = workList.take();
                if (queueObject != null) {
                    try (Connection connection = DbManager.getConnection()) {
                        // Open db
                        try (PreparedStatement stmt = connection.prepareStatement("BEGIN;")) {
                            stmt.execute();
                        }

                        try {
                            boolean hasMoreWork;
                            do {
                                DbObject dbo = queueObject.getObject();
                                switch (queueObject.getHow()) {
                                    case OBJECT_INSERT: {
                                        String sql = dbo.getScript(DbObject.SQL_INSERT);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                                            insert(stmt, dbo);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbo) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbo, e, OBJECT_INSERT, sql);
                                                nonoList.put(object);
                                            }
                                        }
                                    }
                                    break;
                                    case OBJECT_UPDATE: {
                                        String sql = dbo.getScript(DbObject.SQL_UPDATE);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                                            update(stmt, dbo);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbo) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbo, e, OBJECT_UPDATE, sql);
                                                nonoList.put(object);
                                            }
                                        }
                                        break;
                                    }
                                    case OBJECT_DELETE:
                                        String sql = dbo.getScript(DbObject.SQL_DELETE);
                                        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                                            delete(stmt, dbo);
                                        } catch (SQLException e) {
                                            if (DbObject.getType(dbo) != DbObject.TYPE_LOG) {
                                                DbErrorObject object = new DbErrorObject(dbo, e, OBJECT_DELETE, sql);
                                                nonoList.put(object);
                                            }
                                        }
                                        break;
                                }

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
                            e.printStackTrace();
                            System.out.print("ROLLING BACK DB");
                            try (PreparedStatement stmt = connection.prepareStatement("rollback;")) {
                                stmt.execute();
                            }
                            // TODO: efficient error handling
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

    private class DbErrorWorker extends  SwingWorker<Integer, String> {

        volatile boolean keepRunning = true;
        private String name;

        public DbErrorWorker(String name) {
            this.name = name;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            while (keepRunning) {
                DbErrorObject error = nonoList.take();
                if (error != null) {
                    switch (error.getHow()) {
                        case OBJECT_SELECT:
                            if (errorListener != null) {
                                errorListener.onSelectError(error.getObject(), error.getException(), error.getSql());
                            }
                            break;
                        case OBJECT_INSERT:
                            if (errorListener != null) {
                                errorListener.onInsertError(error.getObject(), error.getException(), error.getSql());
                            }
                            break;
                        case OBJECT_UPDATE:
                            if (errorListener != null) {
                                errorListener.onUpdateError(error.getObject(), error.getException(), error.getSql());
                            }
                            break;
                        case OBJECT_DELETE:
                            if (errorListener != null) {
                                errorListener.onDeleteError(error.getObject(), error.getException(), error.getSql());
                            }
                            break;
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
}
