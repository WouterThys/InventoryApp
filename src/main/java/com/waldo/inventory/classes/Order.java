package com.waldo.inventory.classes;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class Order extends DbObject {

    public static final String TABLE_NAME = "orders";

    private Date dateOrdered;
    private List<Item> orderItems;
    private Date dateModified;
    private Date dateReceived;
    private Distributor distributor;

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        dateModified = new Date(Calendar.getInstance().getTime().getTime());

        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setDate(3, dateOrdered);
        statement.setDate(4, dateModified);
        statement.setDate(5, dateReceived);
        statement.setLong(6, distributor.getId());
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        dateModified = new Date(Calendar.getInstance().getTime().getTime());

        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setDate(3, dateOrdered);
        statement.setDate(4, dateModified);
        statement.setDate(5, dateReceived);
        statement.setLong(6, distributor.getId());
        statement.setLong(7, id); // WHERE id
        statement.execute();
    }

    @Override
    public String toString() {
        if (dateOrdered != null) {
            return dateOrdered.toString();
        } else {
            return Date.valueOf(LocalDate.MIN).toString();
        }
    }

    public Order() {
        super(TABLE_NAME);
    }

    public Order(String name) {
        super(TABLE_NAME);
        this.name = name;
    }

    public static Order getUnknownOrder() {
        Order o = new Order();
        o.setName(UNKNOWN_NAME);
        o.setId(UNKNOWN_ID);
        return o;
    }

    public Date getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(Date dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public List<Item> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Item> orderItems) {
        this.orderItems = orderItems;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public boolean isOrdered() {
        if (dateReceived != null && dateOrdered != null) {
            return  (dateReceived.after(dateOrdered));
        }
        return false;
    }
}
