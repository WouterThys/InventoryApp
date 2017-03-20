package com.waldo.inventory.database;

import com.waldo.inventory.classes.DbObject;

import java.sql.SQLException;

public interface TableChangedListener {

    void tableChangedListener(String tableName, DbObject obj) throws SQLException;

}
