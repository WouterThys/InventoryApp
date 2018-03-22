package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

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

    public PackageType(long packageId) {
        super(TABLE_NAME);
        setPackageId(packageId);
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

    public String getPrettyString() {
        String result = toString();
        if (packageId > DbObject.UNKNOWN_ID) {
            result += " - " + getPackage().toString();
        }
        return result;
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        if (aPackage != null && aPackage.getId() != packageId) {
            aPackage = null;
        }
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

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }
}
