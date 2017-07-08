package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Manufacturer extends DbObject {

    public static final String TABLE_NAME = "manufacturers";

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
        super(TABLE_NAME);
    }

    public static Manufacturer getUnknownManufacturer() {
        Manufacturer m = new Manufacturer();
        m.setName(UNKNOWN_NAME);
        m.setId(UNKNOWN_ID);
        m.setCanBeSaved(false);
        return m;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof Manufacturer)) {
                return false;
            }
            if (!(((Manufacturer)obj).getWebsite().equals(getWebsite()))) {
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
            return (getWebsite().toUpperCase().contains(searchTerm)) ;
        }
    }

    @Override
    public Manufacturer createCopy(DbObject copyInto) {
        Manufacturer manufacturer = (Manufacturer) copyInto;
        copyBaseFields(manufacturer);
        manufacturer.setWebsite(getWebsite());
        return manufacturer;
    }

    @Override
    public Manufacturer createCopy() {
        return createCopy(new Manufacturer());
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
