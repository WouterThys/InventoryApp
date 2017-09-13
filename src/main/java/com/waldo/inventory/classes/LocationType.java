package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class LocationType extends DbObject {

    public static final String TABLE_NAME = "locationtypes";

    // Variables
    private int rows;
    private int columns;
    private boolean custom; // Not a straight forward location group

    public LocationType() {
        super(TABLE_NAME);
    }


    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof LocationType)) {
                return false;
            }
            if (!(((LocationType)obj).getColumns() == getColumns())) return false;
            if (!(((LocationType)obj).getRows() == getRows())) return false;
            if (!(((LocationType)obj).isCustom() == isCustom())) return false;
        }
        return result;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setInt(ndx++, getRows());
        statement.setInt(ndx++, getColumns());
        statement.setBoolean(ndx++, isCustom());

        return ndx;
    }

    @Override
    public LocationType createCopy(DbObject copyInto) {
        LocationType cpy = (LocationType) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setRows(getRows());
        cpy.setColumns(getColumns());
        cpy.setCustom(isCustom());

        return cpy;
    }

    @Override
    public LocationType createCopy() {
        return createCopy(new LocationType());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<LocationType> list = db().getLocationTypes();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<LocationType> list = db().getLocationTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onLocationTYpeChangedListenerList);
    }

    public static LocationType getUnknownLocationType() {
        LocationType u = new LocationType();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    public List<Location> getLocations() {
        return new ArrayList<>(SearchManager.sm().findLocationsByTypeId(getId()));
    }

    // Getters and setters

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

}