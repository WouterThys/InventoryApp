package com.waldo.inventory.database;

import com.waldo.inventory.classes.*;
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

    private List<ItemsChangedListener> onItemsChangedListenerList;
    private List<CategoriesChangedListener> onCategoriesChangedListenerList;
    private List<ProductsChangedListener> onProductsChangedListenerList;
    private List<TypesChangedListener> onTypesChangedListenerList;

    private BasicDataSource dataSource;
    private List<String> tableNames;

    private List<Item> items;
    private List<Category> categories;
    private List<Product> products;
    private List<Type> types;

    private DbManager() {}

    public void init() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("net.sf.log4jdbc.DriverSpy");
        dataSource.setUrl("jdbc:log4jdbc:sqlite:data/inventory.db");
        dataSource.setUsername("waldo");
        dataSource.setPassword("");
        dataSource.setMaxIdle(600);
        dataSource.setPoolPreparedStatements(true);
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

    public List<String> getTableNames() throws SQLException {
        if (tableNames == null) {
            tableNames = new ArrayList<>();

            String sql = "SELECT * FROM main.sqlite_master WHERE type='table'";
            try (PreparedStatement stmt = getConnection().prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    tableNames.add(rs.getString("name"));
                }
            }
        }
        return tableNames;
    }

    public List<Item> getItems() throws SQLException {
        if (items == null) {
            updateItems();
        }
        return items;
    }

    private void updateItems() throws SQLException {
        items = new ArrayList<>();

        String sql = "SELECT * FROM items ORDER BY id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Item i = new Item();
                i.setId(rs.getLong("id"));
                i.setName(rs.getString("name"));
                i.setIconPath(rs.getString("iconpath"));
                i.setDescription(rs.getString("description"));
                i.setPrice(rs.getDouble("price"));
                i.setCategory(rs.getInt("category"));
                i.setProduct(rs.getInt("product"));
                i.setType(rs.getInt("type"));
                i.setLocalDataSheet(rs.getString("localdatasheet"));
                i.setOnlineDataSheet(rs.getString("onlinedatasheet"));

                i.setOnTableChangedListener(this);
                items.add(i);
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

    public List<Category> getCategories() throws SQLException {
        if (categories == null) {
            updateCategories();
        }
        return categories;
    }

    private void updateCategories() throws SQLException {
        categories = new ArrayList<>();

        String sql = "SELECT * FROM " + Category.TABLE_NAME + " ORDER BY id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setIconPath(rs.getString("iconpath"));

                c.setOnTableChangedListener(this);
                categories.add(c);
            }
        }
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

    public List<Product> getProducts() throws SQLException {
        if (products == null) {
            updateProducts();
        }
        return products;
    }

    private void updateProducts() throws SQLException {
        products = new ArrayList<>();

        String sql = "SELECT * FROM " + Product.TABLE_NAME + " ORDER BY id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setIconPath(rs.getString("iconpath"));
                p.setCategoryId(rs.getLong("categoryid"));

                p.setOnTableChangedListener(this);
                products.add(p);
            }
        }
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

    public List<Type> getTypes() throws SQLException {
        if (types == null) {
            updateTypes();
        }
        return types;
    }

    private void updateTypes() throws SQLException {
        types = new ArrayList<>();

        String sql = "SELECT * FROM " + Type.TABLE_NAME + " ORDER BY id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Type t = new Type();
                t.setId(rs.getLong("id"));
                t.setName(rs.getString("name"));
                t.setIconPath(rs.getString("iconpath"));
                t.setProductId(rs.getLong("productid"));

                t.setOnTableChangedListener(this);
                types.add(t);
            }
        }
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

    public Item findItemById(long id) throws  SQLException {
        for (Item i : getItems()) {
            if(i.getId() == id) {
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

    public int findProductIndex(Product product) throws SQLException {
        Product p = findProductById(product.getId());
        if (p != null) {
            return getProducts().indexOf(p);
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
        }
    }
}
