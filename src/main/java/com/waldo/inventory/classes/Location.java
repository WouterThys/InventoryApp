package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class Location extends DbObject {

    public static final String TABLE_NAME = "locations";

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        return addBaseParameters(statement);
    }

    public Location() {
        super(TABLE_NAME);
    }

    @Override
    public Location createCopy(DbObject copyInto) {
        Location location = (Location) copyInto;
        copyBaseFields(location);
        return location;
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
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onLocationsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onLocationsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Location> list = db().getLocations();
                if (list.contains(this)) {
                    list.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onLocationsChangedListenerList);
                break;
            }
        }
    }

}
