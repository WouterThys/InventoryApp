package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
