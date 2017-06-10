package com.waldo.inventory.classes;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class PackageType extends DbObject {

    public static final String TABLE_NAME = "packagetypes";

    private String description;

    public PackageType() {
        super(TABLE_NAME);
    }

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, description);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, description);
        statement.setLong(3, id); // WHERE id
        statement.execute();
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
    public PackageType createCopy() {
        PackageType packageType = new PackageType();
        copyBaseFields(packageType);
        packageType.setDescription(getDescription());
        return packageType;
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
