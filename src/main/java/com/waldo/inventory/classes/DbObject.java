package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.*;

public class DbObject {

    public static String TABLE_NAME;

    protected long id = -1;
    protected String name;

    protected String sqlInsert = "INSERT INTO " + TABLE_NAME + " (name) VALUES (?)";
    protected String sqlUpdate = "UPDATE " + TABLE_NAME + " SET name = ? WHERE id = ?";
    protected String sqlDelete = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
    }

    protected void update(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
    }

    public DbObject(String tableName) {
        TABLE_NAME = tableName;
    }

    protected DbObject(String tableName, String sqlInsert, String sqlUpdate, String sqlDelete) {
        this(tableName);
        this.sqlUpdate = sqlUpdate;
        this.sqlDelete = sqlDelete;
        this.sqlInsert = sqlInsert;
    }

    public void save() throws SQLException {
        try (Connection connection = DbManager.getConnection()) {
            if (id == -1) { // Save
                try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    insert(statement);
                    statement.execute();

                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getLong(1);
                    }
                }
            } else { // Update
                try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
                    update(statement);
                    statement.execute();
                }
            }

        }
    }

    public void delete() throws SQLException {
        if (id != -1) {
            try (Connection connection = DbManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
                statement.setLong(1, id);
                statement.execute();
                id = -1; // Contact is not in database
            }
        }
    }

    @Override
    public String toString() {
        if (name == null) {
            return "(No name)";
        }
        if (id == -1) {
            return name + "*";
        }
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
