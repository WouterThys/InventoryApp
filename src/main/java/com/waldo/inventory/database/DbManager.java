package com.waldo.inventory.database;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.adapters.ItemListAdapter;
import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbManager {

    private static final DbManager INSTANCE = new DbManager();
    public static DbManager dbInstance() {
        return INSTANCE;
    }

    private BasicDataSource dataSource;
    private List<String> tableNames;

    private List<Category> categories;
    private List<Product> products;
    private List<Type> types;

    private DbManager() {}

    public void init() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:data/inventory.db");
        dataSource.setUsername("waldo");
        dataSource.setPassword("");

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
        List<String> names = new ArrayList<>();

        String sql = "SELECT * FROM main.sqlite_master WHERE type='table'";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("name"));
            }

            return names;
        }
    }

    public List<Item> getItems() throws SQLException {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT * FROM items ORDER BY id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Item i = new Item();
                i.setId(rs.getLong("id"));
                i.setName(rs.getString("name"));
                i.setDescription(rs.getString("description"));
                i.setPrice(rs.getDouble("price"));
                i.setCategory(rs.getInt("category"));
                i.setProduct(rs.getInt("product"));
                i.setType(rs.getInt("type"));
                i.setLocalDataSheet(rs.getString("localdatasheet"));
                i.setOnlineDataSheet(rs.getString("onlinedatasheet"));
                items.add(i);
            }

            return items;
        }
    }

    public void getItemsAsync(final ItemListAdapter listAdapter) {
        if (listAdapter != null) {
            listAdapter.removeAllItems();
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
                    if (listAdapter != null) {
                        listAdapter.add(c);
                    }
                }
            }
        };
        worker.execute();
    }

    public List<Category> getCategories() throws SQLException {
        if (categories == null) {
            categories = new ArrayList<>();

            String sql = "SELECT * FROM " + Category.TABLE_NAME + " ORDER BY id";
            try (PreparedStatement stmt = getConnection().prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Category i = new Category();
                    i.setId(rs.getLong("id"));
                    i.setName(rs.getString("name"));
                    categories.add(i);
                }
            }
        }
        return categories;
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
            products = new ArrayList<>();

            String sql = "SELECT * FROM " + Product.TABLE_NAME + " ORDER BY id";
            try (PreparedStatement stmt = getConnection().prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Product i = new Product();
                    i.setId(rs.getLong("id"));
                    i.setName(rs.getString("name"));
                    i.setCategoryId(rs.getLong("categoryid"));
                    products.add(i);
                }
            }
        }
        return products;
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
            types = new ArrayList<>();

            String sql = "SELECT * FROM " + Type.TABLE_NAME + " ORDER BY id";
            try (PreparedStatement stmt = getConnection().prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Type i = new Type();
                    i.setId(rs.getLong("id"));
                    i.setName(rs.getString("name"));
                    types.add(i);
                }
            }
        }
        return types;
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

}
