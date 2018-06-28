package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.OrderManager;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ItemOrder extends AbstractOrder<Item> {

    public static final String TABLE_NAME = "itemorders";

    // Runtime variables
    private final List<Item> autoOrderItems = new ArrayList<>();

    public ItemOrder() {
        super(TABLE_NAME);
    }

    public ItemOrder(String name) {
        super(TABLE_NAME, name);
    }

    @Override
    public ItemOrder createCopy(DbObject copyInto) {
        ItemOrder itemOrder = (ItemOrder) copyInto;
        copyBaseFields(itemOrder);
        itemOrder.setDateOrdered(getDateOrdered());
        itemOrder.setDateModified(getDateModified());
        itemOrder.setDateReceived(getDateReceived());
        itemOrder.setDistributorId(getDistributorId());
        itemOrder.setVAT(getVAT());
        itemOrder.setOrderReference(getOrderReference());
        itemOrder.setTrackingNumber(getTrackingNumber());
        itemOrder.setAutoOrder(isAutoOrder());

        return itemOrder;
    }

    @Override
    public ItemOrder createCopy() {
        return createCopy(new ItemOrder());
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

    public static ItemOrder getUnknownOrder() {
        ItemOrder o = new ItemOrder();
        o.setName(UNKNOWN_NAME);
        o.setId(UNKNOWN_ID);
        o.setCanBeSaved(false);
        return o;
    }

    public List<AbstractOrderLine<Item>> missingOrderReferences() {
        List<AbstractOrderLine<Item>> items = new ArrayList<>();
        for (AbstractOrderLine<Item> oi : getOrderLines()) {
            if (oi.getDistributorPartId() <= UNKNOWN_ID) {
                items.add(oi);
            }
        }
        return items;
    }

    public synchronized List<Item> takeAutoOrderItems() {
        List<Item> autoOrderList = new ArrayList<>(autoOrderItems);
        autoOrderItems.clear();
        return autoOrderList;
    }

    @Override
    public List<AbstractOrderLine<Item>> getOrderLines() {
        if (orderLines == null) {
            orderLines = new ArrayList<>();
            orderLines.addAll(SearchManager.sm().findItemOrderLinesForOrder(getId()));
        }
        return orderLines;
    }
}
