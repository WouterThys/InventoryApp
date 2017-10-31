package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class DistributorPartLink extends DbObject {

    public static final String TABLE_NAME = "distributorParts";

    private long distributorId;
    private Distributor distributor;

    private long itemId;
    private Item item;

    private String itemRef;

    public DistributorPartLink() {
        super(TABLE_NAME);
    }

    public DistributorPartLink(long distributorId, long itemId) {
        super(TABLE_NAME);
        this.distributorId = distributorId;
        this.itemId = itemId;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, distributorId);
        statement.setLong(ndx++, itemId);
        statement.setString(ndx++, itemRef);
        return ndx;
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
    public DistributorPartLink createCopy(DbObject copyInto) {
        DistributorPartLink distributorPartLink = (DistributorPartLink) copyInto;
        copyBaseFields(distributorPartLink);
        distributorPartLink.setDistributorId(getDistributorId());
        distributorPartLink.setItemId(getItemId());
        distributorPartLink.setItemRef(getItemRef());
        return distributorPartLink;
    }

    @Override
    public DistributorPartLink createCopy() {
        return createCopy(new DistributorPartLink());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<DistributorPartLink> list = db().getDistributorPartLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<DistributorPartLink> list = db().getDistributorPartLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        // No listeners..
    }


    public long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(long distributorId) {
        distributor = null;
        this.distributorId = distributorId;
    }

    public Distributor getDistributor() {
        if (distributor == null) {
            distributor = SearchManager.sm().findDistributorById(distributorId);
        }
        return distributor;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        item = null;
        this.itemId = itemId;
    }

    public Item getItem() {
        if (item == null) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }

    public String getItemRef() {
        if (itemRef == null) {
            itemRef = "";
        }
        return itemRef;
    }

    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
    }
}
