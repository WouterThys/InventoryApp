package com.waldo.inventory.database;

public interface TableChangedListener {

    void tableChangedListener(String tableName, long id);

}
