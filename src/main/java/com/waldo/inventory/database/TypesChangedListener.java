package com.waldo.inventory.database;

import com.waldo.inventory.classes.Type;

public interface TypesChangedListener {
    void onTypeAdded(Type type);
    void onTypeUpdated(Type type);
    void onTypeDeleted(Type type);
}
