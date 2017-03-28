package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.Type;

public interface TypesChangedListener {
    void onTypeAdded(Type type);
    void onTypeUpdated(Type type);
    void onTypeDeleted(Type type);
}
