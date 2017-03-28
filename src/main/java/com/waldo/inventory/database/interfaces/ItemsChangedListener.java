package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.Item;

public interface ItemsChangedListener {
    void onItemAdded(Item item);
    void onItemUpdated(Item item);
    void onItemDeleted(Item item);
}
