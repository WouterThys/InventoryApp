package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.Order;

public interface OrdersChangedListener {
    void onOrderAdded(Order order);
    void onOrderUpdated(Order order);
    void onOrderDeleted(Order order);
}
