package com.waldo.inventory.database;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.interfaces.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class DbManager implements TableChangedListener {

    private static final Logger LOG = LoggerFactory.getLogger(DbManager.class);

    public static final int OBJECT_ADDED = 0;
    public static final int OBJECT_UPDATED = 1;
    public static final int OBJECT_DELETED = 2;

    private static final DbManager INSTANCE = new DbManager();
    public static DbManager db() {
        return INSTANCE;
    }
    private BasicDataSource dataSource;
    private List<String> tableNames;

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

    private DbManager() {}

    public void init() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("net.sf.log4jdbc.DriverSpy");
        dataSource.setUrl("jdbc:log4jdbc:sqlite:data/inventory.db");
        dataSource.setUsername("waldo");
        dataSource.setPassword("");
        dataSource.setMaxIdle(600);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setLogAbandoned(true);
        dataSource.setRemoveAbandoned(true);
        dataSource.setMaxActive(-1);
        dataSource.setInitialSize(5);
        dataSource.setRemoveAbandonedTimeout(1);
        dataSource.setValidationQueryTimeout(5);

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();

        String sql = "PRAGMA foreign_keys=ON;";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            tableNames = getTableNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        if(dataSource != null) {
            Status().setMessage("Closing down");
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

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
        LOG.info(newObject.getName() + " " + how + tableName);
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

                    i.setOnTableChangedListener(this);
                    items.add(i);
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database: "+ e);
            e.printStackTrace();
        }
    }

    public void getItemsAsync(final List<Item> itemList) {
        if (itemList != null) {
            itemList.clear();
        }
        SwingWorker<Void, Item> worker = new SwingWorker<Void, Item>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Item> itemList = getItems();
                for(Item i : itemList) {
                    publish(i);
                }
                return null;
            }

            @Override
            protected void process(List<Item> chunks) {
                for (Item c : chunks) {
                    if (itemList != null) {
                        itemList.add(c);
                    }
                }
            }
        };
        worker.execute();
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
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database: "+ e);
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                    o.setDistributor(findDistributorById(rs.getLong("distributorid")));
                    o.setOrderItems(getOrderedItems(o.getId()));

                    if (o.getId() != 1) {
                        o.setOnTableChangedListener(this);
                        orders.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database");
            e.printStackTrace();
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
                    o.setDistributor(findDistributorById(rs.getLong("distributorid")));
                    o.setOrderItems(getOrderedItems(o.getId()));
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database");
            e.printStackTrace();
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

                    if (o.getId() != DbObject.UNKNOWN_ID) {
                        o.setOnTableChangedListener(this);
                        orderItems.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch items from database");
            e.printStackTrace();
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
                }
            }
        } catch (SQLException e) {
            Status().setError("Failed to fetch order item from database");
            e.printStackTrace();
        }
        return o;
    }

    public List<Item> getOrderedItems(long orderId) {
        List<Item> items = new ArrayList<>();
        for (OrderItem i : getOrderItems()) {
            if (i.getOrderId() == orderId || orderId == -1) {
                items.add(findItemById(i.getItemId()));
            }
        }
        return items;
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return d;
    }

    /*
    *                  FINDERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public Item findItemById(long id) {
        for (Item i : getItems()) {
            if(i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public Item findItemByName(String name)    {
        for (Item i : getItems()) {
            if (i.getName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    public Category findCategoryById(long id)    {
        for (Category c : getCategories()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Category findCategoryByName(String name)    {
        for (Category c : getCategories()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public int findCategoryIndex(long categoryId)    {
        for (int i = 0; i < getCategories().size(); i++) {
            if (getCategories().get(i).getId() == categoryId) {
                return i;
            }
        }
        return -1;
    }

    public Product findProductById(long id)    {
        for (Product p : getProducts()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Product findProductByName(String name)    {
        for (Product p : getProducts()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public int findProductIndex(long productNdx)    {
        for (int i = 0; i < getProducts().size(); i++) {
            if (getProducts().get(i).getId() == productNdx) {
                return i;
            }
        }
        return -1;
    }

    public Type findTypeById(long id)    {
        for (Type t : getTypes()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Type findTypeByName(String name)    {
        for (Type t : getTypes()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findTypeIndex(long typeNdx)    {
        for (int i = 0; i < getTypes().size(); i++) {
            if (getTypes().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public Manufacturer findManufacturerById(long id)    {
       for (Manufacturer m : getManufacturers()) {
           if (m.getId() == id) {
               return m;
           }
       }
       return null;
    }

    public Manufacturer findManufacturerByName(String name)    {
        for (Manufacturer m : getManufacturers()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public int findManufacturerIndex(long manufacturerId)    {
        for (int i = 0; i < getManufacturers().size(); i++) {
            if (getManufacturers().get(i).getId() == manufacturerId) {
                return i;
            }
        }
        return -1;
    }

    public Location findLocationById(long id)    {
        for (Location t : getLocations()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Location findLocationByName(String name)    {
        for (Location t : getLocations()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findLocationIndex(long typeNdx)    {
        for (int i = 0; i < getLocations().size(); i++) {
            if (getLocations().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public Order findOrderById(long id)    {
        for (Order t : getOrders()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Order findOrderByName(String name)    {
        for (Order t : getOrders()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findOrderIndex(long typeNdx)    {
        for (int i = 0; i < getOrders().size(); i++) {
            if (getOrders().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public OrderItem findOrderItemById(long id)    {
        for (OrderItem t : getOrderItems()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public OrderItem findOrderItemByName(String name)    {
        for (OrderItem t : getOrderItems()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findOrderItemIndex(long orderItemId)    {
        for (int i = 0; i < getOrders().size(); i++) {
            if (getOrderItems().get(i).getId() == orderItemId) {
                return i;
            }
        }
        return -1;
    }

    public Distributor findDistributorById(long distributorId) {
        for (Distributor d : getDistributors()) {
            if (d.getId() == distributorId) {
                return d;
            }
        }
        return null;
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
        Category c = findCategoryById(p.getCategoryId());
        for (Item i : getItemListForCategory(c)) {
            if (i.getProductId() == p.getId()) {
                items.add(i);
            }
        }
        return items;
    }

    public List<Item> getItemListForType(Type t)    {
        List<Item> items = new ArrayList<>();
        Product p = findProductById(t.getProductId());
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
}
