package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.DbObject;

public interface DbErrorListener {
    void onSelectError(final DbObject object, final Throwable throwable, final String sql);
    void onInsertError(final DbObject object, final Throwable throwable, final String sql);
    void onUpdateError(final DbObject object, final Throwable throwable, final String sql);
    void onDeleteError(final DbObject object, final Throwable throwable, final String sql);
}
