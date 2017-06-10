package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Distributor extends DbObject {

    public static final String TABLE_NAME = "distributors";

    private String website;

    public Distributor() {
        super(TABLE_NAME);
    }

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

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            if (searchTerm.contains(getWebsite().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Distributor createCopy(DbObject original) {
        Distributor distributor = new Distributor();
        copyBaseFields(distributor);
        distributor.setWebsite(((Distributor)original).getWebsite());
        return distributor;
    }

    public String getWebsite() {
        if (website == null) {
            website = "";
        }
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
