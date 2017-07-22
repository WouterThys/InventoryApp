package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.DbObject;

public interface DbObjectChangedListener<T extends DbObject> {
    void onInserted(T object);
    void onUpdated(T object);
    void onDeleted(T object);
}
