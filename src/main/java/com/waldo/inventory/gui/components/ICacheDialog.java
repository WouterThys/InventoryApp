package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.CacheChangedListener;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public abstract class ICacheDialog extends iDialog {

    private final List<CacheChangedListener> cacheListenerList = new ArrayList<>();

    public ICacheDialog(Window parent) {
        super(parent);
    }

    public ICacheDialog(Window parent, String title) {
        super(parent, title);
    }

    public <T extends DbObject> void addCacheListener(Class<T> c, CacheChangedListener<T> listener) {
        if (!cacheListenerList.contains(listener)) {
            cacheListenerList.add(listener);
        }
        cache().addListener(c, listener);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        for (CacheChangedListener listener : cacheListenerList) {
            cache().removeListener(listener);
        }
        super.windowClosed(e);
    }
}
