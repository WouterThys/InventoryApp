package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class PackageType extends DbObject {

    public static final String TABLE_NAME = "packagetypes";

    private String description;

    public PackageType() {
        super(TABLE_NAME);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, description);
        return 3;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof PackageType)) {
                return false;
            }
            if (!(((PackageType)obj).getDescription().equals(getDescription()))) {
                return false;
            }
        }
        return result;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        return super.hasMatch(searchTerm);
    }


    @Override
    public PackageType createCopy(DbObject copyInto) {
        PackageType packageType = (PackageType) copyInto;
        copyBaseFields(packageType);
        packageType.setDescription(getDescription());
        return packageType;
    }

    @Override
    public PackageType createCopy() {
        return createCopy(new PackageType());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<PackageType> list = db().getPackageTypes();
                if (!list.contains(this)) {
                    list.add(this);
                }
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onPackageTypesChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onPackageTypesChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<PackageType> list = db().getPackageTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onPackageTypesChangedListenerList);
                break;
            }
        }
    }

    public static PackageType createDummyPackageType() {
        PackageType p = new PackageType();
        p.setName("");
        p.setCanBeSaved(false);
        return p;
    }

    public static PackageType getUnknownPackageType() {
        PackageType p = new PackageType();
        p.setName(UNKNOWN_NAME);
        p.setId(UNKNOWN_ID);
        p.setCanBeSaved(false);
        return p;
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
