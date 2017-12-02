package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.dbclasses.DbObject;

public interface CacheChangedListener<T extends DbObject> {
    void onInserted(T object);
    void onUpdated(T object);
    void onDeleted(T object);
    void onCacheCleared();
}
