package com.waldo.inventory.classes;

import com.waldo.inventory.classes.cache.CacheList;

public abstract class CacheLog {

    private String listName;

    public CacheLog(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        if (listName == null) {
            listName = "";
        }
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public abstract CacheList getCacheList();

    public int getCacheListSize() {
        int size = -1;
        if (getCacheList() != null) {
            size = getCacheList().size();
        }
        return size;
    }
}
