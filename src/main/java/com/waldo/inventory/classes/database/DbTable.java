package com.waldo.inventory.classes.database;

import com.waldo.inventory.managers.TableManager;

import java.util.ArrayList;
import java.util.List;

public class DbTable {

    private String tableName;
    private List<DbForeignKey> dbForeignKeys;

    public DbTable(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return getTableName();
    }

    public void addForeignKey(DbForeignKey key) {
        if (dbForeignKeys == null) {
            dbForeignKeys = new ArrayList<>();
        }

        if (!getDbForeignKeys().contains(key)) {
            dbForeignKeys.add(key);
        }
    }

    public DbForeignKey findForeignKey(String keyName) {
        for (DbForeignKey fk : getDbForeignKeys()) {
            if (fk.getConstraintName().equals(keyName)) {
                return fk;
            }
        }
        return null;
    }

    private List<DbForeignKey> getDbForeignKeys() {
        if (dbForeignKeys == null) {
            TableManager.dbTm().loadTableData(this);
        }
        return dbForeignKeys;
    }


    public String getTableName() {
        if (tableName == null) {
            tableName = "";
        }
        return tableName;
    }
}
