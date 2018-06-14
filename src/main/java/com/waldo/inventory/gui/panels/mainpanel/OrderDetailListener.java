package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;

public interface OrderDetailListener {
    void onSetOrderItemAmount(AbstractOrderLine line, int amount);
    void onEditReference(AbstractOrderLine line);
    void onEditPrice(AbstractOrderLine line);
}
