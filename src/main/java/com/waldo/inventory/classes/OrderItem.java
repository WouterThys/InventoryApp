package com.waldo.inventory.classes;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItem extends DbObject {

    public static final String TABLE_NAME = "orderitems";

    private static final String insertSql = "INSERT INTO "+TABLE_NAME+" (" +
            "(name, iconpath, itemid, lastmodifieddate, orderdate, receivedate, isordered) VALUES " +
            "(?, ?, ?, ?, ?, ?, ?)";



    private static final String updateSql =
            "UPDATE "+TABLE_NAME+" " +
                    "SET name = ?, iconpath = ?, itemid = ?, lastmodifieddate = ?, orderdate = ?, receivedate = ?, isordered = ? " +
                    "WHERE id = ?;";


    private Item itemToOrder;
    private Date lastModifiedDate;
    private Date orderDate;
    private Date receiveDate;

    private boolean isOrdered;


    public OrderItem() {
        super(TABLE_NAME, insertSql, updateSql);
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, itemToOrder.getId());
        statement.setDate(4, lastModifiedDate);
        statement.setDate(5, orderDate);
        statement.setDate(6, receiveDate);
        statement.setBoolean(7, isOrdered);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, itemToOrder.getId());
        statement.setDate(4, lastModifiedDate);
        statement.setDate(5, orderDate);
        statement.setDate(6, receiveDate);
        statement.setBoolean(7, isOrdered);
        statement.setLong(8, id); // WHERE id
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

    public Item getItemToOrder() {
        return itemToOrder;
    }

    public void setItemToOrder(Item itemToOrder) {
        this.itemToOrder = itemToOrder;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public boolean isOrdered() {
        return isOrdered;
    }

    public void setOrdered(boolean ordered) {
        isOrdered = ordered;
    }
}
