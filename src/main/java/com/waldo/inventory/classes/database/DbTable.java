package com.waldo.inventory.classes.database;

import com.waldo.inventory.managers.TableManager;

import java.util.ArrayList;
import java.util.List;

public class DbTable {

    private String tableName;

    private List<ForeignKey> foreignKeys;


    public DbTable(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return getTableName();
    }

    public void addForeignKey(ForeignKey key) {
        if (foreignKeys == null) {
            foreignKeys = new ArrayList<>();
        }

        if (!getForeignKeys().contains(key)) {
            foreignKeys.add(key);
        }
    }

    public ForeignKey findForeignKey(String keyName) {
        for (ForeignKey fk : getForeignKeys()) {
            if (fk.getConstraintName().equals(keyName)) {
                return fk;
            }
        }
        return null;
    }

    private List<ForeignKey> getForeignKeys() {
        if (foreignKeys == null) {
            TableManager.dbTm().loadTableData(this);
        }
        return foreignKeys;
    }


    public String getTableName() {
        if (tableName == null) {
            tableName = "";
        }
        return tableName;
    }
}
