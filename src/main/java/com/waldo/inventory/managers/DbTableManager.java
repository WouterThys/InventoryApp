package com.waldo.inventory.managers;

import com.waldo.inventory.classes.database.DbTable;
import com.waldo.inventory.classes.database.ForeignKey;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbTableManager {


    private static final DbTableManager INSTANCE = new DbTableManager();

    public static DbTableManager dbTm() {
        return INSTANCE;
    }


    private static final String sqlLoadTables = "SELECT table_name FROM information_schema.tables where table_schema='%s';";
    private static final String sqlLoadTableData = "SELECT COLUMN_NAME,CONSTRAINT_NAME, REFERENCED_TABLE_NAME,REFERENCED_COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE REFERENCED_TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s';";

    private boolean initSuccess = false;
    private String schemaName = "";
    private BasicDataSource dataSource;
    private List<DbTable> dbTableList = new ArrayList<>();

    private DbTableManager() {

    }

    public void init(BasicDataSource dataSource, DbSettings dbSettings) {
        initSuccess = false;
        if (dataSource != null && dbSettings != null) {
            initSuccess = true;
            this.schemaName = dbSettings.getDbName();
            this.dataSource = dataSource;
            // Load all tables
            String sql = String.format(sqlLoadTables, dbSettings.getDbName());
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement stmt = connection.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        DbTable table = new DbTable(rs.getString("table_name"));
                        dbTableList.add(table);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                initSuccess = false;
            }
        }
    }

    public DbTable getDbTable(String tableName) {
        if (!initSuccess) return null;

        for (DbTable table : dbTableList) {
            if (table.getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    public void loadTableData(String tableName) {
        if (!initSuccess) return;

        DbTable table = getDbTable(tableName);
        if (table != null) {
            loadTableData(table);
        }
    }

    public void loadTableData(DbTable table) {
        if (!initSuccess) return;

        String sql = String.format(sqlLoadTableData, schemaName, table.getTableName());

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String refTableName = rs.getString("REFERENCED_TABLE_NAME");

                    DbTable refTable = getDbTable(refTableName);
                    if (refTable != null) {
                        String columnName = rs.getString("COLUMN_NAME");
                        String constraintName = rs.getString("CONSTRAINT_NAME");
                        String refColumn = rs.getString("REFERENCED_COLUMN_NAME");

                        ForeignKey fk = new ForeignKey(columnName, constraintName, refTable, refColumn);
                        table.addForeignKey(fk);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
