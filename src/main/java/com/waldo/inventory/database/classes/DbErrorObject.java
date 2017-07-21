package com.waldo.inventory.database.classes;

import com.waldo.inventory.classes.DbObject;

public class DbErrorObject extends  DbQueueObject {

    private Throwable exception;
    private String sql;

    public DbErrorObject(DbObject object, Throwable exception, int how, String sql) {
        super(object, how);
        this.exception = exception;
        this.sql = sql;
    }

    public Throwable getException() {
        return exception;
    }

    public String getSql() {
        return sql;
    }

}
