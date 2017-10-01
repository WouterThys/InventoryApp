package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.LogManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.managers.SearchManager.sm;

public class Package extends DbObject {

    private static final LogManager LOG = LogManager.LOG(Package.class);
    public static final String TABLE_NAME = "packages";

    private long packageTypeId;
    private PackageType packageType;
    private int pins;
    private double width;
    private double height;

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
                if (!(ref.getPins() == getPins())) return false;
                if (!(ref.getWidth() == getWidth())) return false;
                if (!(ref.getHeight() == getHeight())) return false;
            }
        }
        return result;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, getPackageTypeId());
        statement.setInt(ndx++, getPins());
        statement.setDouble(ndx++, getWidth());
        statement.setDouble(ndx++, getHeight());
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
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onPackageChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onPackageChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Package> list = db().getPackages();
                if (list.contains(this)) {
                    list.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onPackageChangedListenerList);
                break;
            }
        }
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
        pack.setPins(getPins());
        pack.setHeight(getHeight());
        pack.setWidth(getWidth());
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

    public int getPins() {
        return pins;
    }

    public void setPins(int pins) {
        this.pins = pins;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
