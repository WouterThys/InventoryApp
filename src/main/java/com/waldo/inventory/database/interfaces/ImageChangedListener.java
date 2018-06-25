package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.dbclasses.DbImage;

public interface ImageChangedListener {
    void onInserted(DbImage image);
    void onUpdated(DbImage image);
    void onDeleted(DbImage image);
}