package com.waldo.inventory.classes;

import com.waldo.inventory.database.*;
import com.waldo.inventory.database.interfaces.TableChangedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DbObject {

    private static final Logger LOG = LoggerFactory.getLogger(DbObject.class);
    public static final int UNKNOWN_ID = 1;
    public static final String UNKNOWN_NAME = "Unknown";

    private String TABLE_NAME;

    protected long id = -1;
    protected String name = "";
    protected String iconPath = "";

    private TableChangedListener onTableChangedListener;

    private String sqlInsert;
    private String sqlUpdate;
    private String sqlDelete;

    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.execute();
    }

    protected void update(PreparedStatement statement) throws SQLException {
        statement.setString(1, name); // Set (name)
        statement.setString(2, iconPath); // Set (icon path)
        statement.setLong(3, id); // Where id
        statement.execute();
    }

    protected DbObject(String tableName) {
        TABLE_NAME = tableName;

        this.sqlInsert = "INSERT INTO " + TABLE_NAME + " (name, iconpath) VALUES (?, ?)";
        this.sqlUpdate = "UPDATE " + TABLE_NAME + " SET name = ?, iconpath = ? WHERE id = ?";
        this.sqlDelete = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
    }

    protected DbObject(String tableName, String sqlInsert, String sqlUpdate, String sqlDelete) {
        this(tableName);
        // Overwrite values
        this.sqlUpdate = sqlUpdate;
        this.sqlDelete = sqlDelete;
        this.sqlInsert = sqlInsert;
    }

    public DbObject(String tableName, String sqlInsert, String sqlUpdate) {
        this(tableName, sqlInsert, sqlUpdate, "DELETE FROM " + tableName + " WHERE id = ?");
    }

    private void doSave() throws SQLException {
        setOnTableChangedListener(DbManager.dbInstance());

        long startTime = System.nanoTime();
        try (Connection connection = DbManager.getConnection();){
            if (!connection.isValid(5)) {
                throw new SQLException("Conenction invalid, timed out after 5s...");
            }
            LOG.debug("Connection is open");
            if (id == -1) { // Save
                try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)){
                    insert(statement);

                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getLong(1);
                    }
                }
            } else { // Update
                try (PreparedStatement statement = connection.prepareStatement(sqlUpdate);) {
                    update(statement);
                }
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        LOG.debug("Connection was open for: " + duration/1000000 + "ms");
    }

    public void save() {
        final long saveId = id;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    LOG.debug("Start save.");
                    doSave();
                } catch (Exception e) {
                    LOG.error("Failed to save object.", e);
                }
                return null;
            }

            @Override
            protected void done() {
                if (saveId < 0) { // Save
                    LOG.debug("Added object to " + TABLE_NAME);
                    if (onTableChangedListener != null) {
                        try {
                            onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_ADDED, DbObject.this);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // Update
                    LOG.debug("Updated object in " + TABLE_NAME);
                    if (onTableChangedListener != null) {
                        try {
                            onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_UPDATED, DbObject.this);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        worker.execute();
        try {
            worker.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOG.error("Failed to save object.", e);
        }
    }

    private void doDelete() throws SQLException {
        if (id != -1) {
            LOG.debug("Start deleting in " + TABLE_NAME);
            try (Connection connection = DbManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
                statement.setLong(1, id);
                statement.execute();
                id = -1; // Not in database anymore
            }

            LOG.debug("Deleted object from " + TABLE_NAME);
            if (onTableChangedListener != null) {
                onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_DELETED, this);
            }
        }
    }

    public void delete() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    doDelete();
                } catch (Exception e) {
                    LOG.error("Failed to delete object.", e);
                }
                return null;
            }
        };
        worker.execute();
        try {
            worker.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOG.error("Failed to delete object.", e);
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DbObject) {
            if (((DbObject) obj).getId() == id) {
                return true;
            }
        }
        return false;
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

    public String getIconPath() {
        if (iconPath != null) {
            return iconPath;
        }
        return "";
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setOnTableChangedListener(TableChangedListener tableChangedListenerListener) {
        this.onTableChangedListener = tableChangedListenerListener;
    }
}
