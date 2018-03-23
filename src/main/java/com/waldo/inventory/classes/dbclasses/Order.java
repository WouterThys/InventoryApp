package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.ItemOrderStates;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.LogManager;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.DateUtils;
import com.waldo.utils.OpenUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Order extends DbObject {

    private static final LogManager LOG = LogManager.LOG(Order.class);
    public static final String TABLE_NAME = "orders";

    private Date dateOrdered;
    private Date dateModified;
    private Date dateReceived;
    private List<OrderItem> orderItems;
    private long distributorId;
    private Distributor distributor;
    private String orderReference;
    private String trackingNumber;

    // Runtime variables
    private boolean isLocked;
    private final List<OrderItem> tempOrderItems = new ArrayList<>(); // List with items not yet added to order

    public Order() {
        super(TABLE_NAME);
    }

    public Order(String name) {
        super(TABLE_NAME);
        this.name = name;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        dateModified = DateUtils.now();

        int ndx = addBaseParameters(statement);
        if (dateOrdered != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateOrdered.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }
        statement.setTimestamp(ndx++, new Timestamp(dateModified.getTime()), Calendar.getInstance());
        if (dateReceived != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateReceived.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }
        statement.setLong(ndx++, distributorId);
        statement.setString(ndx++, orderReference);
        statement.setString(ndx++, trackingNumber);
        return ndx;
    }

    @Override
    public String toString() {
        if (isOrdered()) {
            return dateOrdered.toString() + " - " + super.toString();
        } else {
            if (isUnknown() || !canBeSaved()) {
                return super.toString();
            } else {
                if (dateModified != null) {
                    return dateModified.toString() + " - " + super.toString();
                } else {
                    return "";
                }
            }
        }
    }

    @Override
    public Order createCopy(DbObject copyInto) {
        Order order = (Order) copyInto;
        copyBaseFields(order);
        order.setDateOrdered(getDateOrdered());
        order.setOrderItems(getOrderItems());
        order.setDateModified(getDateModified());
        order.setDateReceived(getDateReceived());
        order.setDistributorId(getDistributorId());
        order.setOrderReference(getOrderReference());
        order.setTrackingNumber(getTrackingNumber());

        return order;
    }

    @Override
    public Order createCopy() {
        return createCopy(new Order());
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
                if (!(ref.getDistributorId() == (getDistributorId()))) return false;
                if (!(ref.getOrderReference().equals(getOrderReference()))) return false;
                if (!(ref.getTrackingNumber().equals(getTrackingNumber()))) return false;

            }
        }
        return result;
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    public static class SortAllOrders implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if (o1 == null || o2 == null) {
                return 1;
            }
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

    public static class SortUnordered implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if (o1.getDateModified() != null && o2.getDateReceived() == null) {
                return -1;
            } else if (o1.getDateModified() == null && o2.getDateModified() != null) {
                return 1;
            } else if (o1.getDateModified() == null && o2.getDateModified() == null) {
                return 0;
            } else {
                return o1.getDateModified().compareTo(o2.getDateModified());
            }
        }
    }

    public void copyOrderLinesToClipboard() {
        String orderText = createOrderText();
        StringSelection selection = new StringSelection(orderText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private String createOrderText() {
        StringBuilder builder = new StringBuilder();
        for (OrderItem orderItem : getOrderItems()) {
            builder.append(orderItem.getDistributorPartLink().getItemRef());
            builder.append(getDistributor().getOrderFileFormat().getSeparator());
            builder.append(orderItem.getAmount());
            builder.append("\n");
        }
        return builder.toString();
    }

    public void browseOrderPage() throws IOException {
        if (getDistributorId() > UNKNOWN_ID) {
            OpenUtils.browseLink(getDistributor().getOrderLink());
        }
    }

    public static Order getUnknownOrder() {
        Order o = new Order();
        o.setName(UNKNOWN_NAME);
        o.setId(UNKNOWN_ID);
        o.setCanBeSaved(false);
        return o;
    }

    public boolean containsItemId(long id) {
        for (OrderItem oi : getOrderItems()) {
            if (oi.getItemId() == id) {
                return true;
            }
        }
        return false;
    }

    public void addItemToList(OrderItem item) {
        if (item != null) {
            if (!orderItems.contains(item)) {
                orderItems.add(item);
                setDateModified(new Date(System.currentTimeMillis()));
            }
        }
    }

    public void addItemToTempList(OrderItem item) {
        if (item != null) {
            if (!tempOrderItems.contains(item)) {
                tempOrderItems.add(item);
            }
        }
    }

    public void removeItemFromList(OrderItem item) {
        if (item != null) {
            if (orderItems.contains(item)) {
                // Remove OrderItem from db
                DatabaseAccess.db().removeItemFromOrder(item);
                // Remove from list
                orderItems.remove(item);
                // Update modification date
                setDateModified(new Date(System.currentTimeMillis()));
                // Update the item of the order item
                SwingUtilities.invokeLater(item::updateOrderState);
            }
        }
    }

    public void updateItemReferences() {
        for (OrderItem oi : orderItems) {
            oi.updateDistributorPart();
        }
    }

    public Price getTotalPrice() {
        Price total = new Price();
        for (OrderItem oi : getOrderItems()) {
            total = Price.add(total, oi.getTotalPrice());
        }

//        BigDecimal bd = new BigDecimal(total);
//        bd = bd.setScale(2, RoundingMode.HALF_UP);
//        return bd.doubleValue();
        return total;
    }

    public void updateItemStates() {
        for (OrderItem oi : getOrderItems()) {
            oi.updateOrderState();
        }
    }

    public void updateItemAmounts(boolean increment) {
        for (OrderItem oi : getOrderItems()) {
            Item item = oi.getItem();
            int current = item.getAmount();
            item.setAmountType(Statics.ItemAmountTypes.Exact);
            if (increment) {
                item.setAmount(current + oi.getAmount());
            } else {
                item.setAmount(current - oi.getAmount());
                if (item.getAmount() < 0) {
                    item.setAmount(0);
                }
            }
            item.save();
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
        return d1 == null && d2 == null || !(d1 != null && d2 == null || d1 == null) && d1.equals(d2);
    }

    public Date getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(Date dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public void setDateOrdered(Timestamp dateOrdered) {
        if (dateOrdered != null) {
            this.dateOrdered = new Date(dateOrdered.getTime());
        }
    }

    public List<OrderItem> getOrderItems() {
        if (orderItems == null) {
            orderItems = cache().getOrderedItems(id);
        }
        return orderItems;
    }

    private void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public void setDateModified(Timestamp dateModified) {
        if (dateModified != null) {
            this.dateModified = new Date(dateModified.getTime());
        }
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public void setDateReceived(Timestamp dateReceived) {
        if (dateReceived != null) {
            this.dateReceived = new Date(dateReceived.getTime());
        }
    }

    public Distributor getDistributor() {
        if (distributor == null) {
            distributor = SearchManager.sm().findDistributorById(distributorId);
        }
        return distributor;
    }

    public long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(long id) {
        if (distributor != null && distributor.getId() != id) {
            distributor = null;
        }
        this.distributorId = id;
    }

    public void setDistributor(String id) {
        try {
            this.distributorId = Long.valueOf(id);
            distributor = null;
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

    public boolean isPlanned() {
        return (dateOrdered == null && dateReceived == null);
    }

    public ItemOrderStates getOrderState() {
        if (!isOrdered() && !isReceived()) {
            return ItemOrderStates.Planned;
        } else if (isOrdered() && !isReceived()) {
            return ItemOrderStates.Ordered;
        } else if (isOrdered() && isReceived()) {
            return ItemOrderStates.Received;
        } else {
            return ItemOrderStates.NoOrder;
        }
    }

    public List<OrderItem> missingOrderReferences() {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItem oi : getOrderItems()) {
            if (oi.getDistributorPartId() <= UNKNOWN_ID) {
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

    public List<OrderItem> getTempOrderItems() {
        return tempOrderItems;
    }

    public void clearTempOrderList() {
        tempOrderItems.clear();
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
