package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Package extends DbObject {

    public static final String TABLE_NAME = "packages";

    private String description;

    public Package() {
        super(TABLE_NAME);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setString(ndx++, getDescription());

        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        return result && obj instanceof Package && ((Package) obj).getDescription().equals(getDescription());
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        String search = searchTerm.toUpperCase();
        String name = getName().toUpperCase();
        return name.contains(search) || search.contains(name);
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<Package> list = cache().getPackages();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<Package> list = cache().getPackages();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        cache().notifyListeners(changedHow, this, cache().onPackageChangedListenerList);
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
