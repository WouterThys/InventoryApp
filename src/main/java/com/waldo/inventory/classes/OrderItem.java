package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;

import static com.waldo.inventory.database.SearchManager.*;

public class OrderItem extends DbObject {

    public static final String TABLE_NAME = "orderitems";

    private long orderId;
    private Order order;
    private long itemId;
    private Item item;
    private int amount;
    private String itemRef;

    public OrderItem() {
        super(TABLE_NAME);
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, orderId);
        statement.setLong(3, itemId);
        statement.setInt(4, amount);
        statement.setString(5, itemRef);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setLong(2, orderId);
        statement.setLong(3, itemId);
        statement.setInt(4, amount);
        statement.setString(5, itemRef);
        statement.setLong(6, id); // WHERE id
        statement.execute();
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        return super.hasMatch(searchTerm);
    }

    @Override
    public OrderItem createCopy() {
        OrderItem orderItem = new OrderItem();
        copyBaseFields(orderItem);
        orderItem.setOrderId(getOrderId());
        orderItem.setItemId(getItemId());
        orderItem.setAmount(getAmount());
        orderItem.setItemRef(getItemRef());
        return orderItem;
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

    public String getItemRef() {
        if (itemRef == null) {
            itemRef = "";
        }
        return itemRef;
    }

    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
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
