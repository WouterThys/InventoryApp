package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class LocationType extends DbObject {

    public static final String TABLE_NAME = "locationtypes";

    private List<Location> locations;

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
        }
        return result;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        return addBaseParameters(statement);
    }

    @Override
    public LocationType createCopy(DbObject copyInto) {
        LocationType cpy = (LocationType) copyInto;
        copyBaseFields(cpy);

        return cpy;
    }

    @Override
    public LocationType createCopy() {
        return createCopy(new LocationType());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<LocationType> list = cache().getLocationTypes();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<LocationType> list = cache().getLocationTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        cache().notifyListeners(changedHow, this, cache().onLocationTYpeChangedListenerList);
    }

    public static LocationType getUnknownLocationType() {
        LocationType u = new LocationType();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    public List<Location> getLocations() {
        if (locations == null) {
            locations = SearchManager.sm().findLocationsByTypeId(getId());
        }
        return locations;
    }

    public void updateLocations() {
        locations = null;
    }
}