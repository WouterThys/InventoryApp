package com.waldo.inventory.gui.panels.mainpanel.itemlisteners;

import com.waldo.inventory.classes.dbclasses.Item;

public interface ItemDetailListener {
    void onShowDataSheet(Item item);
    void onOrderItem(Item item);
    void onShowHistory(Item item);
}
