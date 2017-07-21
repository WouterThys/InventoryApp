package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.database.interfaces.TableChangedListener;

import javax.swing.*;
import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.waldo.inventory.gui.Application.scriptResource;

public abstract class DbObject {

    private static final LogManager LOG = LogManager.LOG(DbObject.class);
    public static final int UNKNOWN_ID = 1;
    public static final String UNKNOWN_NAME = "Unknown";

    public static final String SQL_SELECT_ALL = "sqlSelect.all";
    public static final String SQL_SELECT_ONE = "sqlSelect.one";
    public static final String SQL_INSERT = "sqlInsert";
    public static final String SQL_UPDATE = "sqlUpdate";
    public static final String SQL_DELETE = "sqlDelete";

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_CATEGORY = 2;
    public static final int TYPE_PRODUCT = 3;
    public static final int TYPE_TYPE = 4;
    public static final int TYPE_MANUFACTURER = 5;
    public static final int TYPE_LOCATION = 6;
    public static final int TYPE_ORDER = 7;
    public static final int TYPE_ORDER_ITEM = 8;
    public static final int TYPE_DISTRIBUTOR = 9;
    public static final int TYPE_PACKAGE_TYPE = 10;
    public static final int TYPE_PACKAGE = 11;
    public static final int TYPE_PROJECT = 12;
    public static final int TYPE_PROJECT_DIRECTORY = 13;
    public static final int TYPE_PROJECT_TYPE = 14;
    public static final int TYPE_LOG = 100;

    protected String TABLE_NAME;

    protected long id = -1;
    protected String name = "";
    protected String iconPath = "";

    private TableChangedListener onTableChangedListener;
    private DbObject oldObject;
    protected boolean canBeSaved = true;

    protected DbObject(String tableName) {
        TABLE_NAME = tableName;
    }

    /**
     * Add the parameters to write this object to the database.
     * @param statement: PreparedStatement to add parameters to
     * @return next index to add parameters to
     * @throws SQLException exception thrown wen could not add a parameter
     */
    public abstract int addParameters(PreparedStatement statement) throws SQLException;

    int addBaseParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;
        statement.setString(ndx++, getName());
        statement.setString(ndx++, getIconPath());
        return ndx;
    }

    public static int getType(DbObject dbObject) {
        if (dbObject instanceof Item) return TYPE_ITEM;
        if (dbObject instanceof Category) return TYPE_CATEGORY;
        if (dbObject instanceof Product) return TYPE_PRODUCT;
        if (dbObject instanceof Type) return TYPE_TYPE;
        if (dbObject instanceof Distributor) return TYPE_DISTRIBUTOR;
        if (dbObject instanceof Location) return TYPE_LOCATION;
        if (dbObject instanceof Manufacturer) return TYPE_MANUFACTURER;
        if (dbObject instanceof Order) return TYPE_ORDER;
        if (dbObject instanceof OrderItem) return TYPE_ORDER_ITEM;
        if (dbObject instanceof PackageType) return TYPE_PACKAGE_TYPE;
        if (dbObject instanceof Project) return TYPE_PROJECT;
        if (dbObject instanceof ProjectDirectory) return TYPE_PROJECT_DIRECTORY;
        if (dbObject instanceof ProjectType) return TYPE_PROJECT_TYPE;
        if (dbObject instanceof Log) return TYPE_LOG;

        return TYPE_UNKNOWN;
    }


    protected void doSave() throws SQLException {
//        setOnTableChangedListener(DbManager.db());
//
//        long startTime = System.nanoTime();
//        try (Connection connection = DbManager.getConnection()) {
//            if (!connection.isValid(5)) {
//                throw new SQLException("Conenction invalid, timed out after 5s...");
//            }
//            LOG.debug("Connection is open");
//            if (id == -1) { // Save
//                try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
//                    insert(statement);
//
//                    try (ResultSet rs = statement.getGeneratedKeys()) {
//                        rs.next();
//                        id = rs.getLong(1);
//                    }
//                }
//            } else { // Update
//                // Save old object
//                setOldObject();
//                // Save new object
//                try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
//                    update(statement);
//                }
//            }
//        }
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime);
//        LOG.debug("Connection was open for: " + duration / 1000000 + "ms");
    }

    public void save() {
//        if (!canBeSaved) {
//            JOptionPane.showMessageDialog(null, "\"" + name + "\" can't be saved.", "Save warning", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        final long saveId = id;
//        SwingWorker worker = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {
//                try {
//                    LOG.debug("Start save.");
//                    doSave();
//                } catch (Exception e) {
//                    JOptionPane.showMessageDialog(null, "Failed to save object: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
//                    LOG.error("Failed to save object.", e);
//                }
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                if (saveId < 0) { // Save
//                    LOG.debug("Added object to " + TABLE_NAME);
//                    if (onTableChangedListener != null) {
//                        try {
//                            onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_INSERT, DbObject.this, null);
//                        } catch (SQLException e) {
//                            LOG.error("Error calling onTableChangedListener.", e);
//                        }
//                    }
//                } else { // Update
//                    LOG.debug("Updated object in " + TABLE_NAME);
//                    if (onTableChangedListener != null) {
//                        try {
//                            onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_UPDATE, DbObject.this, oldObject);
//                        } catch (SQLException e) {
//                            LOG.error("Error calling onTableChangedListener.", e);
//                        }
//                    }
//                }
//            }
//        };
//        worker.execute();
//        try {
//            worker.get(2, TimeUnit.SECONDS);
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//            LOG.error("Failed to save object.", e);
//        }
    }

    public void saveSynchronously() throws SQLException {
        if (!canBeSaved) {
            JOptionPane.showMessageDialog(null, "\"" + name + "\" can't be saved.", "Save warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final long saveId = id;

        LOG.debug("Start save.");
        doSave();

        if (saveId < 0) { // Save
            LOG.debug("Added object to " + TABLE_NAME);
            if (onTableChangedListener != null) {
                onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_INSERT, DbObject.this, null);
            }
        } else { // Update
            LOG.debug("Updated object in " + TABLE_NAME);
            if (onTableChangedListener != null) {
                onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_UPDATE, DbObject.this, oldObject);
            }

        }
    }

    protected void doDelete() throws SQLException {
//        if (id != -1) {
//            LOG.debug("Start deleting in " + TABLE_NAME);
//            setOldObject();
//            try (Connection connection = DbManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
//                statement.setLong(1, id);
//                statement.execute();
//                id = -1; // Not in database anymore
//            }
//
//            LOG.debug("Deleted object from " + TABLE_NAME);
//            if (onTableChangedListener != null) {
//                onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_DELETE, oldObject, null);
//            }
//        }
    }

    public void delete() {
//        if (canBeSaved) {
//            SwingWorker worker = new SwingWorker() {
//                @Override
//                protected Object doInBackground() throws Exception {
//                    doDelete();
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    try {
//                        get(10, TimeUnit.SECONDS);
//                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                        JOptionPane.showMessageDialog(null, "Error deleting \"" + name + "\". \n Exception: " + e.getMessage(), "Delete error", JOptionPane.ERROR_MESSAGE);
//                        LOG.error("Failed to delete object.", e);
//                    }
//                }
//            };
//            worker.execute();
//        } else {
//            JOptionPane.showMessageDialog(null, "\"" + name + "\" can't be deleted.", "Delete warning", JOptionPane.WARNING_MESSAGE);
//        }
    }

    @Override
    public String toString() {
        if (name == null) {
            return "(No name)";
        }
        if (id == -1) {
            if (canBeSaved) {
                return name + "*";
            }
        }
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj instanceof DbObject) {
                if (((DbObject) obj).getId() == id && ((DbObject) obj).getName().equals(name)) {
                    return true;
                }
                if (id < 0 || ((DbObject) obj).getId() < 0) {
                    return name.equals(((DbObject) obj).getName());
                }
            }
        }
        return false;
    }

    public boolean hasMatch(String searchTerm) {
        return getName().toUpperCase().contains(searchTerm.toUpperCase())
                || getIconPath().toUpperCase().contains(searchTerm.toUpperCase());
    }

    public abstract DbObject createCopy();

    public abstract DbObject createCopy(DbObject copyInto);

    public void copyBaseFields(DbObject newObject) {
        newObject.setId(getId());
        newObject.setName(getName());
        newObject.setIconPath(getIconPath());
        newObject.setCanBeSaved(false);
    }

//    private void setOldObject() {
//        switch (getType(this)) {
//            case TYPE_UNKNOWN:
//                oldObject = null;
//                break;
//            case TYPE_ITEM:
//                oldObject = DbManager.db().getItemFromDb(id);
//                break;
//            case TYPE_CATEGORY:
//                oldObject = DbManager.db().getCategoryFromDb(id);
//                break;
//            case TYPE_PRODUCT:
//                oldObject = DbManager.db().getProductFromDb(id);
//                break;
//            case TYPE_TYPE:
//                oldObject = DbManager.db().getTypeFromDb(id);
//                break;
//            case TYPE_MANUFACTURER:
//                oldObject = DbManager.db().getManufacturerFromDb(id);
//                break;
//            case TYPE_LOCATION:
//                oldObject = DbManager.db().getLocationFromDb(id);
//                break;
//            case TYPE_ORDER:
//                oldObject = DbManager.db().getOrderFromDb(id);
//                break;
//            case TYPE_ORDER_ITEM:
//                oldObject = DbManager.db().getOrderItemFromDb(id);
//                break;
//            case TYPE_DISTRIBUTOR:
//                oldObject = DbManager.db().getDistributorFromDb(id);
//                break;
//            case TYPE_PACKAGE_TYPE:
//                oldObject = DbManager.db().getPackageTypeFromDb(id);
//                break;
//            case TYPE_PROJECT:
//                oldObject = DbManager.db().getProjectFromDb(id);
//                break;
//            case TYPE_PROJECT_DIRECTORY:
//                oldObject = DbManager.db().getProjectDirectoryFromDb(id);
//                break;
//            case TYPE_PROJECT_TYPE:
//                oldObject = DbManager.db().getProjectTypeFromDb(id);
//                break;
//            case TYPE_LOG:
//                oldObject = DbManager.db().getLogFromDb(id);
//                break;
//        }
//    }

    public boolean isUnknown() {
        return id == UNKNOWN_ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        if (name == null) {
            name = "";
        }
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

    public boolean canBeSaved() {
        return canBeSaved;
    }

    public void setCanBeSaved(boolean canBeSaved) {
        this.canBeSaved = canBeSaved;
    }

    public String getScript(String scriptName) {
        return scriptResource.readString(TABLE_NAME + "." + scriptName);
    }
}
