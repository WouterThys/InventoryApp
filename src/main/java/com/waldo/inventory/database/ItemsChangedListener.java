package com.waldo.inventory.database;

import com.waldo.inventory.classes.Item;

public interface ItemsChangedListener {
    void onItemAdded(Item item);
    void onItemUpdated(Item item);
    void onItemDeleted(Item item);
}
