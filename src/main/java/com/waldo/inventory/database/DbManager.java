package com.waldo.inventory.database;

import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.SQLException;

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
}
