package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Manufacturer extends DbObject {

    public static final String TABLE_NAME = "manufacturers";
    private static final String insertSql = "INSERT INTO "+TABLE_NAME+" (" +
            "name, iconpath, website) VALUES " +
            "(?, ?, ?)";
    private static final String updateSql =
            "UPDATE "+TABLE_NAME+" " +
                    "SET name = ?, iconpath = ?, website = ?  " +
                    "WHERE id = ?;";

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, website);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, website);
        statement.setLong(4, id); // WHERE id
        statement.execute();
    }

    private String website = "";

    public Manufacturer() {
        super(TABLE_NAME, insertSql, updateSql);
    }

    public Manufacturer(String tableName, String sqlInsert, String sqlUpdate) {
        super(tableName, sqlInsert, sqlUpdate);
    }

    public static Manufacturer getUnknownManufacturer() {
        Manufacturer m = new Manufacturer();
        m.setName(UNKNOWN_NAME);
        m.setId(UNKNOWN_ID);
        return m;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            return (getWebsite().toUpperCase().contains(searchTerm)) ;
        }
    }

    public String getWebsite() {
        if (website == null) {
            return "";
        }
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
