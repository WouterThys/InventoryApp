package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.Utils.Statics.OrderStates;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.managers.OrderManager;
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
import java.util.*;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ItemOrder extends DbObject {

    public static final String TABLE_NAME = "itemorders";

    private Date dateOrdered;
    private Date dateModified;
    private Date dateReceived;

    private List<ItemOrderLine> itemOrderLines;

    private long distributorId;
    private Distributor distributor;

    private String orderReference;
    private String trackingNumber;

    private boolean isAutoOrder = false;

    // Runtime variables
    private boolean isLocked;
    private final List<ItemOrderLine> tempOrderItems = new ArrayList<>(); // List with items not yet added to order
    private final List<Item> autoOrderItems = new ArrayList<>();

    public ItemOrder() {
        super(TABLE_NAME);
    }

    public ItemOrder(String name) {
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
        statement.setBoolean(ndx++, isAutoOrder());
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
    public ItemOrder createCopy(DbObject copyInto) {
        ItemOrder itemOrder = (ItemOrder) copyInto;
        copyBaseFields(itemOrder);
        itemOrder.setDateOrdered(getDateOrdered());
        itemOrder.setDateModified(getDateModified());
        itemOrder.setDateReceived(getDateReceived());
        itemOrder.setDistributorId(getDistributorId());
        itemOrder.setOrderReference(getOrderReference());
        itemOrder.setTrackingNumber(getTrackingNumber());
        itemOrder.setAutoOrder(isAutoOrder());

        return itemOrder;
    }

    @Override
    public ItemOrder createCopy() {
        return createCopy(new ItemOrder());
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof ItemOrder)) {
                return false;
            } else {
                ItemOrder ref = (ItemOrder) obj;

                if (!compareIfEqual(ref.getDateOrdered(), getDateOrdered())) return false;
                if (!compareIfEqual(ref.getDateReceived(), getDateReceived())) return false;
                if (!(ref.getItemOrderLines().size() == (getItemOrderLines().size()))) return false;
                if (!(ref.getDistributorId() == (getDistributorId()))) return false;
                if (!(ref.getOrderReference().equals(getOrderReference()))) return false;
                if (!(ref.getTrackingNumber().equals(getTrackingNumber()))) return false;
                if (!(ref.isAutoOrder() == isAutoOrder())) return false;

            }
        }
        return result;
    }

    public static ItemOrder createDummyOrder(String name) {
        ItemOrder itemOrder = new ItemOrder(name);
        itemOrder.setCanBeSaved(false);
        return itemOrder;
    }

    public static ItemOrder createAutoOrder(int autoOrderNumber, Distributor distributor) {
        ItemOrder itemOrder = null;
        if (distributor != null) {
            itemOrder = new ItemOrder();
            itemOrder.setAutoOrder(true);
            itemOrder.setName("AUTO-ORDER_" + autoOrderNumber);
            itemOrder.setDistributorId(distributor.getId());
        }
        return itemOrder;
    }

    public synchronized void addAutoOrderItem(Item item) {
        if (item != null && isAutoOrder()) {
            if (!autoOrderItems.contains(item)) {
                autoOrderItems.add(item);
            }
        }
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert:
                cache().add(this);
            case Update:
                if (isAutoOrder()) {
                    SwingUtilities.invokeLater(() -> {
                        if (autoOrderItems.size() > 0) {
                            OrderManager.doAutoOrder(this);
                        }
                    });
                }
                break;

            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    public static class SortAllOrders implements Comparator<ItemOrder> {
        @Override
        public int compare(ItemOrder o1, ItemOrder o2) {
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

    public static class SortUnordered implements Comparator<ItemOrder> {
        @Override
        public int compare(ItemOrder o1, ItemOrder o2) {
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


    public Map<String, Item> addItemsToOrder(List<Item> itemsToOrder) {
        Map<String, Item> failedItems = null;
        if (getDistributorType() == DistributorType.Items) {
            for (Item item : itemsToOrder) {
                try {
                    ItemOrderLine itemOrderLine = findOrderLineFor(item);
                    if (itemOrderLine == null) {
                        itemOrderLine = new ItemOrderLine(this, item, Math.max(0, item.getMaximum() - item.getAmount()));
                    }
                    itemOrderLine.save();
                } catch (Exception e) {
                    if (failedItems == null) {
                        failedItems = new HashMap<>();
                    }
                    failedItems.put("Failed to add item " + item.toString(), item);
                }
            }
        } else {
            failedItems = new HashMap<>();
            for(Item item : itemsToOrder) {
                failedItems.put("Can not add items to an order for PCB's", item);
            }
        }
        return failedItems;
    }


    public void copyOrderLinesToClipboard() {
        String orderText = createOrderText();
        StringSelection selection = new StringSelection(orderText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private String createOrderText() {
        StringBuilder builder = new StringBuilder();
        for (ItemOrderLine itemOrderLine : getItemOrderLines()) {
            builder.append(itemOrderLine.getDistributorPartLink().getReference());
            builder.append(getDistributor().getOrderFileFormat().getSeparator());
            builder.append(itemOrderLine.getAmount());
            builder.append("\n");
        }
        return builder.toString();
    }

    public void browseOrderPage() throws IOException {
        if (getDistributorId() > UNKNOWN_ID) {
            OpenUtils.browseLink(getDistributor().getOrderLink());
        }
    }

    public static ItemOrder getUnknownOrder() {
        ItemOrder o = new ItemOrder();
        o.setName(UNKNOWN_NAME);
        o.setId(UNKNOWN_ID);
        o.setCanBeSaved(false);
        return o;
    }

    public boolean containsOrderLineFor(Item item) {
        return findOrderLineFor(item) != null;
    }

    public ItemOrderLine findOrderLineFor(Item item) {
        if (item != null) {
            for (ItemOrderLine ol : getItemOrderLines()) {
                if (ol.getItemId() == item.getId()) {
                    return ol;
                }
            }
        }
        return null;
    }

    public void addOrderLine(ItemOrderLine line) {
        if (line != null) {
            if (!itemOrderLines.contains(line)) {
                itemOrderLines.add(line);
                setDateModified(new Date(System.currentTimeMillis()));
            }
        }
    }

    public void addItemToTempList(ItemOrderLine item) {
        if (item != null) {
            if (!tempOrderItems.contains(item)) {
                tempOrderItems.add(item);
            }
        }
    }

    public void removeOrderLine(ItemOrderLine line) {
        if (line != null) {
            if (itemOrderLines.contains(line)) {
                // Remove OrderItem from db
                line.delete();
                // Remove from list
                itemOrderLines.remove(line);
                // Update modification date
                setDateModified(new Date(System.currentTimeMillis()));
                // Update the item of the order item
                SwingUtilities.invokeLater(line::updateOrderState);
            }
        }
    }

    public void updateItemReferences() {
        for (ItemOrderLine oi : getItemOrderLines()) {
            oi.updateDistributorPart();
        }
    }

    public Price getTotalPrice() {
        Price total = new Price(0, Statics.PriceUnits.Euro);
        for (ItemOrderLine oi : getItemOrderLines()) {
            total = Price.add(total, oi.getTotalPrice());
        }
        return total;
    }

    public void updateLineStates() {
        for (ItemOrderLine oi : getItemOrderLines()) {
            oi.updateOrderState();
        }
    }

    public void updateLineAmounts(boolean increment) {
        for (ItemOrderLine oi : getItemOrderLines()) {
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

    public List<ItemOrderLine> getItemOrderLines() {
        if (itemOrderLines == null) {
            itemOrderLines = SearchManager.sm().findItemOrderLinesForOrder(getId());
        }
        return itemOrderLines;
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
            if (getItemOrderLines().size() > 0) {
                for (ItemOrderLine orderItem : getItemOrderLines()) {
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

    public Statics.OrderStates getOrderState() {
        if (!isOrdered() && !isReceived()) {
            return Statics.OrderStates.Planned;
        } else if (isOrdered() && !isReceived()) {
            return Statics.OrderStates.Ordered;
        } else if (isOrdered() && isReceived()) {
            return OrderStates.Received;
        } else {
            return Statics.OrderStates.NoOrder;
        }
    }

    public List<ItemOrderLine> missingOrderReferences() {
        List<ItemOrderLine> items = new ArrayList<>();
        for (ItemOrderLine oi : getItemOrderLines()) {
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


    public synchronized List<Item> takeAutoOrderItems() {
        List<Item> autoOrderList = new ArrayList<>(autoOrderItems);
        autoOrderItems.clear();
        return autoOrderList;
    }


    public boolean isAutoOrder() {
        return isAutoOrder;
    }

    public void setAutoOrder(boolean autoOrder) {
        isAutoOrder = autoOrder;
    }
}
