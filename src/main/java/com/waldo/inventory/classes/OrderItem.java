package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.database.SearchManager.sm;

public class OrderItem extends DbObject {

    private static final LogManager LOG = LogManager.LOG(OrderItem.class);
    public static final String TABLE_NAME = "orderitems";

    private long orderId;
    private Order order;
    private long itemId;
    private Item item;
    private int amount;
    private long distributorPartId;
    private DistributorPart distributorPart;

    public OrderItem() {
        super(TABLE_NAME);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, orderId);
        statement.setLong(3, itemId);
        statement.setInt(4, amount);
        if (distributorPartId < UNKNOWN_ID) {
            distributorPartId = UNKNOWN_ID;
        }
        statement.setLong(5, distributorPartId);
        return 6;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        return super.hasMatch(searchTerm);
    }

    @Override
    public OrderItem createCopy(DbObject copyInto) {
        OrderItem orderItem = (OrderItem) copyInto;
        copyBaseFields(orderItem);
        orderItem.setOrderId(getOrderId());
        orderItem.setItemId(getItemId());
        orderItem.setAmount(getAmount());
        orderItem.setDistributorPartId(getDistributorPartId());
        return orderItem;
    }

    @Override
    public OrderItem createCopy() {
        return createCopy(new OrderItem());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<OrderItem> list = db().getOrderItems();
                if (!list.contains(this)) {
                    list.add(this);
                }
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onOrderItemsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onOrderItemsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<OrderItem> list = db().getOrderItems();
                if (list.contains(this)) {
                    list.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onOrderItemsChangedListenerList);
                break;
            }
        }
    }

    public static OrderItem createDummyOrderItem(Order order, Item item) {
        OrderItem oi = new OrderItem();
        oi.setItemId(item.getId());
        oi.setOrderId(order.getId());
        oi.setCanBeSaved(false);
        return oi;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setOrderId(long orderId) {
        order = null;
        this.orderId = orderId;
    }

    public void setItemId(long itemId) {
        item = null;
        this.itemId = itemId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setAmount(String amount) {
        try {
            if (!amount.isEmpty()) {
                this.amount = Integer.valueOf(amount);
            }
        } catch (Exception e) {
            LOG.error("Error setting amount.", e);
        }
    }

    public long getDistributorPartId() {
        return distributorPartId;
    }

    public void setDistributorPartId(long distributorPartId) {
        distributorPart = null;
        this.distributorPartId = distributorPartId;
    }

    public void setDistributorPartId(String ref) {
        DistributorPart number = SearchManager.sm().findPartNumber(getOrder().getId(), getItemId());
        if (number == null) {
            number = new DistributorPart(getOrder().getDistributorId(), getItemId());
        }
        number.setItemRef(ref);
        distributorPartId = number.getId();
    }

    public DistributorPart getDistributorPart() {
        if (distributorPart == null) {
            distributorPart = sm().findPartNumberById(distributorPartId);
        }
        return distributorPart;
    }

    public Order getOrder() {
        if (order == null) {
            order = sm().findOrderById(orderId);
        }
        return order;
    }

    public Item getItem() {
        if (item == null) {
            item = sm().findItemById(itemId);
        }
        return item;
    }
}
