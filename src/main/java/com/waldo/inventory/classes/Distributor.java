package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class Distributor extends DbObject {

    public static final String TABLE_NAME = "distributors";

    private String website;

    public Distributor() {
        super(TABLE_NAME);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setString(ndx++, getWebsite());
        return ndx;
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
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof Distributor)) {
                return false;
            }
            if (!(((Distributor)obj).getWebsite().equals(getWebsite()))) {
                return false;
            }
        }
        return result;
    }

    @Override
    public Distributor createCopy(DbObject copyInto) {
        Distributor distributor = (Distributor) copyInto;
        copyBaseFields(distributor);
        distributor.setWebsite(getWebsite());
        return distributor;
    }

    @Override
    public Distributor createCopy() {
        return createCopy(new Distributor());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Distributor> distributors = db().getDistributors();
                if (!distributors.contains(this)) {
                    distributors.add(this);
                }
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onDistributorsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onDistributorsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Distributor> distributors = db().getDistributors();
                if (distributors.contains(this)) {
                    distributors.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onDistributorsChangedListenerList);
                break;
            }
        }
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
