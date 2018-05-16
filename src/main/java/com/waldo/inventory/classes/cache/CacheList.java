package com.waldo.inventory.classes.cache;

import com.waldo.utils.DateUtils;

import java.sql.Date;
import java.util.Collection;
import java.util.Vector;

public class CacheList<T> extends Vector<T> {

    private boolean isFetched;

    // Extra info
    private Date initialisationTime = null;
    private long fetchTimeInNanos;

    public CacheList() {
        super();
        this.isFetched = false;
    }

    public void setList(Collection<? extends T> collection, long fetchTimeInNanos) {
        if (size() > 0) {
            this.clear();
        }
        if (collection != null) {
            this.addAll(collection);
            this.initialisationTime = DateUtils.now();
            this.fetchTimeInNanos = fetchTimeInNanos;
            this.isFetched = true;
        } else {
            this.clear();
            this.isFetched = false;
        }
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
