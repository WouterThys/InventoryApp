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

public class DbManager implements TableChangedListener {

    private static final Logger LOG = LoggerFactory.getLogger(DbManager.class);

    public static final int OBJECT_ADDED = 0;
    public static final int OBJECT_UPDATED = 1;
    public static final int OBJECT_DELETED = 2;

    private static final DbManager INSTANCE = new DbManager();
    public static DbManager dbInstance() {
        return INSTANCE;
    }
    private BasicDataSource dataSource;
    private List<String> tableNames;

    // Events
    private List<ItemsChangedListener> onItemsChangedListenerList;
    private List<CategoriesChangedListener> onCategoriesChangedListenerList;
    private List<ProductsChangedListener> onProductsChangedListenerList;
    private List<TypesChangedListener> onTypesChangedListenerList;
    private List<ManufacturersChangedListener> onManufacturerChangedListenerList;
    private List<OrdersChangedListener> onOrdersChangedListenerList;
    private List<LocationChangedListener> onLocationsChangedListenerList;

    // Cached lists
    private List<Item> items;
    private List<Category> categories;
    private List<Product> products;
    private List<Type> types;
    private List<Manufacturer> manufacturers;
    private List<Location> locations;
    private List<Order> orders;

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

        try {
            tableNames = getTableNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        if(dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close();
            }
        }));
    }

    private BasicDataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dbInstance().getDataSource().getConnection();
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
    public void addOnItemsChangedListener(ItemsChangedListener onItemsChangedListener) {
        if (onItemsChangedListenerList == null) {
            onItemsChangedListenerList = new ArrayList<>();
        }
        if (!onItemsChangedListenerList.contains(onItemsChangedListener)) {
            onItemsChangedListenerList.add(onItemsChangedListener);
        }
    }

    public void addOnCategoriesChangedListener(CategoriesChangedListener onCategoriesChangedListener) {
        if (onCategoriesChangedListenerList == null) {
            onCategoriesChangedListenerList = new ArrayList<>();
        }
        if (!onCategoriesChangedListenerList.contains(onCategoriesChangedListener)) {
            onCategoriesChangedListenerList.add(onCategoriesChangedListener);
        }
    }

    public void addOnProductsChangedListener(ProductsChangedListener onProductsChangedListener) {
        if (onProductsChangedListenerList == null) {
            onProductsChangedListenerList = new ArrayList<>();
        }
        if (!onProductsChangedListenerList.contains(onProductsChangedListener)) {
            onProductsChangedListenerList.add(onProductsChangedListener);
        }
    }

    public void addOnTypesChangedListener(TypesChangedListener onTypesChangedListener) {
        if (onTypesChangedListenerList == null) {
            onTypesChangedListenerList = new ArrayList<>();
        }
        if (!onTypesChangedListenerList.contains(onTypesChangedListener)) {
            onTypesChangedListenerList.add(onTypesChangedListener);
        }
    }

    public void addOnManufacturerChangedListener(ManufacturersChangedListener onManufacturerChangedListener) {
        if (onManufacturerChangedListenerList == null) {
            onManufacturerChangedListenerList = new ArrayList<>();
        }
        if (!onManufacturerChangedListenerList.contains(onManufacturerChangedListener)) {
            onManufacturerChangedListenerList.add(onManufacturerChangedListener);
        }
    }

    public void addOnLocationsChangedListener(LocationChangedListener locationChangedListener) {
        if (onLocationsChangedListenerList == null) {
            onLocationsChangedListenerList = new ArrayList<>();
        }
        if (!onLocationsChangedListenerList.contains(locationChangedListener)) {
            onLocationsChangedListenerList.add(locationChangedListener);
        }
    }

    public void addOnOrdersChangedListener(OrdersChangedListener ordersChangedListener) {
        if (onOrdersChangedListenerList == null) {
            onOrdersChangedListenerList = new ArrayList<>();
        }
        if (!onOrdersChangedListenerList.contains(ordersChangedListener)) {
            onOrdersChangedListenerList.add(ordersChangedListener);
        }
    }


    public void removeOnItemsChangedListener(ItemsChangedListener onItemsChangedListener) {
        if (onItemsChangedListenerList != null) {
            if (onItemsChangedListenerList.contains(onItemsChangedListener)) {
                onItemsChangedListenerList.remove(onItemsChangedListener);
            }
        }
    }

    public void removeOnCategoriesChangedListener(CategoriesChangedListener onCategoriesChangedListener) {
        if (onCategoriesChangedListenerList != null) {
            if (onCategoriesChangedListenerList.contains(onCategoriesChangedListener)) {
                onCategoriesChangedListenerList.remove(onCategoriesChangedListener);
            }
        }
    }

    public void removeOnProductsChangedListener(ProductsChangedListener onProductsChangedListener) {
        if (onProductsChangedListenerList != null) {
            if (onProductsChangedListenerList.contains(onProductsChangedListener)) {
                onProductsChangedListenerList.remove(onProductsChangedListener);
            }
        }
    }

    public void removeOnTypesChangedListener(TypesChangedListener onTypesChangedListener) {
        if (onTypesChangedListenerList != null) {
            if (onTypesChangedListenerList.contains(onTypesChangedListener)) {
                onTypesChangedListenerList.remove(onTypesChangedListener);
            }
        }
    }

    public void removeOnManufacturersChangedListener(ManufacturersChangedListener onManufacturersChangedListener) {
        if (onManufacturerChangedListenerList != null) {
            if (onManufacturerChangedListenerList.contains(onManufacturersChangedListener)) {
                onManufacturerChangedListenerList.remove(onManufacturersChangedListener);
            }
        }
    }

    public void removeOnLocationChangedListener(LocationChangedListener locationChangedListener) {
        if (onLocationsChangedListenerList != null) {
            if (onLocationsChangedListenerList.contains(locationChangedListener)) {
                onLocationsChangedListenerList.remove(locationChangedListener);
            }
        }
    }

    public void removeOnOrdersChangedListener(OrdersChangedListener ordersChangedListener) {
        if (onOrdersChangedListenerList != null) {
            if (onOrdersChangedListenerList.contains(ordersChangedListener)) {
                onOrdersChangedListenerList.remove(ordersChangedListener);
            }
        }
    }


    private void notifyItemListListeners(int changedHow, DbObject object) {
        if (onItemsChangedListenerList != null) {
            for(ItemsChangedListener l : onItemsChangedListenerList) {
                LOG.debug("Notifying " + l);
                switch (changedHow) {
                    case OBJECT_ADDED:
                        l.onItemAdded((Item)object);
                        break;
                    case OBJECT_UPDATED:
                        l.onItemUpdated((Item)object);
                        break;
                    case OBJECT_DELETED:
                        l.onItemDeleted((Item)object);
                        break;
                }
            }
        }
    }

    private void notifyCategoryListListeners(int changedHow, DbObject object) {
        if (onCategoriesChangedListenerList != null) {
            if (onCategoriesChangedListenerList.size() > 0) {
                for (CategoriesChangedListener l : onCategoriesChangedListenerList) {
                    LOG.debug("Notifying " + l);
                    switch (changedHow) {
                        case OBJECT_ADDED:
                            l.onCategoryAdded((Category) object);
                            break;
                        case OBJECT_UPDATED:
                            l.onCategoryUpdated((Category) object);
                            break;
                        case OBJECT_DELETED:
                            l.onCategoryDeleted((Category) object);
                            break;
                    }
                }
            }
            else {
                LOG.debug("No one to notify for category change");
            }
        }  else {
            LOG.debug("No one to notify for category change");
        }
    }

    private void notifyProductListListeners(int changedHow, DbObject object) {
        if (onProductsChangedListenerList != null) {
            if (onProductsChangedListenerList.size() > 0) {
                for (ProductsChangedListener l : onProductsChangedListenerList) {
                    LOG.debug("Notifying " + l);
                    switch (changedHow) {
                        case OBJECT_ADDED:
                            l.onProductAdded((Product) object);
                            break;
                        case OBJECT_UPDATED:
                            l.onProductUpdated((Product) object);
                            break;
                        case OBJECT_DELETED:
                            l.onProductDeleted((Product) object);
                            break;
                    }
                }
            } else {
                LOG.debug("No one to notify for product change");
            }
        } else {
            LOG.debug("No one to notify for product change");
        }
    }

    private void notifyTypeListListeners(int changedHow, DbObject object) {
        if (onTypesChangedListenerList != null) {
            if (onTypesChangedListenerList.size() > 0) {
                for (TypesChangedListener l : onTypesChangedListenerList) {
                    LOG.debug("Notifying " + l);
                    switch (changedHow) {
                        case OBJECT_ADDED:
                            l.onTypeAdded((Type) object);
                            break;
                        case OBJECT_UPDATED:
                            l.onTypeUpdated((Type) object);
                            break;
                        case OBJECT_DELETED:
                            l.onTypeDeleted((Type) object);
                            break;
                    }
                }
            } else {
                LOG.debug("No one to notify for types change");
            }
        } else {
            LOG.debug("No one to notify for types change");
        }
    }

    private void notifyManufacturersListListeners(int changedHow, DbObject object) {
        if (onManufacturerChangedListenerList != null) {
            if (onManufacturerChangedListenerList.size() > 0) {
                for (ManufacturersChangedListener l : onManufacturerChangedListenerList) {
                    LOG.debug("Notifying " + l);
                    switch (changedHow) {
                        case OBJECT_ADDED:
                            l.onManufacturerAdded((Manufacturer) object);
                            break;
                        case OBJECT_UPDATED:
                            l.onManufacturerUpdated((Manufacturer) object);
                            break;
                        case OBJECT_DELETED:
                            l.onManufacturerDeleted((Manufacturer) object);
                            break;
                    }
                }
            } else {
                LOG.debug("No one to notify for manufacturers change");
            }
        } else {
            LOG.debug("No one to notify for manufacturers change");
        }
    }

    private void notifyLocationListListeners(int changedHow, DbObject object) {
        if (onLocationsChangedListenerList != null) {
            if (onLocationsChangedListenerList.size() > 0) {
                for (LocationChangedListener l : onLocationsChangedListenerList) {
                    LOG.debug("Notifying " + l);
                    switch (changedHow) {
                        case OBJECT_ADDED:
                            l.onLocationAdded((Location) object);
                            break;
                        case OBJECT_UPDATED:
                            l.onLocationUpdated((Location) object);
                            break;
                        case OBJECT_DELETED:
                            l.onLocationDeleted((Location) object);
                            break;
                    }
                }
            } else {
                LOG.debug("No one to notify for Location change");
            }
        } else {
            LOG.debug("No one to notify for Location change");
        }
    }

    private void notifyOrdersListListeners(int changedHow, DbObject object) {
        if (onOrdersChangedListenerList != null) {
            if (onOrdersChangedListenerList.size() > 0) {
                for (OrdersChangedListener l : onOrdersChangedListenerList) {
                    LOG.debug("Notifying " + l);
                    switch (changedHow) {
                        case OBJECT_ADDED:
                            l.onOrderAdded((Order) object);
                            break;
                        case OBJECT_UPDATED:
                            l.onOrderUpdated((Order) object);
                            break;
                        case OBJECT_DELETED:
                            l.onOrderDeleted((Order) object);
                            break;
                    }
                }
            } else {
                LOG.debug("No one to notify for Order change");
            }
        } else {
            LOG.debug("No one to notify for Order change");
        }
    }

    @Override
    public void onTableChanged(String tableName, int changedHow, DbObject object) throws SQLException {
        String how = "";
        switch (changedHow) {
            case OBJECT_ADDED: how = "added"; break;
            case OBJECT_UPDATED: how = "updated"; break;
            case OBJECT_DELETED: how = "deleted"; break;
        }
        LOG.info(object.getName() + " " + how + " in/from " + tableName);

        switch (tableName) {
            case Item.TABLE_NAME:
                updateItems();
                notifyItemListListeners(changedHow, object);
                break;
            case Category.TABLE_NAME:
                updateCategories();
                notifyCategoryListListeners(changedHow, object);
                break;
            case Product.TABLE_NAME:
                updateProducts();
                notifyProductListListeners(changedHow, object);
                break;
            case Type.TABLE_NAME:
                updateTypes();
                notifyTypeListListeners(changedHow, object);
                break;
            case Manufacturer.TABLE_NAME:
                updateManufacturers();
                notifyManufacturersListListeners(changedHow, object);
                break;
            case Location.TABLE_NAME:
                updateLocations();
                notifyLocationListListeners(changedHow, object);
                break;
            case Order.TABLE_NAME:
                updateOrders();
                notifyOrdersListListeners(changedHow, object);
                break;
        }
    }


    /*
    *                  ITEMS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Item> getItems() throws SQLException {
        if (items == null) {
            updateItems();
        }
        return items;
    }

    private void updateItems() throws SQLException {
        items = new ArrayList<>();

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


                        i.setOnTableChangedListener(this);
                        items.add(i);

                }
            }
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


    /*
    *                  CATEGORIES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Category> getCategories() throws SQLException {
        if (categories == null) {
            updateCategories();
        }
        return categories;
    }

    private void updateCategories() throws SQLException {
        categories = new ArrayList<>();

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
        }
        categories.add(0, Category.getUnknownCategory());
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
    public List<Product> getProducts() throws SQLException {
        if (products == null) {
            updateProducts();
        }
        return products;
    }

    private void updateProducts() throws SQLException {
        products = new ArrayList<>();

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
        }

        products.add(0, Product.getUnknownProduct());
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
    public List<Type> getTypes() throws SQLException {
        if (types == null) {
            updateTypes();
        }
        return types;
    }

    private void updateTypes() throws SQLException {
        types = new ArrayList<>();

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
        }
        types.add(0, Type.getUnknownType());
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
    public List<Manufacturer> getManufacturers() throws SQLException {
        if (manufacturers == null) {
            updateManufacturers();
        }
        return manufacturers;
    }

    private void updateManufacturers() throws SQLException {
        manufacturers = new ArrayList<>();

        String sql = "SELECT * FROM " + Manufacturer.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Manufacturer m = new Manufacturer();
                    m.setId(rs.getLong("id"));
                    m.setName(rs.getString("name"));
                    m.setIconPath(rs.getString("iconpath"));

                    if (m.getId() != 1) {
                        m.setOnTableChangedListener(this);
                        manufacturers.add(m);
                    }
                }
            }
        }
        manufacturers.add(0, Manufacturer.getUnknownManufacturer());
    }

    /*
    *                  LOCATIONS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Location> getLocations() throws SQLException {
        if (locations == null) {
            updateLocations();
        }
        return locations;
    }

    private void updateLocations() throws SQLException {
        locations = new ArrayList<>();

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
        }
        locations.add(0, Location.getUnknownLocation());
    }

    /*
    *                  ORDERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Order> getOrders() throws SQLException {
        if (orders == null) {
            updateOrders();
        }
        return orders;
    }

    private void updateOrders() throws SQLException {
        orders = new ArrayList<>();

        String sql = "SELECT * FROM " + Order.TABLE_NAME + " ORDER BY name";
        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Order o = new Order();
                    o.setId(rs.getLong("id"));
                    o.setName(rs.getString("name"));
                    o.setIconPath(rs.getString("iconpath"));

                    if (o.getId() != 1) {
                        o.setOnTableChangedListener(this);
                        orders.add(o);
                    }
                }
            }
        }
        orders.add(0, Order.getUnknownOrder());
    }


    /*
    *                  FINDERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public Item findItemById(long id) throws  SQLException {
        for (Item i : getItems()) {
            if(i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public Item findItemByName(String name) throws SQLException {
        for (Item i : getItems()) {
            if (i.getName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    public Category findCategoryById(long id) throws SQLException {
        for (Category c : getCategories()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Category findCategoryByName(String name) throws SQLException {
        for (Category c : getCategories()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public int findCategoryIndex(long categoryNdx) throws SQLException {
        for (int i = 0; i < getCategories().size(); i++) {
            if (getCategories().get(i).getId() == categoryNdx) {
                return i;
            }
        }
        return -1;
    }

    public Product findProductById(long id) throws SQLException {
        for (Product p : getProducts()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Product findProductByName(String name) throws SQLException {
        for (Product p : getProducts()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public int findProductIndex(long productNdx) throws SQLException {
        for (int i = 0; i < getProducts().size(); i++) {
            if (getProducts().get(i).getId() == productNdx) {
                return i;
            }
        }
        return -1;
    }

    public Type findTypeById(long id) throws SQLException {
        for (Type t : getTypes()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Type findTypeByName(String name) throws SQLException {
        for (Type t : getTypes()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findTypeIndex(long typeNdx) throws SQLException {
        for (int i = 0; i < getTypes().size(); i++) {
            if (getTypes().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public Manufacturer findManufacturerById(long id) throws SQLException {
       for (Manufacturer m : getManufacturers()) {
           if (m.getId() == id) {
               return m;
           }
       }
       return null;
    }

    public Manufacturer findManufacturerByName(String name) throws SQLException {
        for (Manufacturer m : getManufacturers()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public int findManufacturerIndex(long typeNdx) throws SQLException {
        for (int i = 0; i < getManufacturers().size(); i++) {
            if (getManufacturers().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public Location findLocationById(long id) throws SQLException {
        for (Location t : getLocations()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Location findLocationByName(String name) throws SQLException {
        for (Location t : getLocations()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findLocationIndex(long typeNdx) throws SQLException {
        for (int i = 0; i < getLocations().size(); i++) {
            if (getLocations().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }

    public Order findOrderById(long id) throws SQLException {
        for (Order t : getOrders()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Order findOrderByName(String name) throws SQLException {
        for (Order t : getOrders()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int findOrderIndex(long typeNdx) throws SQLException {
        for (int i = 0; i < getOrders().size(); i++) {
            if (getOrders().get(i).getId() == typeNdx) {
                return i;
            }
        }
        return -1;
    }
    /*
    *                  OTHER
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Product> getProductListForCategory(long categoryId) throws SQLException {
        List<Product> products = new ArrayList<>();
        for (Product p : getProducts()) {
            if (p.getCategoryId() == categoryId) {
                products.add(p);
            }
        }
        return products;
    }

    public List<Type> getTypeListForProduct(long productId) throws SQLException {
        List<com.waldo.inventory.classes.Type> types = new ArrayList<>();
        for (Type t : getTypes()) {
            if (t.getProductId() == productId) {
                types.add(t);
            }
        }
        return types;
    }
}
