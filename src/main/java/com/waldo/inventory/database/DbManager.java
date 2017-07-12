package com.waldo.inventory.database;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.database.interfaces.TableChangedListener;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import com.waldo.inventory.gui.Application;
import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.Utils.Statics.LogTypes.INFO;
import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.scriptResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class DbManager implements TableChangedListener {

    private static final LogManager LOG = LogManager.LOG(DbManager.class);

    public static final int OBJECT_ADDED = 0;
    public static final int OBJECT_UPDATED = 1;
    public static final int OBJECT_DELETED = 2;

    // Db
    private static final DbManager INSTANCE = new DbManager();
    public static DbManager db() {
        return INSTANCE;
    }
    private BasicDataSource dataSource;
    private List<String> tableNames;
    private boolean initialized = false;

    // Events
    private List<DbObjectChangedListener<Item>> onItemsChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<Category>> onCategoriesChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<Product>> onProductsChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<Type>> onTypesChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<Manufacturer>> onManufacturerChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<Order>> onOrdersChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<Location>> onLocationsChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<OrderItem>> onOrderItemsChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<Distributor>> onDistributorsChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<PartNumber>> onPartNumbersChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<PackageType>> onPackageTypesChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<Project>> onProjectChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<ProjectDirectory>> onProjectDirectoryChangedListenerList = new ArrayList<>();
    private List<DbObjectChangedListener<ProjectType>> onProjectTypeChangedListenerList = new ArrayList<>();

    // Part numbers...

    // Cached lists
    private List<Item> items;
    private List<Category> categories;
    private List<Product> products;
    private List<Type> types;
    private List<Manufacturer> manufacturers;
    private List<Location> locations;
    private List<Order> orders;
    private List<OrderItem> orderItems;
    private List<Distributor> distributors;
    private List<PartNumber> partNumbers;
    private List<PackageType> packageTypes;
    private List<Project> projects;
    private List<ProjectDirectory> projectDirectories;
    private List<ProjectType> projectTypes;
    private List<ProjectTypeLink> projectTypeLinks;
    private List<Log> logs;

    private DbManager() {}

    public void init() {
        initialized = false;
        DbSettings s = settings().getDbSettings();
        if (s != null) {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName("net.sf.log4jdbc.DriverSpy");
            dataSource.setUrl("jdbc:log4jdbc:sqlite:" + s.getDbFile());
            dataSource.setUsername(s.getDbUserName());
            dataSource.setPassword(s.getDbUserPw());
            dataSource.setMaxIdle(s.getDbMaxIdleConnections());
            dataSource.setMaxActive(s.getDbMaxActiveConnections());
            dataSource.setPoolPreparedStatements(s.isDbPoolPreparedStatements());
            dataSource.setLogAbandoned(s.isDbLogAbandoned());
            dataSource.setRemoveAbandoned(s.isDbRemoveAbandoned());
            dataSource.setInitialSize(s.getDbInitialSize());
            dataSource.setRemoveAbandonedTimeout(s.getDbRemoveAbandonedTimeout());

            Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            flyway.migrate();
            initialized = true;

            String sql = "PRAGMA foreign_keys=ON;";
            try (Connection connection = getConnection()) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.execute();
                }
            } catch (SQLException e) {
                LOG.error("Error reading from db.", e);
            }

            try {
                tableNames = getTableNames();
            } catch (SQLException e) {
                LOG.error("Error initializing db.", e);
            }

            LOG.info("Database initialized with db file: " + s.getDbFile());
        }
    }

    private void close() {
        if(dataSource != null) {
            Status().setMessage("Closing down");
            try {
                dataSource.close();
            } catch (SQLException e) {
                LOG.error("Error closing db.", e);
            }
        }
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

    public void addOnPartNumbersChangedListener(DbObjectChangedListener<PartNumber> dbObjectChangedListener) {
        if (!onPartNumbersChangedListenerList.contains(dbObjectChangedListener)) {
            onPartNumbersChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProjectChangedListener(DbObjectChangedListener<Project> dbObjectChangedListener) {
        if (!onProjectChangedListenerList.contains(dbObjectChangedListener)) {
            onProjectChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnPackageTypeChangedListener(DbObjectChangedListener<PackageType> dbObjectChangedListener) {
        if (!onPackageTypesChangedListenerList.contains(dbObjectChangedListener)) {
            onPackageTypesChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProjectDirectoryChangedListener(DbObjectChangedListener<ProjectDirectory> dbObjectChangedListener) {
        if (!onProjectDirectoryChangedListenerList.contains(dbObjectChangedListener)) {
            onProjectDirectoryChangedListenerList.add(dbObjectChangedListener);
        }
    }

    public void addOnProjectTypeChangedListener(DbObjectChangedListener<ProjectType> dbObjectChangedListener) {
        if (!onProjectTypeChangedListenerList.contains(dbObjectChangedListener)) {
            onProjectTypeChangedListenerList.add(dbObjectChangedListener);
        }
    }



    public void removeOnItemsChangedListener(DbObjectChangedListener<Item> dbObjectChangedListener) {
        if (onItemsChangedListenerList != null) {
            if (onItemsChangedListenerList.contains(dbObjectChangedListener)) {
                onItemsChangedListenerList.remove(dbObjectChangedListener);
            }
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

    public void removeOnManufacturersChangedListener(DbObjectChangedListener<Manufacturer> dbObjectChangedListener) {
        if (onManufacturerChangedListenerList != null) {
            if (onManufacturerChangedListenerList.contains(dbObjectChangedListener)) {
                onManufacturerChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnLocationChangedListener(DbObjectChangedListener<Location> dbObjectChangedListener) {
        if (onLocationsChangedListenerList != null) {
            if (onLocationsChangedListenerList.contains(dbObjectChangedListener)) {
                onLocationsChangedListenerList.remove(dbObjectChangedListener);
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

    public void removeOnOrderItemsChangedListener(DbObjectChangedListener<OrderItem> dbObjectChangedListener) {
        if (onOrderItemsChangedListenerList != null) {
            if (onOrderItemsChangedListenerList.contains(dbObjectChangedListener)) {
                onOrderItemsChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnDistributorChangedListener(DbObjectChangedListener<Distributor> dbObjectChangedListener) {
        if (onDistributorsChangedListenerList != null) {
            if (onDistributorsChangedListenerList.contains(dbObjectChangedListener)) {
                onDistributorsChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnPackageTypesChangedListener(DbObjectChangedListener<PackageType> dbObjectChangedListener) {
        if (onPackageTypesChangedListenerList != null) {
            if (onPackageTypesChangedListenerList.contains(dbObjectChangedListener)) {
                onPackageTypesChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnProjectsChangedListener(DbObjectChangedListener<Project> dbObjectChangedListener) {
        if (onProjectChangedListenerList != null) {
            if (onProjectChangedListenerList.contains(dbObjectChangedListener)) {
                onProjectChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnProjectsDirectoryChangedListener(DbObjectChangedListener<ProjectDirectory> dbObjectChangedListener) {
        if (onProjectDirectoryChangedListenerList != null) {
            if (onProjectDirectoryChangedListenerList.contains(dbObjectChangedListener)) {
                onProjectDirectoryChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }

    public void removeOnProjectTypesChangedListener(DbObjectChangedListener<ProjectType> dbObjectChangedListener) {
        if (onProjectTypeChangedListenerList != null) {
            if (onProjectTypeChangedListenerList.contains(dbObjectChangedListener)) {
                onProjectTypeChangedListenerList.remove(dbObjectChangedListener);
            }
        }
    }



    private <T extends DbObject> void notifyListeners(int changedHow, T newObject, T oldObject, List<DbObjectChangedListener<T>> listeners) {
        for (DbObjectChangedListener<T> l : listeners) {
            switch (changedHow) {
                case OBJECT_ADDED:
                    l.onAdded(newObject);
                    break;
                case OBJECT_UPDATED:
                    l.onUpdated(newObject, oldObject);
                    break;
                case OBJECT_DELETED:
                    l.onDeleted(newObject);
                    break;
            }
        }
    }

    @Override
    public void onTableChanged(String tableName, int changedHow, DbObject newObject, DbObject oldObject) throws SQLException {
        String how = "";
        switch (changedHow) {
            case OBJECT_ADDED: how = "added in "; break;
            case OBJECT_UPDATED: how = "updated in "; break;
            case OBJECT_DELETED: how = "deleted from"; break;
        }

        Status().setMessage(newObject.getName() + " " + how + tableName);

        switch (tableName) {
            case Item.TABLE_NAME:
                updateItems();
                notifyListeners(changedHow, (Item)newObject, (Item)oldObject, onItemsChangedListenerList);
                break;
            case Category.TABLE_NAME:
                updateCategories();
                notifyListeners(changedHow, (Category)newObject, (Category)oldObject, onCategoriesChangedListenerList);
                break;
            case Product.TABLE_NAME:
                updateProducts();
                notifyListeners(changedHow, (Product)newObject, (Product)oldObject, onProductsChangedListenerList);
                break;
            case Type.TABLE_NAME:
                updateTypes();
                notifyListeners(changedHow, (Type)newObject, (Type)oldObject, onTypesChangedListenerList);
                break;
            case Manufacturer.TABLE_NAME:
                updateManufacturers();
                notifyListeners(changedHow, (Manufacturer)newObject, (Manufacturer)oldObject, onManufacturerChangedListenerList);
                break;
            case Location.TABLE_NAME:
                updateLocations();
                notifyListeners(changedHow, (Location)newObject, (Location)oldObject, onLocationsChangedListenerList);
                break;
            case Order.TABLE_NAME:
                updateOrders();
                notifyListeners(changedHow, (Order)newObject, (Order)oldObject, onOrdersChangedListenerList);
                break;
            case OrderItem.TABLE_NAME:
                updateOrderItems();
                notifyListeners(changedHow, (OrderItem)newObject, (OrderItem)oldObject, onOrderItemsChangedListenerList);
                break;
            case Distributor.TABLE_NAME:
                updateDistributors();
                notifyListeners(changedHow, (Distributor)newObject, (Distributor)oldObject, onDistributorsChangedListenerList);
                break;
            case PartNumber.TABLE_NAME:
                updatePartNumbers();
                notifyListeners(changedHow, (PartNumber)newObject, (PartNumber)oldObject, onPartNumbersChangedListenerList);
                break;
            case PackageType.TABLE_NAME:
                updatePackageTypes();
                notifyListeners(changedHow, (PackageType)newObject, (PackageType)oldObject, onPackageTypesChangedListenerList);
                break;
            case Project.TABLE_NAME:
                updateProjects();
                notifyListeners(changedHow, (Project)newObject, (Project)oldObject, onProjectChangedListenerList);
                break;
            case ProjectDirectory.TABLE_NAME:
                updateProjectDirectories();
                notifyListeners(changedHow, (ProjectDirectory)newObject, (ProjectDirectory)oldObject, onProjectDirectoryChangedListenerList);
                break;
            case ProjectType.TABLE_NAME:
                updateProjectTypes();
                notifyListeners(changedHow, (ProjectType)newObject, (ProjectType)oldObject, onProjectTypeChangedListenerList);
                break;
            case ProjectTypeLink.TABLE_NAME:
                updateProjectTypeLinks();
                break;
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
        Status().setMessage("Fetching items from DB");

        String sql = "SELECT * FROM items ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Item i = new Item();
                    i.setId(rs.getLong("id"));
                    i.setName(rs.getString("name"));
                    i.setIconPath(rs.getString("iconpath"));
                    i.setDescription(rs.getString("description"));
                    i.setPrice(rs.getDouble("price"));
                    i.setCategoryId(rs.getInt("categoryid"));
                    i.setProductId(rs.getInt("productid"));
                    i.setTypeId(rs.getInt("typeid"));
                    i.setLocalDataSheet(rs.getString("localdatasheet"));
                    i.setOnlineDataSheet(rs.getString("onlinedatasheet"));
                    i.setManufacturerId(rs.getLong("manufacturerid"));
                    i.setLocationId(rs.getLong("locationid"));
                    i.setAmount(rs.getInt("amount"));
                    i.setAmountType(rs.getInt("amounttype"));
                    i.setOrderState(rs.getInt("orderstate"));
                    i.setPackageTypeId(rs.getLong("packagetypeid"));
                    i.setPins(rs.getInt("pins"));
                    i.setWidth(rs.getDouble("width"));
                    i.setHeight(rs.getDouble("height"));
                    i.setRating(rs.getFloat("rating"));
                    i.setDiscourageOrder(rs.getBoolean("discourageorder"));
                    i.setRemarks(rs.getString("remarks"));

                    i.setOnTableChangedListener(this);
                    items.add(i);
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database: "+ e);
        }
    }

    public Item getItemFromDb(long itemId) {
        Item i = null;
        Status().setMessage("Fetching items from DB");

        String sql = "SELECT * FROM items WHERE id = " + itemId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    i = new Item();
                    i.setId(rs.getLong("id"));
                    i.setName(rs.getString("name"));
                    i.setIconPath(rs.getString("iconpath"));
                    i.setDescription(rs.getString("description"));
                    i.setPrice(rs.getDouble("price"));
                    i.setCategoryId(rs.getInt("categoryid"));
                    i.setProductId(rs.getInt("productid"));
                    i.setTypeId(rs.getInt("typeid"));
                    i.setLocalDataSheet(rs.getString("localdatasheet"));
                    i.setOnlineDataSheet(rs.getString("onlinedatasheet"));
                    i.setManufacturerId(rs.getLong("manufacturerid"));
                    i.setLocationId(rs.getLong("locationid"));
                    i.setAmount(rs.getInt("amount"));
                    i.setAmountType(rs.getInt("amounttype"));
                    i.setOrderState(rs.getInt("orderstate"));
                    i.setPackageTypeId(rs.getLong("packagetypeid"));
                    i.setPins(rs.getInt("pins"));
                    i.setWidth(rs.getDouble("width"));
                    i.setHeight(rs.getDouble("height"));
                    i.setRating(rs.getFloat("rating"));
                    i.setDiscourageOrder(rs.getBoolean("discourageorder"));
                    i.setRemarks(rs.getString("remarks"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database: "+ e);
        }
        return i;
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
        Status().setMessage("Fetching categories from DB");

        String sql = "SELECT * FROM " + Category.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Category c = new Category();
                    c.setId(rs.getLong("id"));
                    c.setName(rs.getString("name"));
                    c.setIconPath(rs.getString("iconpath"));

                    if (c.getId() != DbObject.UNKNOWN_ID) {
                        c.setOnTableChangedListener(this);
                        categories.add(c);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch categories from database");
        }
        categories.add(0, Category.getUnknownCategory());
    }

    public Category getCategoryFromDb(long categoryId) {
        Category c = null;
        Status().setMessage("Fetching categories from DB");

        String sql = "SELECT * FROM " + Category.TABLE_NAME + " WHERE id = " + categoryId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    c = new Category();
                    c.setId(rs.getLong("id"));
                    c.setName(rs.getString("name"));
                    c.setIconPath(rs.getString("iconpath"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch categories from database");
        }
        return c;
    }

    public void getCategoriesAsync(final List<Category> categories) {
        if (categories != null) {
            categories.clear();
        }
        SwingWorker<Void, Category> worker = new SwingWorker<Void, Category>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Category> categoryList = getCategories();
                for(Category c : categoryList) {
                    publish(c);
                }
                return null;
            }

            @Override
            protected void process(List<Category> chunks) {
                for (Category c : chunks) {
                    if (categories != null) {
                        categories.add(c);
                    }
                }
            }
        };
        worker.execute();
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
        Status().setMessage("Fetching products from DB");

        String sql = "SELECT * FROM " + Product.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));
                    p.setCategoryId(rs.getLong("categoryid"));

                    if (p.getId() != DbObject.UNKNOWN_ID) {
                        p.setOnTableChangedListener(this);
                        products.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch products from database");
        }

        products.add(0, Product.getUnknownProduct());
    }

    public Product getProductFromDb(long productId) {
        Product p = null;
        String sql = "SELECT * FROM " + Product.TABLE_NAME + " WHERE id = " + productId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new Product();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));
                    p.setCategoryId(rs.getLong("categoryid"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch products from database");
        }
        return p;
    }

    public void getProductsAsync(final List<Product> products) {
        if (products != null) {
            products.clear();
        }
        SwingWorker<Void, Product> worker = new SwingWorker<Void, Product>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Product> productList = getProducts();
                for(Product p : productList) {
                    publish(p);
                }
                return null;
            }

            @Override
            protected void process(List<Product> chunks) {
                for (Product c : chunks) {
                    if (products != null) {
                        products.add(c);
                    }
                }
            }
        };
        worker.execute();
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
        Status().setMessage("Fetching types from DB");

        String sql = "SELECT * FROM " + Type.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Type t = new Type();
                    t.setId(rs.getLong("id"));
                    t.setName(rs.getString("name"));
                    t.setIconPath(rs.getString("iconpath"));
                    t.setProductId(rs.getLong("productid"));

                    if (t.getId() != 1) {
                        t.setOnTableChangedListener(this);
                        types.add(t);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch types from database", "Error", JOptionPane.ERROR_MESSAGE);
        }
        types.add(0, Type.getUnknownType());
    }

    public Type getTypeFromDb(long typeId) {
        Type t = null;
        Status().setMessage("Fetching types from DB");

        String sql = "SELECT * FROM " + Type.TABLE_NAME + " WHERE id = " + typeId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    t = new Type();
                    t.setId(rs.getLong("id"));
                    t.setName(rs.getString("name"));
                    t.setIconPath(rs.getString("iconpath"));
                    t.setProductId(rs.getLong("productid"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch types from database", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return t;
    }

    public void getTypesAsync(final List<Type> types) {
        if (types != null) {
            types.clear();
        }
        SwingWorker<Void, Type> worker = new SwingWorker<Void, Type>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Type> typeList = getTypes();
                for(Type p : typeList) {
                    publish(p);
                }
                return null;
            }

            @Override
            protected void process(List<Type> chunks) {
                for (Type c : chunks) {
                    if (types != null) {
                        types.add(c);
                    }
                }
            }
        };
        worker.execute();
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
        Status().setMessage("Fetching manufacturers from DB");

        String sql = "SELECT * FROM " + Manufacturer.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Manufacturer m = new Manufacturer();
                    m.setId(rs.getLong("id"));
                    m.setName(rs.getString("name"));
                    m.setWebsite(rs.getString("website"));
                    m.setIconPath(rs.getString("iconpath"));

                    if (m.getId() != 1) {
                        m.setOnTableChangedListener(this);
                        manufacturers.add(m);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch manufacturers from databasee");
        }
        manufacturers.add(0, Manufacturer.getUnknownManufacturer());
    }

    public Manufacturer getManufacturerFromDb(long manufacturerId) {
        Manufacturer m = null;
        Status().setMessage("Fetching manufacturers from DB");

        String sql = "SELECT * FROM " + Manufacturer.TABLE_NAME + " WHERE id = " + manufacturerId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    m = new Manufacturer();
                    m.setId(rs.getLong("id"));
                    m.setName(rs.getString("name"));
                    m.setWebsite(rs.getString("website"));
                    m.setIconPath(rs.getString("iconpath"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch manufacturers from databasee");
        }
       return m;
    }

    /*
    *                  LOCATIONS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Location> getLocations()   {
        if (locations == null) {
            updateLocations();
        }
        return locations;
    }

    private void updateLocations() {
        locations = new ArrayList<>();
        Status().setMessage("Fetching locations from DB");

        String sql = "SELECT * FROM " + Location.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Location l = new Location();
                    l.setId(rs.getLong("id"));
                    l.setName(rs.getString("name"));
                    l.setIconPath(rs.getString("iconpath"));

                    if (l.getId() != 1) {
                        l.setOnTableChangedListener(this);
                        locations.add(l);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch locations from database");
        }
        locations.add(0, Location.getUnknownLocation());
    }

    public Location getLocationFromDb(long locationId) {
        Location l = null;
        Status().setMessage("Fetching locations from DB");

        String sql = "SELECT * FROM " + Location.TABLE_NAME + " WHERE id = " + locationId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    l = new Location();
                    l.setId(rs.getLong("id"));
                    l.setName(rs.getString("name"));
                    l.setIconPath(rs.getString("iconpath"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch locations from database");
        }
        return l;
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
        Status().setMessage("Fetching orders from DB");

        String sql = "SELECT * FROM " + Order.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Order o = new Order();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setIconPath(rs.getString("iconpath"));
                    o.setDateOrdered(rs.getDate("dateordered"));
                    o.setDateModified(rs.getDate("datemodified"));
                    o.setDateReceived(rs.getDate("datereceived"));
                    o.setDistributor(sm().findDistributorById(rs.getLong("distributorid")));
                    o.setOrderFile(rs.getString("orderfile"));
                    //o.setOrderItems(getOrderedItems(o.getId()));
                    o.setOrderReference(rs.getString("orderreference"));
                    o.setTrackingNumber(rs.getString("trackingnumber"));

                    if (o.getId() != 1) {
                        o.setOnTableChangedListener(this);
                        orders.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database");
        }
        orders.add(0, Order.getUnknownOrder());
        orders.sort(new Order.OrderAllOrders());
    }

    public Order getOrderFromDb(long orderId) {
        Order o = null;
        Status().setMessage("Fetching orders from DB");

        String sql = "SELECT * FROM " + Order.TABLE_NAME + " WHERE id = " + orderId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    o = new Order();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setIconPath(rs.getString("iconpath"));
                    o.setDateOrdered(rs.getDate("dateordered"));
                    o.setDateModified(rs.getDate("datemodified"));
                    o.setDateReceived(rs.getDate("datereceived"));
                    o.setDistributor(sm().findDistributorById(rs.getLong("distributorid")));
                    o.setOrderFile(rs.getString("orderfile"));
                    //o.setOrderItems(getOrderedItems(o.getId()));
                    o.setOrderReference(rs.getString("orderreference"));
                    o.setTrackingNumber(rs.getString("trackingnumber"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database");
        }
        return o;
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
        Status().setMessage("Fetching order items from DB");

        String sql = "SELECT * FROM " + OrderItem.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    OrderItem o = new OrderItem();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setOrderId(rs.getLong("orderid"));
                    o.setItemId(rs.getLong("itemid"));
                    o.setAmount(rs.getInt("amount"));
                    o.setItemRef(rs.getString("itemref"));

                    if (o.getId() != DbObject.UNKNOWN_ID) {
                        o.setOnTableChangedListener(this);
                        orderItems.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database");
        }
    }

    public OrderItem getOrderItemFromDb(long orderItemId) {
        OrderItem o = null;
        Status().setMessage("Fetching order items from DB");

        String sql = "SELECT * FROM " + OrderItem.TABLE_NAME + " WHERE id = " + orderItemId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    o = new OrderItem();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setOrderId(rs.getLong("orderid"));
                    o.setItemId(rs.getLong("itemid"));
                    o.setAmount(rs.getInt("amount"));
                    o.setItemRef(rs.getString("itemref"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch order item from database");
        }
        return o;
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
        Status().setMessage("Removing \""+orderItem.getItem().toString()+"\" from \""+orderItem.getOrder().toString());

        String sql = scriptResource.readString("orderitems.sqlDeleteItemFromOrder");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, orderItem.getOrderId());
                stmt.setLong(2, orderItem.getItemId());
                stmt.execute();

                onTableChanged(OrderItem.TABLE_NAME, DbManager.OBJECT_DELETED, OrderItem.createDummyOrderItem(orderItem.getOrder(), orderItem.getItem()), null);
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
        Status().setMessage("Fetching distributors from DB");

        String sql = "SELECT * FROM " + Distributor.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Distributor d = new Distributor();
                    d.setId(rs.getLong("id"));
                    d.setName(rs.getString("name"));
                    d.setIconPath(rs.getString("iconpath"));
                    d.setWebsite(rs.getString("website"));

                    if (d.getId() != DbObject.UNKNOWN_ID) {
                        d.setOnTableChangedListener(this);
                        distributors.add(d);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch distributors from database");
        }
    }

    public Distributor getDistributorFromDb(long distributorId) {
        Distributor d = null;
        Status().setMessage("Fetching order items from DB");

        String sql = "SELECT * FROM " + Distributor.TABLE_NAME + " WHERE id = " + distributorId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    d = new Distributor();
                    d.setId(rs.getLong("id"));
                    d.setName(rs.getString("name"));
                    d.setIconPath(rs.getString("iconpath"));
                    d.setWebsite(rs.getString("website"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch distributor from database");
        }
        return d;
    }


    /*
    *                  PART NUMBERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<PartNumber> getPartNumbers()    {
        if (partNumbers == null) {
            updatePartNumbers();
        }
        return partNumbers;
    }

    private void updatePartNumbers()    {
        partNumbers = new ArrayList<>();
        Status().setMessage("Fetching part numbers from DB");

        String sql = scriptResource.readString(PartNumber.TABLE_NAME + ".sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    PartNumber pn = new PartNumber();
                    pn.setId(rs.getLong("id"));
                    pn.setName(rs.getString("name"));
                    pn.setIconPath(rs.getString("iconpath"));
                    pn.setDistributorId(rs.getLong("distributorid"));
                    pn.setItemId(rs.getLong("itemid"));
                    pn.setItemRef(rs.getString("distributoritemref"));

                    if (pn.getId() != DbObject.UNKNOWN_ID) {
                        pn.setOnTableChangedListener(this);
                        partNumbers.add(pn);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch part numbers from database");
        }
    }

    public PartNumber getPartNumberFromDb(long partNumberId) {
        PartNumber pn = null;
        Status().setMessage("Fetching part number from DB");

        String sql = "SELECT * FROM " + PartNumber.TABLE_NAME + " WHERE id = " + partNumberId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    pn = new PartNumber();
                    pn.setId(rs.getLong("id"));
                    pn.setName(rs.getString("name"));
                    pn.setIconPath(rs.getString("iconpath"));
                    pn.setDistributorId(rs.getLong("distributorid"));
                    pn.setItemId(rs.getLong("itemid"));
                    pn.setItemRef(rs.getString("distributoritemref"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch part number from database");
        }
        return pn;
    }

    public PartNumber findPartNumberFromDb(long distributorId, long itemId) {
        PartNumber pn = null;
        String sql = scriptResource.readString("partnumbers.sqlFindItemRef");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setLong(1, distributorId);
                stmt.setLong(2, itemId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        pn = new PartNumber();
                        pn.setId(rs.getLong("id"));
                        pn.setName(rs.getString("name"));
                        pn.setIconPath(rs.getString("iconpath"));
                        pn.setDistributorId(rs.getLong("distributorid"));
                        pn.setItemId(rs.getLong("itemid"));
                        pn.setItemRef(rs.getString("distributoritemref"));
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch part number from database");
            LOG.error("Error in findPartNumberFromDb.", e);
        }
        return pn;
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
        Status().setMessage("Fetching package types from DB");

        String sql = scriptResource.readString(PackageType.TABLE_NAME + ".sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    PackageType pt = new PackageType();
                    pt.setId(rs.getLong("id"));
                    pt.setName(rs.getString("name"));
                    pt.setDescription(rs.getString("description"));

                    if (pt.getId() != DbObject.UNKNOWN_ID) {
                        pt.setOnTableChangedListener(this);
                        packageTypes.add(pt);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch package types from database");
        }
    }

    public PackageType getPackageTypeFromDb(long packageTypeId) {
        PackageType pt = null;
        Status().setMessage("Fetching package type from DB");

        String sql = "SELECT * FROM " + PackageType.TABLE_NAME + " WHERE id = " + packageTypeId;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    pt = new PackageType();
                    pt.setId(rs.getLong("id"));
                    pt.setName(rs.getString("name"));
                    pt.setDescription(rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch package type from database");
        }
        return pt;
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
        Status().setMessage("Fetching projects from DB");

        String sql = scriptResource.readString(Project.TABLE_NAME + ".sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Project p = new Project();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));

                    // ProjectDirectories are fetched in object itself

                    if (p.getId() != DbObject.UNKNOWN_ID) {
                        p.setOnTableChangedListener(this);
                        projects.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch projects from database");
        }
    }

    public Project getProjectFromDb(long id) {
        Project p = null;
        Status().setMessage("Fetching project from DB");

        String sql = "SELECT * FROM " + Project.TABLE_NAME + " WHERE id = " + id;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new Project();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));

                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch project from database");
        }
        return p;
    }

    /*
    *                  PROJECT DIRECTORIES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<ProjectDirectory> getProjectDirectories()    {
        if (projectDirectories == null) {
            updateProjectDirectories();
        }
        return projectDirectories;
    }

    private void updateProjectDirectories()    {
        projectDirectories = new ArrayList<>();
        Status().setMessage("Fetching projectDirectories from DB");

        String sql = scriptResource.readString(ProjectDirectory.TABLE_NAME + ".sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    ProjectDirectory p = new ProjectDirectory();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));

                    p.setDirectory(rs.getString("directory"));
                    p.setProjectId(rs.getLong("projectid"));

                    if (p.getId() != DbObject.UNKNOWN_ID) {
                        p.setOnTableChangedListener(this);
                        projectDirectories.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch projectDirectories from database");
        }
    }

    public ProjectDirectory getProjectDirectoryFromDb(long id) {
        ProjectDirectory p = null;
        Status().setMessage("Fetching projectDirecty from DB");

        String sql = "SELECT * FROM " + ProjectDirectory.TABLE_NAME + " WHERE id = " + id;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new ProjectDirectory();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));

                    p.setDirectory(rs.getString("directory"));
                    p.setProjectId(rs.getLong("projectid"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch projectDirecty from database");
        }
        return p;
    }

    /*
    *                  PROJECT TYPES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<ProjectType> getProjectTypes()    {
        if (projectTypes == null) {
            updateProjectTypes();
        }
        return projectTypes;
    }

    private void updateProjectTypes()    {
        projectTypes = new ArrayList<>();
        Status().setMessage("Fetching ProjectType from DB");

        String sql = scriptResource.readString(ProjectType.TABLE_NAME + ".sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    ProjectType p = new ProjectType();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setIconPath(rs.getString("iconpath"));
                    p.setOpenAsFolder(rs.getBoolean("openasfolder"));
                    p.setUseDefaultLauncher(rs.getBoolean("usedefaultlauncher"));
                    p.setLauncherPath(rs.getString("launcherpath"));
                    p.setExtension(rs.getString("extension"));
                    p.setMatchExtension(rs.getBoolean("matchextension"));
                    p.setUseParentFolder(rs.getBoolean("useparentfolder"));
                    p.setParserName(rs.getString("parsername"));

                    if (p.getId() != DbObject.UNKNOWN_ID) {
                        p.setOnTableChangedListener(this);
                        projectTypes.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch ProjectType from database");
        }
    }

    public ProjectType getProjectTypeFromDb(long id) {
        ProjectType p = null;
        Status().setMessage("Fetching ProjectType from DB");

        String sql = "SELECT * FROM " + ProjectType.TABLE_NAME + " WHERE id = " + id;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    p = new ProjectType();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));

                    p.setExtension(rs.getString("extension"));
                    p.setOpenAsFolder(rs.getBoolean("openasfolder"));
                    p.setUseDefaultLauncher(rs.getBoolean("usedefaultlauncher"));
                    p.setLauncherPath(rs.getString("launcherpath"));
                    p.setMatchExtension(rs.getBoolean("matchextension"));
                    p.setUseParentFolder(rs.getBoolean("useparentfolder"));
                    p.setParserName(rs.getString("parsername"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch ProjectType from database");
        }
        return p;
    }


    /*
    *                  PROJECT TYPE LINKS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<ProjectTypeLink> getProjectTypeLinks()    {
        if (projectTypeLinks == null) {
            updateProjectTypeLinks();
        }
        return projectTypeLinks;
    }

    private void updateProjectTypeLinks()    {
        projectTypeLinks = new ArrayList<>();
        Status().setMessage("Fetching projectTypeLinks from DB");

        String sql = scriptResource.readString(ProjectTypeLink.TABLE_NAME + ".sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    ProjectTypeLink p = new ProjectTypeLink();
                    p.setId(rs.getLong("id"));
                    p.setProjectDirectoryId(rs.getLong("projectdirectoryid"));
                    p.setProjectTypeId(rs.getLong("projecttypeid"));
                    p.setFilePath(rs.getString("filepath"));

                    projectTypeLinks.add(p);
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch ProjectTypeLink from database");
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
        Status().setMessage("Fetching logs from DB");

        String sql = scriptResource.readString(Log.TABLE_NAME + ".sqlSelectAll");
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Log l = new Log();
                    l.setId(rs.getLong("id"));
                    l.setLogType(rs.getInt("logtype"));
                    l.setLogTime(rs.getDate("logtime"));
                    l.setLogClass(rs.getString("logclass"));
                    l.setLogMessage(rs.getString("logmessage"));
                    l.setLogException(rs.getString("logexception"));

                    logs.add(l);
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch logs from database", e);
        }
    }

    public Log getLogFromDb(long id) {
        Log l = null;
        Status().setMessage("Fetching log from DB");

        String sql = "SELECT * FROM " + ProjectType.TABLE_NAME + " WHERE id = " + id;
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    l = new Log();
                    l.setId(rs.getLong("id"));
                    l.setLogType(rs.getInt("logtype"));
                    l.setLogTime(rs.getDate("logdate"));
                    l.setLogClass(rs.getString("logclass"));
                    l.setLogMessage(rs.getString("logmessage"));
                    l.setLogException(rs.getString("logexception"));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch log from database");
        }
        return l;
    }

    /*
    *                  OTHER
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Product> getProductListForCategory(long categoryId)    {
        List<Product> products = new ArrayList<>();
        for (Product p : getProducts()) {
            if (p.getCategoryId() == categoryId) {
                products.add(p);
            }
        }
        return products;
    }

    public List<Type> getTypeListForProduct(long productId)    {
        List<com.waldo.inventory.classes.Type> types = new ArrayList<>();
        for (Type t : getTypes()) {
            if (t.getProductId() == productId) {
                types.add(t);
            }
        }
        return types;
    }

    public List<Item> getItemListForCategory(Category c)    {
        List<Item> items = new ArrayList<>();
        for (Item i : getItems()) {
            if (i.getCategoryId() == c.getId()) {
                items.add(i);
            }
        }
        return items;
    }

    public List<Item> getItemListForProduct(Product p)    {
        List<Item> items = new ArrayList<>();
        Category c = sm().findCategoryById(p.getCategoryId());
        for (Item i : getItemListForCategory(c)) {
            if (i.getProductId() == p.getId()) {
                items.add(i);
            }
        }
        return items;
    }

    public List<Item> getItemListForType(Type t)    {
        List<Item> items = new ArrayList<>();
        Product p = sm().findProductById(t.getProductId());
        for (Item i : getItemListForProduct(p)) {
            if (i.getTypeId() == t.getId()) {
                items.add(i);
            }
        }
        return items;
    }

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

    public List<ProjectDirectory> getProjectDirectoryListForProject(long projectId) {
        List<ProjectDirectory> directories = new ArrayList<>();
        for (ProjectDirectory directory : getProjectDirectories()) {
            if (directory.getProjectId() == projectId) {
                directories.add(directory);
            }
        }
        return directories;
    }

    public HashMap<ProjectType, ArrayList<File>> getProjectTypesForProjectDirectory(long directoryId) {
        HashMap<ProjectType, ArrayList<File>> projectTypes = new HashMap<>();
        for (ProjectTypeLink ptl : getProjectTypeLinks()) {
            if(ptl.getProjectDirectoryId() == directoryId) {
                if (projectTypes.containsKey(ptl.getProjectType())) {
                    projectTypes.computeIfAbsent(ptl.getProjectType(), k -> new ArrayList<>());
                } else {
                    projectTypes.put(ptl.getProjectType(), new ArrayList<>());
                }
                projectTypes.get(ptl.getProjectType()).add(ptl.getFile());
            }
        }
        return projectTypes;
    }

    public List<Project> getProjectForProjectType(long id) {
        List<Project> projects = new ArrayList<>();
        for(Project project : getProjects()) {
            for (ProjectDirectory pd : project.getProjectDirectories()) {
                for (ProjectType pt : pd.getProjectTypes().keySet()) {
                    if (pt.getId() == id) {
                        if (!projects.contains(project)) {
                            projects.add(project);
                        }
                    }
                }
            }

        }
        return projects;
    }

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
}
