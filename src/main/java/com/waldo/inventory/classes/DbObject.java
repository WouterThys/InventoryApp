package com.waldo.inventory.classes;

import com.waldo.inventory.managers.LogManager;

import java.sql.*;
import java.util.Comparator;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.Application.scriptResource;

public abstract class DbObject {

    private static final LogManager LOG = LogManager.LOG(DbObject.class);
    public static final int UNKNOWN_ID = 1;
    public static final String UNKNOWN_NAME = "Unknown";

    public static final String SQL_SELECT_ALL = ".sqlSelect.all";
    public static final String SQL_SELECT_ONE = ".sqlSelect.one";
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
    public static final int TYPE_PROJECT_TYPE = 14;
    public static final int TYPE_ORDER_FILE_FORMAT = 15;
    public static final int TYPE_SET_ITEM = 16;
    public static final int TYPE_DIMENSION_TYPE = 17;
    public static final int TYPE_LOCATION_TYPE = 18;
    public static final int TYPE_PROJECT_CODE = 19;
    public static final int TYPE_PROJECT_PCB = 20;
    public static final int TYPE_PROJECT_OTHER = 21;

    public static final int TYPE_KC_COMPONENT = 30;
    public static final int TYPE_KC_ITEM_LINK = 31;

    public static final int TYPE_LOG = 100;
    public static final int TYPE_DB_HISTORY = 101;

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
        if (dbObject instanceof ProjectIDE) return TYPE_PROJECT_TYPE;
        if (dbObject instanceof OrderFileFormat) return TYPE_ORDER_FILE_FORMAT;
        if (dbObject instanceof Package) return TYPE_PACKAGE;
        if (dbObject instanceof SetItem) return TYPE_SET_ITEM;
        if (dbObject instanceof DimensionType) return TYPE_DIMENSION_TYPE;
        if (dbObject instanceof PcbItem) return TYPE_KC_COMPONENT;
        if (dbObject instanceof PcbItemItemLink) return TYPE_KC_ITEM_LINK;
        if (dbObject instanceof LocationType) return TYPE_LOCATION_TYPE;
        if (dbObject instanceof Log) return TYPE_LOG;
        if (dbObject instanceof DbHistory) return TYPE_DB_HISTORY;
        if (dbObject instanceof ProjectCode) return  TYPE_PROJECT_CODE;
        if (dbObject instanceof ProjectPcb) return  TYPE_PROJECT_PCB;
        if (dbObject instanceof ProjectOther) return TYPE_PROJECT_OTHER;

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
        if (id == UNKNOWN_ID) {
            return "";
        }
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj instanceof DbObject) {
                if (((DbObject) obj).getId() == getId() && ((DbObject) obj).getName().equals(getName())) {
                    return true;
                }
                if (getId() < 0 || ((DbObject) obj).getId() < 0) {
                    return getName().equals(((DbObject) obj).getName());
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

        newObject.setInserted(isInserted);
        newObject.setCanBeSaved(false);
    }

    public static class DbObjectNameComparator<T extends DbObject> implements Comparator<T> {
        @Override
        public int compare(T dbo1, T dbo2) {
            if (dbo1.isUnknown() && !dbo2.isUnknown()) {
                return -1;
            }
            if (!dbo1.isUnknown() && dbo2.isUnknown()) {
                return 1;
            }
            return dbo1.getName().compareTo(dbo2.getName());
        }
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

    public void setInserted(boolean inserted) {
        this.isInserted = inserted;
    }
}
