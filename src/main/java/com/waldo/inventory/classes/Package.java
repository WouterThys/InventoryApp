package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Package extends DbObject {

    public static final String TABLE_NAME = "package";

    private long typeId;
    private PackageType packageType;
    private int pins;
    private int height;
    private int width;

    public Package() {
        super(TABLE_NAME);
    }

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        if (typeId <= UNKNOWN_ID) {
            typeId = UNKNOWN_ID;
        }
        statement.setLong(2, typeId);
        statement.setInt(3, pins);
        statement.setInt(4, width);
        statement.setInt(5, height);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, typeId);
        statement.setInt(3, pins);
        statement.setInt(4, width);
        statement.setInt(5, height);
        statement.setLong(6, id); // WHERE id
        statement.execute();
    }

    public static Package getUnknownPackage() {
        Package p = new Package();
        p.setName(UNKNOWN_NAME);
        p.setId(UNKNOWN_ID);
        p.setCanBeSaved(false);
        return p;
    }

    public static Package createDummyPackage() {
        Package p = new Package();
        p.setName("");
        p.setCanBeSaved(false);
        return p;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public PackageType getPackageType() {
        if (packageType == null) {
            packageType = DbManager.db().findPackageTypeByIndex(typeId);
        }
        return packageType;
    }

    public void setPackageType(PackageType packageType) {
        if (packageType != null && (packageType.getId() != UNKNOWN_ID)) {
            this.typeId = packageType.getId();
        } else {
            this.typeId = UNKNOWN_ID;
        }
        this.packageType = packageType;
    }

    public int getPins() {
        return pins;
    }

    public void setPins(int pins) {
        this.pins = pins;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
