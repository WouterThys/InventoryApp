package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Manufacturer extends DbObject {

    public static final String TABLE_NAME = "manufacturers";

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, getImageId());
        statement.setString(ndx++, getWebsite());
        return ndx;
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

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
