package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Location extends DbObject {

    public static final String TABLE_NAME = "locations";

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, id); // WHERE id
        statement.execute();
    }

    public Location() {
        super(TABLE_NAME);
    }

    public Location(String tableName, String sqlInsert, String sqlUpdate) {
        super(tableName, sqlInsert, sqlUpdate);
    }

    @Override
    public Location createCopy(DbObject original) {
        Location location = new Location();
        copyBaseFields(location);
        return location;
    }

    public static Location getUnknownLocation() {
        Location l = new Location();
        l.setName(UNKNOWN_NAME);
        l.setId(UNKNOWN_ID);
        l.setCanBeSaved(false);
        return l;
    }

}
