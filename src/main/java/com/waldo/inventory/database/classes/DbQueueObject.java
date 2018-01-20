package com.waldo.inventory.database.classes;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.DatabaseAccess;

public class DbQueueObject {

    private final int how;
    private final DbObject object;
    private final String sql;

    // Extras
    private long insertTime;
    private long removeTime;

    public DbQueueObject(DbObject object, int how) {
        this.object = object;
        this.how = how;
        this.sql = "";
    }

    public DbQueueObject(String sql) {
        this.object = null;
        this.how = DatabaseAccess.EXECUTE_SQL;
        this.sql = sql;
    }

    public int getHow() {
        return how;
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
