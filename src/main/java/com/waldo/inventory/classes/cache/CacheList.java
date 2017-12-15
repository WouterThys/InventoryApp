package com.waldo.inventory.classes.cache;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.CacheChangedListener;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.Date;
import java.util.List;

public class CacheList<T extends DbObject> extends ArrayList<T> {

    private List<CacheChangedListener<T>> changedListeners = new ArrayList<>();
    private boolean isFetched = false;
    // Extra info
    private Date initialisationTime = null;
    private long fetchTimeInNanos;

    public CacheList() {
        super();
        isFetched = false;
    }

    public CacheList(@NotNull Collection<? extends T> collection) {
        super(collection);

        initialisationTime = DateUtils.now();
    }

    public CacheList(@NotNull Collection<? extends T> collection, long fetchTimeInNanos) {
        super(collection);

        this.initialisationTime = DateUtils.now();
        this.fetchTimeInNanos = fetchTimeInNanos;
    }

    public void setList(@NotNull Collection<? extends T> collection, long fetchTimeInNanos) {
        if (isFetched) {
            this.clear();
        }
        this.addAll(collection);
        this.initialisationTime = DateUtils.now();
        this.fetchTimeInNanos = fetchTimeInNanos;
        this.isFetched = true;
    }

    @Override
    public void clear() {
        super.clear();
        isFetched = false;
    }

    public List<CacheChangedListener<T>> getChangedListeners() {
        return changedListeners;
    }

    public void addChangedListener(CacheChangedListener<T> listener) {
        if (listener != null && !changedListeners.contains(listener)) {
            changedListeners.add(listener);
        }
    }

    public void removeChangedListener(CacheChangedListener<T> listener) {
        if (listener != null) {
            changedListeners.remove(listener);
        }
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
