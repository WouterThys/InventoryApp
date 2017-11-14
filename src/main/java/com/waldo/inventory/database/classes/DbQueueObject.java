package com.waldo.inventory.database.classes;

import com.waldo.inventory.classes.dbclasses.DbObject;

public class DbQueueObject {

    private int how;
    private DbObject object;

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
}
