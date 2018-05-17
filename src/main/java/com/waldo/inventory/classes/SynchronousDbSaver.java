package com.waldo.inventory.classes;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.interfaces.CacheChangedListener;

import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class SynchronousDbSaver<T extends DbObject> implements CacheChangedListener<T> {

    public interface SynchronousSaveListener<U extends DbObject> {
        void beforeSave(U objectToSave);
        void saved(U saveObject);
        void done();
    }

    private final List<T> objectList;
    private SynchronousSaveListener<T> saveListener;
    private int savingIndex;

    public SynchronousDbSaver(Class<T> dbClass, List<T> objectList) {
        this(dbClass, objectList, null);
    }

    public SynchronousDbSaver(Class<T> dbClass, List<T> objectList, SynchronousSaveListener<T> saveListener) {
        this.objectList = objectList;
        this.saveListener = saveListener;
        cache().addListener(dbClass, this);
    }

    public void addSaveListener(SynchronousSaveListener<T> saveListener) {
        this.saveListener = saveListener;
    }

    public void startSaving() {
        savingIndex = -1;
        saveNext();
    }

    private void saveNext() {
        savingIndex++;
        if (savingIndex < objectList.size()) {
            T t = objectList.get(savingIndex);
            if (t != null && t.canBeSaved()) {
                if (saveListener != null) {
                    saveListener.beforeSave(t);
                }
                t.save();
            } else {
                saveNext();
            }
        } else {
            if (saveListener != null) {
                saveListener.done();
            }
            cache().removeListener(this);
        }
    }

    @Override
    public void onInserted(T object) {
        if (saveListener != null) {
            saveListener.saved(object);
        }
        saveNext();
    }

    @Override
    public void onUpdated(T object) {
        if (saveListener != null) {
            saveListener.saved(object);
        }
        saveNext();
    }

    @Override
    public void onDeleted(T object) {

    }

    @Override
    public void onCacheCleared() {

    }
}
