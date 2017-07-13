package com.waldo.inventory.classes;

import com.waldo.inventory.database.LogManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    private PartNumber distributorPart;

    public OrderItem() {
        super(TABLE_NAME);
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, orderId);
        statement.setLong(3, itemId);
        statement.setInt(4, amount);
        statement.setLong(5, distributorPartId);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setLong(2, orderId);
        statement.setLong(3, itemId);
        statement.setInt(4, amount);
        statement.setLong(5, distributorPartId);
        statement.setLong(6, id); // WHERE id
        statement.execute();
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
        this.orderId = orderId;
    }

    public void setItemId(long itemId) {
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

    public PartNumber getDistributorPart() {
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
