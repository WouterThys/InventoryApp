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


    //
    // STATIC METHODS
    //

    static int autoOrderItemCnt = 0;
    public static synchronized void autoOrderItem(final Item item) {
        if (item != null && settings().getGeneralSettings().isAutoOrderEnabled() && item.isAutoOrder()) {
            if ((!item.getOrderState().equals(Statics.OrderStates.Planned)) && (item.getAutoOrderById() > DbObject.UNKNOWN_ID)) {

                item.setOrderState(Statics.OrderStates.Planned);

                autoOrderItemCnt++;
                System.out.println("autoOrderItem() " + autoOrderItemCnt);

                Distributor autoOrderBy = item.getAutoOrderBy();
                Order autoOrder = null;

                // Find auto
                List<Order> autoOrders = SearchManager.sm().findAutoOrdersByState(Statics.OrderStates.Planned);
                if (autoOrders.size() > 0) {
                    for (Order order : autoOrders) {
                        if (order.getDistributorId() == autoOrderBy.getId()) {
                            autoOrder = order;
                            break;
                        }
                    }
                }

                // Create new if no auto order yet
                if (autoOrder == null) {
                    autoOrder = Order.createAutoOrder(autoOrders.size() + 1, autoOrderBy);
                    autoOrder.addAutoOrderItem(item);
                    autoOrder.save();
                } else {
                    autoOrder.addAutoOrderItem(item);
                    doAutoOrder(autoOrder);
                }
            }
        }
    }

    static int doAutoOrderCnt;
    public static synchronized void doAutoOrder(Order autoOrder) {
        if (autoOrder != null) {

            List<Item> itemList = autoOrder.takeAutoOrderItems();

            if (itemList.size() > 0) {
                doAutoOrderCnt++;
                System.out.println("doAutoOrderCnt() " + doAutoOrderCnt);

                // Replaced?
                for (int i = 0; i < itemList.size(); i++) {
                    Item item = itemList.get(i);
                    if (item.getReplacementItemId() > DbObject.UNKNOWN_ID) {
                        itemList.set(i, item.getReplacementItem());
                    }
                }

                // Order item
                addItemsToOrder(itemList, autoOrder);
            }
        }
    }

    // Order status
    public static boolean moveToOrdered(Order order) {
        boolean result = false;
        if (order != null && order.canBeSaved() && !order.isOrdered()) {
            // Check
            if (validateOrderLines(order)) {
                // Do order
                order.setDateOrdered(DateUtils.now());
                order.setLocked(true);
                order.updateLineStates();
                order.save();
                result = true;
            }
        }
        return result;
    }

    public static void moveToReceived(Order order) {
        //boolean result = false;
        if (order != null && order.canBeSaved() && !order.isReceived()) {
            // Do receive
            order.setDateReceived(DateUtils.now());
            order.setLocked(true);
            order.updateLineStates();
            order.updateLineAmounts(true);
            order.save();
            //result = true;
        }
        //return result;
    }

    public static void backToOrdered(Order order) {
        if (order != null && order.canBeSaved() && order.isReceived()) {
            order.setDateReceived((Date) null);
            order.setLocked(true);
            order.updateLineStates();
            order.updateLineAmounts(false);
            order.save();
        }
    }

    public static void backToPlanned(Order order) {
        if (order != null && order.canBeSaved() && order.isOrdered()) {
            order.setDateReceived((Date) null);
            order.setDateOrdered((Date) null);
            order.setLocked(false);
            order.updateLineStates();
            order.save();
        }
    }

    private static boolean validateOrderLines(Order order) {
        List<String> errors = checkOrder(order);
        if (errors.size() > 0) {
            return false;
        }
        return true;
    }

    private static List<String> checkOrder(Order order) {
        List<String> errorList = new ArrayList<>();

        if (order == null) {
            errorList.add(" - No order selected..");
        } else {
            if (order.getDistributor() == null) {
                errorList.add(" - Order had no distributor..");
            } else {
                if (order.getDistributor().getOrderFileFormat() != null && !order.getDistributor().getOrderFileFormat().isUnknown()) {

                    if (order.getOrderLines().size() < 1) {
                        errorList.add(" - Order has no items..");
                    } else {
                        List<OrderLine> errorItems = order.missingOrderReferences();
                        if (errorItems.size() > 0) {
                            errorList.add(" - Next order items have no reference: ");
                            for (OrderLine oi : errorItems) {
                                errorList.add(" \t * " + oi.getName());
                            }
                        }
                    }
                }
            }
        }
        return errorList;
    }



    public static void addItemToOrder(Item item, Order order) {
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        addItemsToOrder(items, order);
    }

    static int addItemsToOrderCnt;
    public static void addItemsToOrder(List<Item> itemsToOrder, Order order) {
        List<Item> list = new ArrayList<>();
        // Update items
        for (Item item : itemsToOrder) {
            if (order.findOrderLineFor(item) == null) {
                list.add(item);
            }
        }

        LOG.info("Auto ordering " + list.size() + " items for " + order);

        addItemsToOrderCnt++;
        System.out.println("addItemsToOrderCnt() " + addItemsToOrderCnt);

        // Add
        order.addItemsToOrder(list);
    }
}
