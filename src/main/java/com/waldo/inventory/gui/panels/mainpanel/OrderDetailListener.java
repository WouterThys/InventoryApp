package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.dbclasses.OrderLine;

public interface OrderDetailListener {
    void onSetOrderItemAmount(OrderLine line, int amount);
    void onEditReference(OrderLine line);
    void onEditPrice(OrderLine line);
}
