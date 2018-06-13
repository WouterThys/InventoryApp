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

    static int doAutoOrderCnt;
    public static synchronized void doAutoOrder(ItemOrder autoItemOrder) {
        if (autoItemOrder != null) {

            List<Item> itemList = autoItemOrder.takeAutoOrderItems();

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
            if (validateOrderLines(itemOrder)) {
                // Do itemOrder
                itemOrder.setDateOrdered(DateUtils.now());
                itemOrder.setLocked(true);
                itemOrder.updateLineStates();
                itemOrder.save();
                result = true;
            }
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

    private static boolean validateOrderLines(ItemOrder itemOrder) {
        List<String> errors = checkOrder(itemOrder);
        if (errors.size() > 0) {
            return false;
        }
        return true;
    }

    private static List<String> checkOrder(ItemOrder itemOrder) {
        List<String> errorList = new ArrayList<>();

        if (itemOrder == null) {
            errorList.add(" - No itemOrder selected..");
        } else {
            if (itemOrder.getDistributor() == null) {
                errorList.add(" - ItemOrder had no distributor..");
            } else {
                if (itemOrder.getDistributor().getOrderFileFormat() != null && !itemOrder.getDistributor().getOrderFileFormat().isUnknown()) {

                    if (itemOrder.getItemOrderLines().size() < 1) {
                        errorList.add(" - ItemOrder has no items..");
                    } else {
                        List<ItemOrderLine> errorItems = itemOrder.missingOrderReferences();
                        if (errorItems.size() > 0) {
                            errorList.add(" - Next itemOrder items have no reference: ");
                            for (ItemOrderLine oi : errorItems) {
                                errorList.add(" \t * " + oi.getName());
                            }
                        }
                    }
                }
            }
        }
        return errorList;
    }



    public static void addItemToOrder(Item item, ItemOrder itemOrder) {
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        addItemsToOrder(items, itemOrder);
    }

    static int addItemsToOrderCnt;
    public static void addItemsToOrder(List<Item> itemsToOrder, ItemOrder itemOrder) {
        List<Item> list = new ArrayList<>();
        // Update items
        for (Item item : itemsToOrder) {
            if (itemOrder.findOrderLineFor(item) == null) {
                list.add(item);
            }
        }

        LOG.info("Auto ordering " + list.size() + " items for " + itemOrder);

        addItemsToOrderCnt++;
        System.out.println("addItemsToOrderCnt() " + addItemsToOrderCnt);

        // Add
        itemOrder.addItemsToOrder(list);
    }
}
