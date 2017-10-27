package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.managers.SearchManager.sm;

public class Package extends DbObject {

    public static final String TABLE_NAME = "packages";

    private long packageTypeId;
    private PackageType packageType;

    public Package() {
        super(TABLE_NAME);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof Package)) {
                return false;
            } else {
                Package ref = (Package) obj;
                if (!(ref.getPackageTypeId() == getPackageTypeId())) { return false; }
            }
        }
        return result;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, getPackageTypeId());
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
        pack.setPackageTypeId(getPackageTypeId());
        return pack;
    }

    public long getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(long packageTypeId) {
        packageType = null;
        this.packageTypeId = packageTypeId;
    }

    public PackageType getPackageType() {
        if (packageType == null) {
            packageType = sm().findPackageTypeById(packageTypeId);
        }
        return packageType;
    }
}
