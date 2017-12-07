package com.waldo.inventory.database.classes;

import com.waldo.inventory.classes.dbclasses.DbObject;

public class DbQueueObject {

    private int how;
    private DbObject object;

    // Extras
    private long insertTime;
    private long removeTime;

    public DbQueueObject(DbObject object, int how) {
        this.object = object;
        this.how = how;
    }

    public int getHow() {
        return how;
    }

    public DbObject getObject() {
        return object;
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
