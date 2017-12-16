package com.waldo.inventory.classes;

import com.waldo.inventory.classes.cache.CacheList;

public class CacheLog {

    private String listName;
    private CacheList cacheList;

    public CacheLog(String listName, CacheList cacheList) {
        this.listName = listName;
        this.cacheList = cacheList;
    }

    public String getListName() {
        if (listName == null) {
            listName = "";
        }
        return listName;
    }

    public CacheList getCacheList() {
        return cacheList;
    }

    public int getCacheListSize() {
        if (cacheList.isFetched()) {
            return cacheList.size();
        }
        return -1;
    }
}
