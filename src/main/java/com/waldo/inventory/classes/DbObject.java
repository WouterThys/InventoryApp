package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.database.*;
import com.waldo.inventory.database.interfaces.TableChangedListener;
import com.waldo.inventory.gui.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class DbObject {

    private static final Logger LOG = LoggerFactory.getLogger(DbObject.class);
    public static final int UNKNOWN_ID = 1;
    public static final String UNKNOWN_NAME = "Unknown";

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_CATEGORY = 2;
    public static final int TYPE_PRODUCT = 3;
    public static final int TYPE_TYPE = 4;
    public static final int TYPE_MANUFACTURER = 5;
    public static final int TYPE_LOCATION = 6;
    public static final int TYPE_ORDER = 7;
    public static final int TYPE_ORDER_ITEM = 8;

    private String TABLE_NAME;

    protected long id = -1;
    protected String name = "";
    protected String iconPath = "";

    private TableChangedListener onTableChangedListener;
    private DbObject oldObject;

    private String sqlInsert;
    private String sqlUpdate;
    private String sqlDelete;

    protected ResourceManager scriptResource;

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

        URL url = DbObject.class.getResource("/db/scripts/scripts.properties");
        scriptResource = new ResourceManager(url.getPath());

        this.sqlInsert = scriptResource.readString(tableName + "." + "sqlInsert");
        this.sqlUpdate = scriptResource.readString(tableName + "." + "sqlUpdate");
        this.sqlDelete = scriptResource.readString(tableName + "." + "sqlDelete");

    }

    protected DbObject(String tableName, String sqlInsert, String sqlUpdate, String sqlDelete) {
        this(tableName);
        // Overwrite values
        this.sqlUpdate = sqlUpdate;
        this.sqlDelete = sqlDelete;
        this.sqlInsert = sqlInsert;
    }

    public static int getType(DbObject dbObject) {
        if (dbObject instanceof Item) return TYPE_ITEM;
        if (dbObject instanceof Category) return TYPE_CATEGORY;
        if (dbObject instanceof Product) return TYPE_PRODUCT;
        if (dbObject instanceof Type) return TYPE_TYPE;
        return TYPE_UNKNOWN;
    }

    public DbObject(String tableName, String sqlInsert, String sqlUpdate) {
        this(tableName, sqlInsert, sqlUpdate, "DELETE FROM " + tableName + " WHERE id = ?");
    }

    private void doSave() throws SQLException {
        setOnTableChangedListener(DbManager.db());

        long startTime = System.nanoTime();
        try (Connection connection = DbManager.getConnection()){
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
                // Save old object
                setOldObject();
                // Save new object
                try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
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
                            onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_ADDED, DbObject.this, null);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // Update
                    LOG.debug("Updated object in " + TABLE_NAME);
                    if (onTableChangedListener != null) {
                        try {
                            onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_UPDATED, DbObject.this, oldObject);
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
            setOldObject();
            try (Connection connection = DbManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
                statement.setLong(1, id);
                statement.execute();
                id = -1; // Not in database anymore
            }

            LOG.debug("Deleted object from " + TABLE_NAME);
            if (onTableChangedListener != null) {
                onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_DELETED, oldObject, null);
            }
        }
    }

    public void delete() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                doDelete();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(10, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    JOptionPane.showMessageDialog(null, "Error deleting \""+name+"\". \n Exception: " + e.getMessage(),"Delete error" ,JOptionPane.ERROR_MESSAGE);
                    LOG.error("Failed to delete object.", e);
                }
            }
        };
        worker.execute();

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
            if (((DbObject) obj).getId() == id && ((DbObject) obj).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMatch(String searchTerm) {
        return getName().toUpperCase().contains(searchTerm.toUpperCase())
                || getIconPath().toUpperCase().contains(searchTerm.toUpperCase());
    }

    private void setOldObject() {
        switch (getType(this)) {
            case TYPE_UNKNOWN :
                oldObject = null;
                break;
            case TYPE_ITEM:
                oldObject = DbManager.db().getItemFromDb(id);
                break;
            case TYPE_CATEGORY:
                oldObject = DbManager.db().getCategoryFromDb(id);
                break;
            case TYPE_PRODUCT:
                oldObject = DbManager.db().getProductFromDb(id);
                break;
            case TYPE_TYPE:
                oldObject = DbManager.db().getTypeFromDb(id);
                break;
            case TYPE_MANUFACTURER:
                oldObject = DbManager.db().getManufacturerFromDb(id);
                break;
            case TYPE_LOCATION:
                oldObject = DbManager.db().getLocationFromDb(id);
                break;
            case TYPE_ORDER:
                oldObject = DbManager.db().getOrderFromDb(id);
                break;
            case TYPE_ORDER_ITEM:
                oldObject = DbManager.db().getOrderItemFromDb(id);
                break;
        }
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
