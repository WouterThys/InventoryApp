package com.waldo.inventory.database;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbManager {

    private static final DbManager INSTANCE = new DbManager();

    private BasicDataSource dataSource;

    public static DbManager getInstance() {
        return INSTANCE;
    }

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
    }

    public void close() {
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

    public BasicDataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getInstance().getDataSource().getConnection();
    }

    public List<Item> getItems() throws SQLException {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT * FROM items ORDER BY id";
        try (Connection connection = DbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
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
                items.add(i);
            }

            return items;
        }
    }

    public <T extends DbObject> List<T> getList() throws SQLException {
        List<T> list = new ArrayList<>();

        String sql = "SELECT * FROM " + T.TABLE_NAME + " ORDER BY id";
        try (Connection connection = DbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                T i = (T) new DbObject(T.TABLE_NAME);
                i.setId(rs.getLong("id"));
                i.setName(rs.getString("name"));
                list.add(i);
            }

            return list;
        }
    }
}
