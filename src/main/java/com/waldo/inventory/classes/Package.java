package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class Package extends DbObject {

    public static final String TABLE_NAME = "packages";

    private String description;

    public Package() {
        super(TABLE_NAME);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        return result && obj instanceof Package && ((Package) obj).getDescription().equals(getDescription());
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setString(ndx++, getDescription());

        return ndx;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Package> list = db().getPackages();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Package> list = db().getPackages();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onPackageChangedListenerList);
    }

    @Override
    public Package createCopy() {
        return createCopy(new Package());
    }

    @Override
    public Package createCopy(DbObject copyInto) {
        Package pack = (Package) copyInto;
        copyBaseFields(pack);

        pack.setDescription(getDescription());

        return pack;
    }

    public String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
