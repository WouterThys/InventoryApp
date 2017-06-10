package com.waldo.inventory.classes;

import com.sun.org.apache.xpath.internal.operations.Or;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DbManager;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class Order extends DbObject {

    public static final String TABLE_NAME = "orders";

    private Date dateOrdered;
    private List<OrderItem> orderItems = new ArrayList<>();
    private Date dateModified;
    private Date dateReceived;
    private Distributor distributor;
    private OrderFile orderFile = new OrderFile(this);

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        dateModified = new Date(Calendar.getInstance().getTime().getTime());

        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setDate(3, dateOrdered);
        statement.setDate(4, dateModified);
        statement.setDate(5, dateReceived);
        statement.setLong(6, distributor.getId());
        statement.setString(7, orderFile.getOrderFileName());
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
        statement.setString(7, orderFile.getOrderFileName());
        statement.setLong(8, id); // WHERE id
        statement.execute();
    }

    @Override
    public String toString() {
        if (isOrdered()) {
            return dateOrdered.toString() + " - " + super.toString();
        } else {
            if (isUnknown() || !canBeSaved()) {
                return super.toString();
            } else {
                return dateModified.toString() + " - " + super.toString();
            }
        }
    }

    @Override
    public Order createCopy(DbObject original) {
        Order order = new Order();
        copyBaseFields(order);
        order.setDateOrdered(((Order)original).getDateOrdered());
        order.setOrderItems(((Order)original).getOrderItems());
        order.setDateModified(((Order)original).getDateModified());
        order.setDateReceived(((Order)original).getDateReceived());
        order.setDistributor(((Order)original).getDistributor());
        order.setOrderFile(((Order)original).getOrderFile());

        return order;
    }

    public static class OrderAllOrders implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if (o1.isUnknown()) {
                return 1;
            }
            if (o2.isUnknown()) {
                return -1;
            }
            if (o1.isOrdered() && o2.isOrdered()) { // Both ordered
                return o1.getDateOrdered().compareTo(o2.dateOrdered);
            } else if (o1.isOrdered() && !o2.isOrdered()) { // o1 ordered
                return 1;
            } else if (!o1.isOrdered() && o2.isOrdered()) { // o2 ordered
                return -1;
            } else { // None ordered
                return o1.getDateModified().compareTo(o2.getDateModified());
            }
        }
    }

    public static class OrderUnordered implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            return o1.getDateModified().compareTo(o2.getDateModified());
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
        o.setCanBeSaved(false);
        return o;
    }

    public void addItemToList(OrderItem item) {
        if (item != null) {
            if (!orderItems.contains(item)) {
                orderItems.add(item);
                setDateModified(new Date(System.currentTimeMillis()));
                save();
            }
        }
    }

    public void removeItemFromList(OrderItem item) {
        if (item != null) {
            if (orderItems.contains(item)) {
                item.getItem().setOrderState(Statics.ItemOrderState.NONE);
                item.getItem().save();
                DbManager.db().removeItemFromOrder(item);

                orderItems.remove(item);
                setDateModified(new Date(System.currentTimeMillis()));

                save();
            }
        }
    }

    public void updateItemReferences() {
        if (distributor != null && orderItems.size() > 0) {
            for (OrderItem oi : orderItems) {
                PartNumber partNumber = DbManager.db().findPartNumber(distributor.getId(), oi.getItemId());
                if (partNumber != null) {
                    if (!oi.getItemRef().equals(partNumber.getItemRef())) {
                        oi.setItemRef(partNumber.getItemRef());
                        oi.save();
                    }
                } else {
                    oi.setItemRef("");
                    oi.save();
                }
            }
        }
    }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem oi : getOrderItems()) {
            total += oi.getAmount() + oi.getItem().getPrice();
        }
        return Math.round(total);
    }

    public boolean hasOrderFile() {
        return orderFile.isSuccess();
    }

    public void setItemStates(int state) {
        for (OrderItem oi : getOrderItems()) {
            oi.getItem().setOrderState(state);
            oi.getItem().save();
        }
    }

    public void updateItemAmounts() {
        for (OrderItem oi : getOrderItems()) {
            int current = oi.getItem().getAmount();
            oi.getItem().setAmount(current + oi.getAmount());
            oi.getItem().save();
        }
    }

    public OrderItem findOrderItemInOrder(long itemId) {
        for (OrderItem oi : getOrderItems()) {
            if (oi.getItemId() == itemId) {
                return oi;
            }
        }
        return null;
    }

    public Date getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(Date dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
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
        return (dateOrdered != null);
    }

    public boolean isReceived() {
        return dateReceived != null;
    }

    public OrderFile getOrderFile() {
        return orderFile;
    }

    public void setOrderFile(OrderFile orderFile) {
        this.orderFile = orderFile;
    }

    public void setOrderFile(String fileName) {
        orderFile.loadOrderFile(fileName);
    }

    public List<OrderItem> missingOrderReferences() {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItem oi : getOrderItems()) {
            if (oi.getItemRef() == null || oi.getItemRef().isEmpty()) {
                items.add(oi);
            }
        }
        return items;
    }
}
