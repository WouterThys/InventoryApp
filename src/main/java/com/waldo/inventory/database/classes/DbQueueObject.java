package com.waldo.inventory.database.classes;

import com.waldo.inventory.Utils.Statics.QueryType;
import com.waldo.inventory.classes.dbclasses.DbObject;

public class DbQueueObject {

    private final QueryType queryType;
    private final DbObject object;
    private final String sql;

    // Extras
    private long insertTime;
    private long removeTime;

    public DbQueueObject(DbObject object, QueryType queryType) {
        this.object = object;
        if (queryType == null) {
            this.queryType = QueryType.Unknown;
        } else {
            this.queryType = queryType;
        }
        this.sql = "";
    }

    public DbQueueObject(String sql) {
        this.object = null;
        this.queryType = QueryType.Custom;
        this.sql = sql;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public DbObject getObject() {
        return object;
    }

    public String getSql() {
        return sql;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public void setRemoveTime(long removeTime) {
        this.removeTime = removeTime;
    }

    public long getTimeInQueue() {
        return removeTime - insertTime;
    }
}
