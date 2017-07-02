package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.LogManager;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.waldo.inventory.database.SearchManager.sm;

public class Order extends DbObject {

    private static final LogManager LOG = LogManager.LOG(Order.class);
    public static final String TABLE_NAME = "orders";

    private Date dateOrdered;
    private Date dateModified;
    private Date dateReceived;
    private List<OrderItem> orderItems = new ArrayList<>();
    private Distributor distributor;
    private OrderFile orderFile = new OrderFile(this);
    private String orderReference;
    private String trackingNumber;

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
        statement.setString(8, orderReference);
        statement.setString(9, trackingNumber);
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
        statement.setString(8, orderReference);
        statement.setString(9, trackingNumber);
        statement.setLong(10, id); // WHERE id
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
    public Order createCopy() {
        Order order = new Order();
        copyBaseFields(order);
        order.setDateOrdered(getDateOrdered());
        order.setOrderItems(getOrderItems());
        order.setDateModified(getDateModified());
        order.setDateReceived(getDateReceived());
        order.setDistributor(getDistributor());
        order.setOrderFile(getOrderFile());
        order.setOrderReference(getOrderReference());
        order.setTrackingNumber(getTrackingNumber());

        return order;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof Order)) {
                return false;
            } else {
                Order ref = (Order) obj;

                if (!compareIfEqual(ref.getDateOrdered(), getDateOrdered())) return false;
                if (!compareIfEqual(ref.getDateReceived(), getDateReceived())) return false;
                //if (!compareIfEqual(ref.getDateModified(), getDateModified())) return false;
                if (!(ref.getOrderItems().equals(getOrderItems()))) return false;
                if (!(ref.getDistributor().equals(getDistributor()))) return false;
                //if (!(ref.getOrderFile().equals(getOrderFile()))) return false;
                if (!(ref.getOrderReference().equals(getOrderReference()))) return false;
                if (!(ref.getTrackingNumber().equals(getTrackingNumber()))) return false;

            }
        }
        return result;
    }

    @Override
    public void delete() {
            if (canBeSaved) {
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        // First delete all OrderItems
                        for (OrderItem orderItem : getOrderItems()) {
                            removeItemFromList(orderItem); // TODO: check if this can be done with cascaded delete of Foreign Key..
                        }

                        // Delete Order itself
                        doDelete();
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get(10, TimeUnit.SECONDS);
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            JOptionPane.showMessageDialog(null, "Error deleting \"" + name + "\". \n Exception: " + e.getMessage(), "Delete error", JOptionPane.ERROR_MESSAGE);
                            LOG.error("Failed to delete object.", e);
                        }
                    }
                };
                worker.execute();
            } else {
                JOptionPane.showMessageDialog(null, "\""+name+"\" can't be deleted.", "Delete warning", JOptionPane.WARNING_MESSAGE);
            }
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
                // Update the item of the order item
                item.getItem().setOrderState(Statics.ItemOrderState.NONE);
                item.getItem().save();
                // Remove OrderItem from db
                DbManager.db().removeItemFromOrder(item);
                // Remove from list
                orderItems.remove(item);
                // Update modification date
                setDateModified(new Date(System.currentTimeMillis()));

                save();
            }
        }
    }

    public void updateItemReferences() {
        if (distributor != null && orderItems.size() > 0) {
            for (OrderItem oi : orderItems) {
                PartNumber partNumber = sm().findPartNumber(distributor.getId(), oi.getItemId());
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
            total += ((double) oi.getAmount() * oi.getItem().getPrice());
        }

        BigDecimal bd = new BigDecimal(total);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
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

    private boolean compareIfEqual(Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            return true;
        } else if (d1 != null && d2 == null || d1 == null) {
            return false;
        } else {
            return d1.equals(d2);
        }
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

    public long getDistributorId() {
        if (distributor == null) {
            return UNKNOWN_ID;
        } else {
            return distributor.getId();
        }
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public void setDistributor(String id) {
        try {
            this.distributor = sm().findDistributorById(Long.valueOf(id));
        } catch (Exception e) {
            LOG.error("Error setting distributor.", e);
        }
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

    public String getOrderReference() {
        if (orderReference == null) {
            orderReference = "";
        }
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public String getTrackingNumber() {
        if (trackingNumber == null) {
            trackingNumber = "";
        }
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
