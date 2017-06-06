package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PackageType extends DbObject {

    public static final String TABLE_NAME = "packagetypes";

    public PackageType() {
        super(TABLE_NAME);
    }

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, id); // WHERE id
        statement.execute();
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        return super.hasMatch(searchTerm);
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
}
