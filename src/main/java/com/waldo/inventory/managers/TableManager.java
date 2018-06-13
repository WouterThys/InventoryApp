package com.waldo.inventory.managers;

import com.waldo.inventory.database.classes.DbForeignKey;
import com.waldo.inventory.database.classes.DbTable;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.settings.settingsclasses.DbSettings;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableManager {

    private static final TableManager INSTANCE = new TableManager();

    public static TableManager dbTm() {
        return INSTANCE;
    }

    private static final String sqlLoadTables = "SELECT table_name FROM information_schema.tables where table_schema='%s';";
    private static final String sqlLoadTableData = "SELECT COLUMN_NAME,CONSTRAINT_NAME, REFERENCED_TABLE_NAME,REFERENCED_COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE REFERENCED_TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s';";

    private boolean initSuccess = false;
    private String schemaName = "";
    private BasicDataSource dataSource;
    private final List<DbTable> dbTableList = new ArrayList<>();

    private TableManager() {

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

    DbTable getDbTable(String tableName) {
        if (!initSuccess) return null;

        for (DbTable table : dbTableList) {
            if (table.getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    public List<DbTable> getDbTableList() {
        return dbTableList;
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

                        DbForeignKey fk = new DbForeignKey(table, columnName, constraintName, refTable, refColumn);
                        table.addForeignKey(fk);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DbObject> getForeignKeyReferences(DbObject object, DbForeignKey fk) {
        List<DbObject> objectList = new ArrayList<>();
        String sql = "select name, id from " + fk.getFromTable().getTableName() + " where " + fk.getFromColumn() + " = " + object.getId() + ";";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    DbObject obj = DbObject.createDummy(fk.getFromTable().getTableName(), rs.getString("name"), rs.getLong("id"));
                    objectList.add(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return objectList;
    }

}
