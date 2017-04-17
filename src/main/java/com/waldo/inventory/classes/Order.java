package com.waldo.inventory.classes;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Order extends DbObject {

    public static final String TABLE_NAME = "orders";

    private Date orderDate;
    private List<Item> orderItems;
    private Date lastModifiedDate;
    private Date receiveDate;
    private boolean isOrdered;

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setDate(3, orderDate);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setDate(3, orderDate);
        statement.setLong(4, id); // WHERE id
        statement.execute();
    }

    @Override
    public String toString() {
        if (orderDate != null) {
            return orderDate.toString();
        } else {
            return Date.valueOf(LocalDate.MIN).toString();
        }
    }

    public Order() {
        super(TABLE_NAME);
    }

    public Order(String tableName, String sqlInsert, String sqlUpdate) {
        super(tableName, sqlInsert, sqlUpdate);
    }

    public static Order getUnknownOrder() {
        Order o = new Order();
        o.setName(UNKNOWN_NAME);
        o.setId(UNKNOWN_ID);
        return o;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
