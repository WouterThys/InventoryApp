package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PartNumber extends DbObject {

    public static final String TABLE_NAME = "partnumbers";

    private long distributorId;
    private long itemId;
    private String itemRef;

    public PartNumber() {
        super(TABLE_NAME);
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, distributorId);
        statement.setLong(4, itemId);
        statement.setString(5, itemRef);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, distributorId);
        statement.setLong(4, itemId);
        statement.setString(5, itemRef);
        statement.setLong(6, id); // WHERE id
        statement.execute();
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {


        }
        return false;
    }


    public long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(long distributorId) {
        this.distributorId = distributorId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getItemRef() {
        return itemRef;
    }

    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
    }
}