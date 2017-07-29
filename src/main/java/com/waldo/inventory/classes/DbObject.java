package com.waldo.inventory.classes;

import com.waldo.inventory.database.LogManager;

import java.sql.*;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.Application.scriptResource;

public abstract class DbObject {

    private static final LogManager LOG = LogManager.LOG(DbObject.class);
    public static final int UNKNOWN_ID = 1;
    public static final String UNKNOWN_NAME = "Unknown";

    public static final String SQL_SELECT_ALL = ".sqlSelect.all";
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
    public static final int TYPE_ORDER_FILE_FORMAT = 15;
    public static final int TYPE_SET_ITEM = 16;
    public static final int TYPE_LOG = 100;

    protected String TABLE_NAME;
    protected boolean isInserted = false;

    protected long id = -1;
    protected String name = "";
    protected String iconPath = "";
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
        if (dbObject instanceof OrderFileFormat) return TYPE_ORDER_FILE_FORMAT;
        if (dbObject instanceof Package) return TYPE_PACKAGE;
        if (dbObject instanceof SetItem) return TYPE_SET_ITEM;
        if (dbObject instanceof Log) return TYPE_LOG;

        return TYPE_UNKNOWN;
    }

    public void save() {
        if (canBeSaved) {
            if (id < 0 && !isInserted) {
                db().insert(this);
                isInserted = true;
            } else {
                db().update(this);
            }
        }
    }

    public abstract void tableChanged(int changedHow);


    public void delete() {
        if (canBeSaved) {
            if (id >= UNKNOWN_ID) {
                db().delete(this);
            }
        }
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

    public boolean isUnknown() {
        return id == UNKNOWN_ID;
    }

    public String getTableName() {
        return TABLE_NAME;
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
