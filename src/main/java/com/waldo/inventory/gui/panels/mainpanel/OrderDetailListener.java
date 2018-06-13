package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.dbclasses.ItemOrderLine;

public interface OrderDetailListener {
    void onSetOrderItemAmount(ItemOrderLine line, int amount);
    void onEditReference(ItemOrderLine line);
    void onEditPrice(ItemOrderLine line);
}
