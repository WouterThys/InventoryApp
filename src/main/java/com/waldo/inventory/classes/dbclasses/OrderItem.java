package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.LogManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class OrderItem extends DbObject {

    private static final LogManager LOG = LogManager.LOG(OrderItem.class);
    public static final String TABLE_NAME = "orderitems";

    private long orderId;
    private Order order;
    private long itemId;
    private Item item;
    private int amount;
    private DistributorPartLink distributorPartLink;

    public OrderItem() {
        super(TABLE_NAME);
    }

    public OrderItem(long orderId, long itemId, int amount) {
        super(TABLE_NAME);

        setOrderId(orderId);
        setItemId(itemId);
        setAmount(amount);

        setName(getOrder().toString() + " - " + getItem().toString());
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;
        String name = "";
        try {
            name = getOrder().getName() + " - " + getItem().getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        statement.setString(ndx++, name);
        statement.setLong(ndx++, orderId);
        statement.setLong(ndx++, itemId);
        statement.setInt(ndx++, amount);
        return ndx;
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
        return orderItem;
    }

    @Override
    public OrderItem createCopy() {
        return createCopy(new OrderItem());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<OrderItem> list = cache().getOrderItems();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<OrderItem> list = cache().getOrderItems();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public long getOrderId() {
        return orderId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setOrderId(long orderId) {
        if (order != null && order.getId() != orderId) {
            order = null;
        }
        this.orderId = orderId;
    }

    public void setItemId(long itemId) {
        if (item != null && item.getId() != itemId) {
            item = null;
        }
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
        if (distributorPartLink == null) {
            distributorPartLink = getDistributorPartLink();
        }
        if (distributorPartLink != null) {
            return distributorPartLink.getId();
        }
        return UNKNOWN_ID;
    }

    public DistributorPartLink getDistributorPartLink() {
        if (distributorPartLink == null && getOrder() != null) {
            distributorPartLink = sm().findDistributorPartLink(getOrder().getDistributorId(), getItemId());
        }
        return distributorPartLink;
    }

    public void updateDistributorPart() {
        distributorPartLink = null;
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
