package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.DbObject;

import java.sql.SQLException;

public interface TableChangedListener {
    void onTableChanged(String tableName, int changedHow, DbObject newObject, DbObject oldObject) throws SQLException;
}
