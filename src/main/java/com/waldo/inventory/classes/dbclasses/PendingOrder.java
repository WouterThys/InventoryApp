package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.DateUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PendingOrder extends DbObject {

    public static final String TABLE_NAME = "pendingorders";

    private long itemId;
    private Item item;

    private long distributorId;
    private Distributor distributor;

    private Date orderDate;

    public PendingOrder() {
        super(TABLE_NAME);
    }

    public PendingOrder(Item item, Distributor distributor) {
        this();

        this.item = item;
        this.distributor = distributor;

        if (item != null) {
            itemId = item.getId();
        }
        if (distributor != null) {
            distributorId = distributor.getId();
        }

        orderDate = DateUtils.now();
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        if (getItemId() < UNKNOWN_ID) {
            setItemId(UNKNOWN_ID);
        }
        if (getDistributorId() < UNKNOWN_ID) {
            setDistributorId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getItemId());
        statement.setLong(ndx++, getDistributorId());
        if (orderDate != null) {
            statement.setTimestamp(ndx++, new Timestamp(orderDate.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }

        return ndx;
    }

    @Override
    public PendingOrder createCopy(DbObject copyInto) {
        PendingOrder cpy = new PendingOrder();
        copyBaseFields(cpy);

        cpy.setItemId(getItemId());
        cpy.setDistributorId(getDistributorId());
        cpy.setOrderDate(getOrderDate());

        return cpy;
    }

    @Override
    public PendingOrder createCopy() {
        return createCopy(new PendingOrder());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<PendingOrder> list = cache().getPendingOrders();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<PendingOrder> list = cache().getPendingOrders();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        if (item != null && item.getId() != itemId) {
            item = null;
        }
        this.itemId = itemId;
    }

    public Item getItem() {
        if (item == null) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }

    public long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(long distributorId) {
        if (distributor != null && distributor.getId() != distributorId) {
            distributor = null;
        }
        this.distributorId = distributorId;
    }

    public Distributor getDistributor() {
        if (distributor == null) {
            distributor = SearchManager.sm().findDistributorById(distributorId);
        }
        return distributor;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        if (orderDate != null) {
            this.orderDate = new Date(orderDate.getTime());
        }
    }
}
