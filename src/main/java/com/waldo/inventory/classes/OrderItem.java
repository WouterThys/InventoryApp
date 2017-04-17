package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItem extends DbObject {

    public static final String TABLE_NAME = "orderitems";

    private long orderId;
    private long itemId;

    public OrderItem() {
        super(TABLE_NAME);
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, orderId);
        statement.setLong(3, itemId);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setLong(2, orderId);
        statement.setLong(3, itemId);
        statement.setLong(4, id); // WHERE id
        statement.execute();
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
           

        }
        return false;
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
}
