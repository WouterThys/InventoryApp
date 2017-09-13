package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class Location extends DbObject {

    public static final String TABLE_NAME = "locations";

    // private String name; -> in DbObject
    private long locationTypeId;
    private LocationType locationType;
    private int row = 0;
    private int col = 0;
    private String alias;

    public Location() {
        super(TABLE_NAME);
    }

    @Override
    public String toString() {
        if (getLocationType() != null) {
            if (getLocationType().isCustom()) {
                return getName();
            } else {
                return getLocationType().getName() + "(" + Statics.Alphabet[row] + "," + col + ")";
            }
        } else {
            return "*" + "("+ Statics.Alphabet[row]+","+col+")";
        }
    }

    public String getPrettyString() {
        if (getLocationType() != null && getLocationTypeId() > UNKNOWN_ID) {
            String locType = getLocationType().getName();
            if (locType.length() > 3) {
                locType = locType.substring(0,3);
            }
            return locType + "/"+ Statics.Alphabet[row]+"/"+col;
        }
        return "";
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setString(ndx++, getName());
        statement.setLong(ndx++, getLocationTypeId());
        statement.setInt(ndx++, getRow());
        statement.setInt(ndx++, getCol());
        statement.setString(ndx++, getAlias());

        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof Location)) {
                return false;
            }
            if (!(((Location)obj).getLocationTypeId() == getLocationTypeId())) {
                System.out.println("location type differs");
                return false;
            }
            if (!(((Location)obj).getRow() == getRow())) {
                System.out.println("row differs");
                return false;
            }
            if (!(((Location)obj).getCol() == getCol())) {
                System.out.println("col differs");
                return false;
            }
            if (!(((Location)obj).getAlias().equals(getAlias()))) {
                System.out.println("alias differs");
                return false;
            }
        }
        return result;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            if (getLocationType() == null) {
                return false;
            }
            // Local objects
            if (getLocationType().getName().toUpperCase().contains(searchTerm)) {
                return true;
            } else if (Statics.Alphabet[row].toUpperCase().contains(searchTerm)) {
                return true;
            } else if (String.valueOf(col).toUpperCase().contains(searchTerm)) {
                return true;
            } else if (getPrettyString().toUpperCase().contains(searchTerm)) {
                return true;
            } else if (getAlias().toUpperCase().contains(searchTerm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Location createCopy(DbObject copyInto) {
        Location cpy = (Location) copyInto;
        copyBaseFields(cpy);

        cpy.setLocationTypeId(getLocationTypeId());
        cpy.setRow(getRow());
        cpy.setCol(getCol());
        cpy.setAlias(getAlias());

        return cpy;
    }

    @Override
    public Location createCopy() {
        return createCopy(new Location());
    }

    public static Location getUnknownLocation() {
        Location l = new Location();
        l.setName(UNKNOWN_NAME);
        l.setId(UNKNOWN_ID);
        l.setCanBeSaved(false);
        return l;
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Location> list = db().getLocations();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {

                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Location> list = db().getLocations();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onLocationsChangedListenerList);
    }

    public long getLocationTypeId() {
        return locationTypeId;
    }

    public void setLocationTypeId(long locationTypeId) {
        locationType = null;
        this.locationTypeId = locationTypeId;
    }

    public LocationType getLocationType() {
        if (locationType == null) {
            locationType = SearchManager.sm().findLocationTypeById(locationTypeId);
        }
        return locationType;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setRowAsString(String row) {
        this.row = Statics.indexOfAlphabet(row);
    }

    public int getCol() {
        return col;
    }

    public void setCol(int column) {
        this.col = column;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
