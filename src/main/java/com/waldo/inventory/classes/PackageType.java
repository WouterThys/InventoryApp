package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class PackageType extends DbObject {

    public static final String TABLE_NAME = "packagetypes";

    private long packageId;
    private Package aPackage;

    private int defaultPins;
    private boolean allowOtherPinNumbers;

    private String description;

    public PackageType() {
        super(TABLE_NAME);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setLong(ndx++, getPackageId());
        statement.setInt(ndx++, getDefaultPins());
        statement.setBoolean(ndx++, isAllowOtherPinNumbers());
        statement.setString(ndx++, getDescription());

        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof PackageType)) {
                return false;
            }
            PackageType ref = (PackageType) obj;

            if (!(ref.getPackageId() == getPackageId())) return false;
            if (!(ref.getDefaultPins() == getDefaultPins())) return false;
            if (!(ref.isAllowOtherPinNumbers() == isAllowOtherPinNumbers())) return false;
            if (!(ref.getDescription().equals(getDescription()))) return false;
        }
        return result;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        return super.hasMatch(searchTerm);
    }


    @Override
    public PackageType createCopy(DbObject copyInto) {
        PackageType cpy = (PackageType) copyInto;
        copyBaseFields(cpy);

        cpy.setPackageId(getPackageId());
        cpy.setDefaultPins(getDefaultPins());
        cpy.setAllowOtherPinNumbers(isAllowOtherPinNumbers());
        cpy.setDescription(getDescription());

        return cpy;
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
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<PackageType> list = db().getPackageTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onPackageTypesChangedListenerList);
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

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        aPackage = null;
        this.packageId = packageId;
    }

    public Package getPackage() {
        if (aPackage == null) {
            aPackage = SearchManager.sm().findPackageById(packageId);
        }
        return aPackage;
    }

    public int getDefaultPins() {
        return defaultPins;
    }

    public void setDefaultPins(int defaultPins) {
        this.defaultPins = defaultPins;
    }

    public boolean isAllowOtherPinNumbers() {
        return allowOtherPinNumbers;
    }

    public void setAllowOtherPinNumbers(boolean allowOtherPinNumbers) {
        this.allowOtherPinNumbers = allowOtherPinNumbers;
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
