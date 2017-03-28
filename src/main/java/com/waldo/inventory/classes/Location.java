package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Location extends DbObject {

    public static final String TABLE_NAME = "locations";
    private static final String insertSql = "INSERT INTO "+TABLE_NAME+" (" +
            "name, iconpath) VALUES " +
            "(?, ?)";
    private static final String updateSql =
            "UPDATE "+TABLE_NAME+" " +
                    "SET name = ?, iconpath = ? " +
                    "WHERE id = ?;";

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
        super(TABLE_NAME, insertSql, updateSql);
    }

    public Location(String tableName, String sqlInsert, String sqlUpdate) {
        super(tableName, sqlInsert, sqlUpdate);
    }

    public static Location getUnknownLocation() {
        Location l = new Location();
        l.setName(UNKNOWN_NAME);
        l.setId(UNKNOWN_ID);
        return l;
    }

}