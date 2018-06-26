package com.waldo.inventory.database.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;

public class DbErrorObject extends DbQueueObject {

    private final Throwable exception;
    private final String sql;

    public DbErrorObject(DbObject object, Throwable exception, Statics.QueryType how, String sql) {
        super(object, how);
        this.exception = exception;
        this.sql = sql;
    }

    public DbErrorObject(Throwable exception, String sql) {
        super(sql);
        this.exception = exception;
        this.sql = sql;
    }

    @Override
    public String toString() {
        return "DbErrorObject{" +
                "exception=" + exception +
                ", sql='" + sql + '\'' +
                '}';
    }

    public Throwable getException() {
        return exception;
    }

    public String getSql() {
        return sql;
    }

}
