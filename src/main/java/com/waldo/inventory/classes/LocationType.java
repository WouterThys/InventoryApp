package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class LocationType extends DbObject {

    public static final String TABLE_NAME = "locationtypes";

    // Variables
    private int rows;
    private int columns;

    public LocationType() {
        super(TABLE_NAME);
    }


    public static LocationType createDummyLocationType() {
        LocationType locationType = new LocationType();
        locationType.canBeSaved = false;
        return locationType;
    }

    public List<String> createRowStrings() {
        List<String> rowStrings = new ArrayList<>();
        rowStrings.addAll(Arrays.asList(Statics.Alphabet).subList(0, rows));
        return rowStrings;
    }

    public List<String> createColumnStrings() {
        List<String> columnStrings = new ArrayList<>();
        for (int i = 0; i < columns; i++) {
            columnStrings.add(String.valueOf(i));
        }
        return columnStrings;
    }

    public List<String> createLocationStrings() {
        List<String> locationStrings = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                locationStrings.add(Statics.Alphabet[r] + String.valueOf(c));
            }
        }
        return locationStrings;
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
        }
        return result;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setInt(ndx++, getRows());
        statement.setInt(ndx++, getColumns());

        return ndx;
    }

    @Override
    public LocationType createCopy(DbObject copyInto) {
        LocationType cpy = (LocationType) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setRows(getRows());
        cpy.setColumns(getColumns());

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

    public int getTotalDrawers() {
        return getRows() * getColumns();
    }

    public boolean hasDrawers() {
        return (getRows() > 0 && getColumns() > 0);
    }
}