package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItem extends DbObject {

    public static final String TABLE_NAME = "orderitems";

    private Order order;
    private Item item;

    public OrderItem(Order order, Item item) {
        super(TABLE_NAME);
        this.order = order;
        this.item = item;
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, order.getId());
        statement.setLong(3, item.getId());
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setLong(2, order.getId());
        statement.setLong(3, item.getId());
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

    public Order getOrder() {
        return order;
    }

    public Item getItem() {
        return item;
    }
}
