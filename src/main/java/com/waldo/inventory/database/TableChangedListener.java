package com.waldo.inventory.database;

import com.waldo.inventory.classes.DbObject;

import java.sql.SQLException;

public interface TableChangedListener {
    void onTableChanged(String tableName, int changedHow, DbObject obj) throws SQLException;
}
