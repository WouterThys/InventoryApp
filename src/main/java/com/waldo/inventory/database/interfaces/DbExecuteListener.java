package com.waldo.inventory.database.interfaces;

public interface DbExecuteListener {
    void onExecuted(String sql);
    void onExecuteError(String sql, Throwable throwable);
}
