package com.waldo.inventory.classes.cache;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.CacheChangedListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CacheList<T extends DbObject> extends ArrayList<T> {

    private List<CacheChangedListener<T>> changedListeners = new ArrayList<>();
    private Date initialisationTime = null;

    public CacheList(@NotNull Collection<? extends T> collection) {
        super(collection);

        initialisationTime = DateUtils.now();
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
}
