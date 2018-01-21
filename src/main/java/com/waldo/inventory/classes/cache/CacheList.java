package com.waldo.inventory.classes.cache;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.DateUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

public class CacheList<T> extends ArrayList<T> {

    private boolean isFetched = false;

    // Extra info
    private Date initialisationTime = null;
    private long fetchTimeInNanos;

    public CacheList() {
        super();
        this.isFetched = false;
    }

    public void setList(@NotNull Collection<? extends T> collection, long fetchTimeInNanos) {
        if (size() > 0) {
            this.clear();
        }
        this.addAll(collection);
        this.initialisationTime = DateUtils.now();
        this.fetchTimeInNanos = fetchTimeInNanos;
        this.isFetched = true;
    }

    @Override
    public void clear() {
//        if (size() > 0 && T. instanceof DbObject) {
//            CacheManager.cache().notifyListeners(DatabaseAccess.OBJECT_CACHE_CLEAR, get(0));
//        }
        isFetched = false;
        super.clear();
    }

    public Date getInitialisationTime() {
        return initialisationTime;
    }

    public long getFetchTimeInNanos() {
        return fetchTimeInNanos;
    }

    public boolean isFetched() {
        return isFetched;
    }
}
