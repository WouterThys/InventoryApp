package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Set extends DbObject {

    public static final String TABLE_NAME = "sets";

    // Variables
    private long manufacturerId;
    private Manufacturer manufacturer;

    private long locationId;
    private Location location;

    private List<Item> setItems;


    public Set() {
        super(TABLE_NAME);
    }

    public Set(String name) {
        super(TABLE_NAME);
        setName(name);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        // Add parameters
        statement.setLong(ndx++, getManufacturerId());
        statement.setLong(ndx++, getLocationId());

        return ndx;
    }

    @Override
    public Set createCopy(DbObject copyInto) {
        Set cpy = (Set) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setManufacturerId(getManufacturerId());
        cpy.setLocationId(getLocationId());

        return cpy;
    }

    @Override
    public Set createCopy() {
        return createCopy(new Set());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<Set> list = cache().getSets();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<Set> list = cache().getSets();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public static Set getUnknownSet() {
        Set u = new Set();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    // Getters and setters

    public long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(long manufacturerId) {
        if (manufacturer != null && manufacturer.getId() != manufacturerId) {
            manufacturer = null;
        }
        this.manufacturerId = manufacturerId;
    }

    public Manufacturer getManufacturer() {
        if (manufacturer == null) {
            manufacturer = SearchManager.sm().findManufacturerById(manufacturerId);
        }
        return manufacturer;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        if (location != null && location.getId() != locationId) {
            location = null;
        }
        this.locationId = locationId;
    }

    public Location getLocation() {
        if (location == null) {
            location = SearchManager.sm().findLocationById(locationId);
        }
        return location;
    }

    public List<Item> getSetItems() {
        if (setItems == null) {
            setItems = SearchManager.sm().findSetItemsBySetId(getId());
        }
        return setItems;
    }
}