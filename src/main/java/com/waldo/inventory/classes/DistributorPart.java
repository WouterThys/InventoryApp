package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class DistributorPart extends DbObject {

    public static final String TABLE_NAME = "distributorParts";

    private long distributorId;
    private long itemId;
    private String itemRef;

    public DistributorPart() {
        super(TABLE_NAME);
    }

    public DistributorPart(long distributorId, long itemId) {
        super(TABLE_NAME);
        this.distributorId = distributorId;
        this.itemId = itemId;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, distributorId);
        statement.setLong(4, itemId);
        statement.setString(5, itemRef);
        return 6;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {


        }
        return false;
    }

    @Override
    public DistributorPart createCopy(DbObject copyInto) {
        DistributorPart distributorPart = (DistributorPart) copyInto;
        copyBaseFields(distributorPart);
        distributorPart.setDistributorId(getDistributorId());
        distributorPart.setItemId(getItemId());
        distributorPart.setItemRef(getItemRef());
        return distributorPart;
    }

    @Override
    public DistributorPart createCopy() {
        return createCopy(new DistributorPart());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<DistributorPart> list = db().getDistributorParts();
                if (!list.contains(this)) {
                    list.add(this);
                }
                // TODO db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onDistr);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                // TODO db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onDistributorsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<DistributorPart> list = db().getDistributorParts();
                if (list.contains(this)) {
                    list.remove(this);
                }
                // TODO db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onDistributorsChangedListenerList);
                break;
            }
        }
    }


    public long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(long distributorId) {
        this.distributorId = distributorId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getItemRef() {
        return itemRef;
    }

    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
    }
}
