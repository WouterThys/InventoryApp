package com.waldo.inventory.managers;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.utils.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class OrderManager {

    private static final LogManager LOG = LogManager.LOG(OrderManager.class);

    // Singleton
    private static final OrderManager INSTANCE = new OrderManager();

    private OrderManager() {
    }

    public static OrderManager om() {
        return INSTANCE;
    }


    // TODO: move a lot of order code here
    // ...
    // ...
    // ...

    // Creating orders
    public static ItemOrder createNewItemOrder(String name) {
        ItemOrder order = null;
        if (SearchManager.sm().findItemOrderByName(name) == null) {
            order = new ItemOrder(name);
            order.save();
        }
        return order;
    }

    public static PcbOrder createNewPcbOrder(String name) {
        PcbOrder order = null;
        if (SearchManager.sm().findPcbOrderByName(name) == null) {
            order = new PcbOrder(name);
            order.save();
        }
        return order;
    }


    public static <T extends Orderable> void addLineToOrder(T line, AbstractOrder<T> order) {
        if (line != null && order != null) {
            ArrayList<T> list = new ArrayList<>();
            list.add(line);
            addItemsToOrder(list, order);
        }
    }


    public static <T extends Orderable> void addItemsToOrder(List<T> linesToOrder, AbstractOrder<T> order) {
        if (linesToOrder != null && order != null) {
            List<T> list = new ArrayList<>();
            // Update items
            for (T line : linesToOrder) {
                if (!order.containsOrderLineFor(line)) {
                    list.add(line);
                }
            }

            LOG.info("Auto ordering " + list.size() + " items for " + order);

            // Add
            for (T line : list) {
                AbstractOrderLine orderLine = line.createOrderLine(order);
                orderLine.save();
            }
        }
    }





    // Auto orders
    public static synchronized void autoOrderItem(final Item item) {
        if (item != null && settings().getGeneralSettings().isAutoOrderEnabled() && item.isAutoOrder()) {
            if ((!item.getOrderState().equals(Statics.OrderStates.Planned)) && (item.getAutoOrderById() > DbObject.UNKNOWN_ID)) {

                item.setOrderState(Statics.OrderStates.Planned);

                Distributor autoOrderBy = item.getAutoOrderBy();
                ItemOrder autoItemOrder = null;

                // Find auto
                List<ItemOrder> autoItemOrders = SearchManager.sm().findAutoOrdersByState(Statics.OrderStates.Planned);
                if (autoItemOrders.size() > 0) {
                    for (ItemOrder itemOrder : autoItemOrders) {
                        if (itemOrder.getDistributorId() == autoOrderBy.getId()) {
                            autoItemOrder = itemOrder;
                            break;
                        }
                    }
                }

                // Create new if no auto order yet
                if (autoItemOrder == null) {
                    autoItemOrder = ItemOrder.createAutoOrder(autoItemOrders.size() + 1, autoOrderBy);
                    autoItemOrder.addAutoOrderItem(item);
                    autoItemOrder.save();
                } else {
                    autoItemOrder.addAutoOrderItem(item);
                    doAutoOrder(autoItemOrder);
                }
            }
        }
    }

    public static synchronized void doAutoOrder(ItemOrder autoItemOrder) {
        if (autoItemOrder != null) {

            List<Item> itemList = autoItemOrder.takeAutoOrderItems();

            if (itemList.size() > 0) {

                // Replaced?
                for (int i = 0; i < itemList.size(); i++) {
                    Item item = itemList.get(i);
                    if (item.getReplacementItemId() > DbObject.UNKNOWN_ID) {
                        itemList.set(i, item.getReplacementItem());
                    }
                }

                // ItemOrder item
                addItemsToOrder(itemList, autoItemOrder);
            }
        }
    }

    // ItemOrder status
    public static boolean moveToOrdered(ItemOrder itemOrder) {
        boolean result = false;
        if (itemOrder != null && itemOrder.canBeSaved() && !itemOrder.isOrdered()) {
            // Check
            //if (validateOrderLines(itemOrder)) {
                // Do itemOrder
                itemOrder.setDateOrdered(DateUtils.now());
                itemOrder.setLocked(true);
                itemOrder.updateLineStates();
                itemOrder.save();
                result = true;
            //}
        }
        return result;
    }

    public static void moveToReceived(ItemOrder itemOrder) {
        //boolean result = false;
        if (itemOrder != null && itemOrder.canBeSaved() && !itemOrder.isReceived()) {
            // Do receive
            itemOrder.setDateReceived(DateUtils.now());
            itemOrder.setLocked(true);
            itemOrder.updateLineStates();
            itemOrder.updateLineAmounts(true);
            itemOrder.save();
            //result = true;
        }
        //return result;
    }

    public static void backToOrdered(ItemOrder itemOrder) {
        if (itemOrder != null && itemOrder.canBeSaved() && itemOrder.isReceived()) {
            itemOrder.setDateReceived((Date) null);
            itemOrder.setLocked(true);
            itemOrder.updateLineStates();
            itemOrder.updateLineAmounts(false);
            itemOrder.save();
        }
    }

    public static void backToPlanned(ItemOrder itemOrder) {
        if (itemOrder != null && itemOrder.canBeSaved() && itemOrder.isOrdered()) {
            itemOrder.setDateReceived((Date) null);
            itemOrder.setDateOrdered((Date) null);
            itemOrder.setLocked(false);
            itemOrder.updateLineStates();
            itemOrder.save();
        }
    }


}
