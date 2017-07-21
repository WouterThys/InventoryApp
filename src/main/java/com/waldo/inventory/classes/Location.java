package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

}
