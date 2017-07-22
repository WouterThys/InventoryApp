package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class Manufacturer extends DbObject {

    public static final String TABLE_NAME = "manufacturers";

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
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

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Manufacturer> list = db().getManufacturers();
                if (!list.contains(this)) {
                    list.add(this);
                }
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onManufacturerChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onManufacturerChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Manufacturer> list = db().getManufacturers();
                if (list.contains(this)) {
                    list.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onManufacturerChangedListenerList);
                break;
            }
        }
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
