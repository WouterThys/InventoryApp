package com.waldo.inventory.gui.panels.mainpanel.itemlisteners;

import com.waldo.inventory.classes.dbclasses.OrderItem;

public interface OrderDetailListener {
    void onSetOrderItemAmount(OrderItem orderItem, int amount);
    void onEditReference(OrderItem orderItem);
    void onEditPrice(OrderItem orderItem);
}
