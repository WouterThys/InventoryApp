package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.ItemOrderStates;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.Price;
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

    public static final String TABLE_NAME = "orders";

    private Date dateOrdered;
    private Date dateModified;
    private Date dateReceived;

    private List<OrderLine> orderLines;

    private long distributorId;
    private Distributor distributor;

    private String orderReference;
    private String trackingNumber;

    // Runtime variables
    private boolean isLocked;
    private final List<OrderLine> tempOrderItems = new ArrayList<>(); // List with items not yet added to order

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
        statement.setLong(ndx++, getDistributorId());
        statement.setString(ndx++, getOrderReference());
        statement.setString(ndx++, getTrackingNumber());
        return ndx;
    }

    @Override
    public String toString() {
        if (isOrdered()) {
            return DateUtils.formatMonthDate(dateOrdered) + " - " + super.toString();
        } else {
            if (isUnknown() || !canBeSaved()) {
                return super.toString();
            } else {
                if (dateModified != null) {
                    return DateUtils.formatMonthDate(dateModified) + " - " + super.toString();
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
                if (!(ref.getOrderLines().size() == (getOrderLines().size()))) return false;
                if (!(ref.getDistributorId() == (getDistributorId()))) return false;
                if (!(ref.getOrderReference().equals(getOrderReference()))) return false;
                if (!(ref.getTrackingNumber().equals(getTrackingNumber()))) return false;

            }
        }
        return result;
    }

    public static Order createDummyOrder(String name) {
        Order order = new Order(name);
        order.setCanBeSaved(false);
        return order;
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
        for (OrderLine orderLine : getOrderLines()) {
            builder.append(orderLine.getDistributorPartLink().getReference());
            builder.append(getDistributor().getOrderFileFormat().getSeparator());
            builder.append(orderLine.getAmount());
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

    public boolean containsOrderLineFor(Item item) {
        return findOrderLineFor(item) != null;
    }

    public boolean containsOrderLineFor(ProjectPcb pcb) {
        if (pcb != null && getDistributorType() == DistributorType.Items) {
            for (OrderLine ol : getOrderLines()) {
                if (ol.getItemId() == pcb.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public OrderLine findOrderLineFor(Item item) {
        if (item != null && getDistributorType() == DistributorType.Items) {
            for (OrderLine ol : getOrderLines()) {
                if (ol.getItemId() == item.getId()) {
                    return ol;
                }
            }
        }
        return null;
    }

    public void addOrderLine(OrderLine line) {
        if (line != null) {
            if (!orderLines.contains(line)) {
                orderLines.add(line);
                setDateModified(new Date(System.currentTimeMillis()));
            }
        }
    }

    public void addItemToTempList(OrderLine item) {
        if (item != null) {
            if (!tempOrderItems.contains(item)) {
                tempOrderItems.add(item);
            }
        }
    }

    public void removeOrderLine(OrderLine line) {
        if (line != null) {
            if (orderLines.contains(line)) {
                // Remove OrderItem from db
                line.delete();
                // Remove from list
                orderLines.remove(line);
                // Update modification date
                setDateModified(new Date(System.currentTimeMillis()));
                // Update the item of the order item
                SwingUtilities.invokeLater(line::updateOrderState);
            }
        }
    }

    public void updateItemReferences() {
        for (OrderLine oi : getOrderLines()) {
            oi.updateDistributorPart();
        }
    }

    public Price getTotalPrice() {
        Price total = new Price(0, Statics.PriceUnits.Euro);
        for (OrderLine oi : getOrderLines()) {
            total = Price.add(total, oi.getTotalPrice());
        }
        return total;
    }

    public void updateLineStates() {
        for (OrderLine oi : getOrderLines()) {
            oi.updateOrderState();
        }
    }

    public void updateLineAmounts(boolean increment) {
        for (OrderLine oi : getOrderLines()) {
            oi.updateLineAmount(increment);
        }
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

    public List<OrderLine> getOrderLines() {
        if (orderLines == null) {
            orderLines = SearchManager.sm().findOrderLinesForOrder(getId());
        }
        return orderLines;
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
        if (distributor == null && getDistributorId() > UNKNOWN_ID) {
            distributor = SearchManager.sm().findDistributorById(distributorId);
        }
        return distributor;
    }

    public long getDistributorId() {
        if (distributorId <= UNKNOWN_ID) {
            distributorId = UNKNOWN_ID;
        }
        return distributorId;
    }

    public void setDistributorId(long id) {
        if (distributor != null && distributor.getId() != id) {
            distributor = null;
            if (getOrderLines().size() > 0) {
                for (OrderLine orderItem : getOrderLines()) {
                    orderItem.updateDistributorPart();
                }
            }
        }
        this.distributorId = id;
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

    public List<OrderLine> missingOrderReferences() {
        List<OrderLine> items = new ArrayList<>();
        for (OrderLine oi : getOrderLines()) {
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

    public DistributorType getDistributorType() {
        if (getDistributor() != null) {
            return distributor.getDistributorType();
        }
        return null;
    }


    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }


    public List<OrderLine> getTempOrderItems() {
        return tempOrderItems;
    }

    public void clearTempOrderList() {
        tempOrderItems.clear();
    }
}
