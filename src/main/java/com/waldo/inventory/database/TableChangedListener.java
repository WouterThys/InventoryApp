package com.waldo.inventory.database;

import java.sql.SQLException;

public interface TableChangedListener {

    void tableChangedListener(String tableName, long id) throws SQLException;

}
