package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

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

    private long manufacturerId;
    private Manufacturer manufacturer;

    private Date orderDate;

    public PendingOrder() {
        super(TABLE_NAME);
    }

    public PendingOrder(Item item, Manufacturer manufacturer) {
        this();

        this.item = item;
        this.manufacturer = manufacturer;

        if (item != null) {
            itemId = item.getId();
        }
        if (manufacturer != null) {
            manufacturerId = manufacturer.getId();
        }
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        if (getItemId() < UNKNOWN_ID) {
            setItemId(UNKNOWN_ID);
        }
        if (getManufacturerId() < UNKNOWN_ID) {
            setManufacturerId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getItemId());
        statement.setLong(ndx++, getManufacturerId());
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
        cpy.setManufacturerId(getManufacturerId());
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

    public long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(long manufacturerId) {
        if (manufacturer != null && manufacturer.getId() != manufacturerId) {
            manufacturer = null;
        }
        this.manufacturerId = manufacturerId;
    }

    public Manufacturer getManufacturer() {
        if (manufacturer == null) {
            manufacturer = SearchManager.sm().findManufacturerById(manufacturerId);
        }
        return manufacturer;
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
