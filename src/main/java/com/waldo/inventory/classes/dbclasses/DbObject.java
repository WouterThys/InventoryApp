package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.Statics.QueryType;
import com.waldo.inventory.classes.AddUpdateDelete;
import com.waldo.inventory.classes.search.SearchMatch;
import com.waldo.inventory.managers.LogManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.waldo.inventory.database.DatabaseAccess.db;
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
    public static final int TYPE_DIVISION = 2;
    public static final int TYPE_MANUFACTURER = 5;
    public static final int TYPE_LOCATION = 6;
    public static final int TYPE_ORDER = 7;
    public static final int TYPE_ORDER_LINE = 8;
    public static final int TYPE_DISTRIBUTOR = 9;
    public static final int TYPE_PACKAGE_TYPE = 10;
    public static final int TYPE_PACKAGE = 11;
    public static final int TYPE_PROJECT = 12;
    public static final int TYPE_PROJECT_TYPE = 14;
    public static final int TYPE_ORDER_FILE_FORMAT = 15;
    public static final int TYPE_LOCATION_TYPE = 18;
    public static final int TYPE_PROJECT_CODE = 19;
    public static final int TYPE_PROJECT_PCB = 20;
    public static final int TYPE_PROJECT_OTHER = 21;
    public static final int TYPE_PARSER_ITEM_LINK = 22;
    public static final int TYPE_SET = 23;
    public static final int TYPE_SET_ITEM_LINK = 24;
    public static final int TYPE_PENDING_ORDER = 25;
    public static final int TYPE_CREATED_PCB = 26;
    public static final int TYPE_CREATED_PCB_LINK = 27;
    public static final int TYPE_ORDER_PCB = 28;

    public static final int TYPE_KC_COMPONENT = 30;
    public static final int TYPE_KC_ITEM_LINK = 31;

    public static final int TYPE_LOG = 100;
    public static final int TYPE_DB_HISTORY = 101;
    public static final int TYPE_STATISTICS = 102;

    protected final String TABLE_NAME;
    protected boolean isInserted = false;
    protected boolean isDeleted = false;

    protected long id = -1;
    protected String name = "";
    protected String iconPath = "";

    protected long imageId;

    protected boolean canBeSaved = true;
    protected final AddUpdateDelete aud = new AddUpdateDelete();

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
        if (dbObject instanceof Set) return TYPE_SET;
        if (dbObject instanceof Item) return TYPE_ITEM;
        if (dbObject instanceof Division) return TYPE_DIVISION;
        if (dbObject instanceof Distributor) return TYPE_DISTRIBUTOR;
        if (dbObject instanceof Location) return TYPE_LOCATION;
        if (dbObject instanceof Manufacturer) return TYPE_MANUFACTURER;
        if (dbObject instanceof ItemOrder) return TYPE_ORDER;
        if (dbObject instanceof ItemOrderLine) return TYPE_ORDER_LINE;
        if (dbObject instanceof PackageType) return TYPE_PACKAGE_TYPE;
        if (dbObject instanceof Project) return TYPE_PROJECT;
        if (dbObject instanceof ProjectIDE) return TYPE_PROJECT_TYPE;
        if (dbObject instanceof OrderFileFormat) return TYPE_ORDER_FILE_FORMAT;
        if (dbObject instanceof Package) return TYPE_PACKAGE;
        if (dbObject instanceof PcbItem) return TYPE_KC_COMPONENT;
        if (dbObject instanceof PcbItemItemLink) return TYPE_KC_ITEM_LINK;
        if (dbObject instanceof LocationType) return TYPE_LOCATION_TYPE;
        if (dbObject instanceof Log) return TYPE_LOG;
        if (dbObject instanceof DbHistory) return TYPE_DB_HISTORY;
        if (dbObject instanceof ProjectCode) return  TYPE_PROJECT_CODE;
        if (dbObject instanceof ProjectPcb) return  TYPE_PROJECT_PCB;
        if (dbObject instanceof ProjectOther) return TYPE_PROJECT_OTHER;
        if (dbObject instanceof ParserItemLink) return TYPE_PARSER_ITEM_LINK;
        if (dbObject instanceof SetItemLink) return TYPE_SET_ITEM_LINK;
        if (dbObject instanceof Statistics) return TYPE_STATISTICS;
        if (dbObject instanceof PendingOrder) return TYPE_PENDING_ORDER;

        return TYPE_UNKNOWN;
    }

    public static DbObject createDummy(String tableName, String objectName, long objectId) {
        DbObject obj = new DbObject(tableName) {
            @Override
            public int addParameters(PreparedStatement statement) {
                return 0;
            }

            @Override
            public void tableChanged(QueryType changedHow) {

            }

            @Override
            public DbObject createCopy() {
                return null;
            }

            @Override
            public DbObject createCopy(DbObject copyInto) {
                return null;
            }
        };
        obj.setName(objectName);
        obj.setId(objectId);
        obj.setCanBeSaved(false);
        obj.setInserted(true);

        return obj;
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

    public abstract void tableChanged(QueryType changedHow);

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
        if (Main.DEBUG_MODE) {
           return name + " (" + id + ")";
        }
        return name;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj != null) {
//            if (obj instanceof DbObject) {
//                if (((DbObject) obj).getId() == getId() && ((DbObject) obj).getName().equals(getName())) {
//                    return true;
//                }
//                if (getId() < 0 || ((DbObject) obj).getId() < 0) {
//                    return getName().equals(((DbObject) obj).getName());
//                }
//            }
//        }
//        return false;
//    }


    @Override
    public boolean equals(Object o) {
        DbObject dbObject;
        try {
            if (this == o) return true;
            if (!(o instanceof DbObject)) return false;
            dbObject = (DbObject) o;
            if (getId() == dbObject.getId() &&
                    getImageId() == dbObject.getImageId() &&
                    Objects.equals(TABLE_NAME, dbObject.TABLE_NAME) &&
                    Objects.equals(getName(), dbObject.getName())) {
                return true;
            }
            // Not saved?
            if (!dbObject.isSaved() || !isSaved()) {
                return Objects.equals(getName(), dbObject.getName());
            }
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(TABLE_NAME, getId(), getName());
    }

    public List<SearchMatch> searchByKeyWord(String searchTerm) {
        List<SearchMatch> matches = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            searchTerm = searchTerm.toUpperCase();

            SearchMatch m;
            m = SearchMatch.hasMatch(32, getName(), searchTerm);
            if (m != null) matches.add(m);
        }
        return matches;
    }

    public abstract DbObject createCopy();

    public abstract DbObject createCopy(DbObject copyInto);

    public void copyBaseFields(DbObject newObject) {
        newObject.setId(getId());
        newObject.setName(getName());
        newObject.setIconPath(getIconPath());
        newObject.setImageId(getImageId());

        newObject.setInserted(isInserted);
        newObject.setCanBeSaved(false);
    }

    public boolean isSaved() {
        return getId() > 0;
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
        if (id > UNKNOWN_ID) {
            isInserted = true;
        }
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
        if (iconPath == null) {
            iconPath = "";
        }
        return iconPath;
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

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    public AddUpdateDelete getAud() {
        return aud;
    }


    public long getImageId() {
        if (imageId < UNKNOWN_ID) {
            imageId = UNKNOWN_ID;
        }
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}
