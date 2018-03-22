package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.DateUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

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
        orderDate = DateUtils.now();
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
    }

    @Override
    public String toString() {
        return "PendingOrder{" +
                "item=" + item +
                ", distributor=" + distributor +
                ", orderDate=" + orderDate +
                ", id=" + id +
                '}';
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
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);

        if (result) {
            if (obj instanceof PendingOrder) {
                PendingOrder po = (PendingOrder) obj;

                if (po.getItemId() != getItemId()) return false;
                if (po.getDistributorId() != getDistributorId()) return false;

                return true;
            }
        }

        return result;
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
