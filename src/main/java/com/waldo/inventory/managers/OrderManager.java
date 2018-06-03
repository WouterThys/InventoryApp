package com.waldo.inventory.managers;

import com.waldo.inventory.Main;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderLine;
import com.waldo.utils.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    // Singleton
    private static final OrderManager INSTANCE = new OrderManager();
    public static OrderManager om() {
        return INSTANCE;
    }
    private OrderManager() {
    }


    // TODO: move a lot of order code here
    // ...
    // ...
    // ...





    //
    // STATIC METHODS
    //

    public static void autoOrderItem(Item item) {
        if (item != null && Main.AUTO_ORDER && item.isAutoOrder()) {

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
}
