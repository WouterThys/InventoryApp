package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class DimensionType extends DbObject {

    public static final String TABLE_NAME = "dimensiontypes";

    // Variables
    private double width;
    private double height;
    private long packageTypeId;
    private PackageType packageType;

    public DimensionType() {
        super(TABLE_NAME);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        // Add parameters
        statement.setDouble(ndx++, getWidth());
        statement.setDouble(ndx++, getHeight());
        statement.setLong(ndx++, getPackageTypeId());

        return ndx;
    }

    @Override
    public DimensionType createCopy(DbObject copyInto) {
        DimensionType cpy = (DimensionType) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setWidth(getWidth());
        cpy.setHeight(getHeight());
        cpy.setPackageTypeId(getPackageTypeId());

        return cpy;
    }

    @Override
    public DimensionType createCopy() {
        return createCopy(new DimensionType());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<DimensionType> list = db().getDimensionTypes();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {

                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<DimensionType> list = db().getDimensionTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onDimensionTypeChangedListenerList);
    }

    public static DimensionType getUnknownDimensionType() {
        DimensionType u = new DimensionType();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    public static DimensionType createDummyDimensionType() {
        DimensionType d = new DimensionType();
        d.setName("");
        d.setCanBeSaved(false);
        return d;
    }

    // Getters and setters

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

    public long getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(long packageTypeId) {
        packageType = null;
        this.packageTypeId = packageTypeId;
    }

    public PackageType getPackageType() {
        if (packageType == null) {
            packageType = SearchManager.sm().findPackageTypeById(packageTypeId);
        }
        return packageType;
    }
}