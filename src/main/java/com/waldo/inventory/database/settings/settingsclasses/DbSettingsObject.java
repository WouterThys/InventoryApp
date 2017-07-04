package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.database.settings.SettingsManager;

public abstract class DbSettingsObject extends DbObject {

    public static final int SETTINGS_TYPE_LOG = 200;
    public static final int SETTINGS_TYPE_FILE = 201;
    public static final int SETTINGS_TYPE_DB = 202;

    private boolean isSaved = false;

    protected DbSettingsObject(String tableName) {
        super(tableName);
    }

    public boolean isDefault() {
        return name.equals(SettingsManager.DEFAULT);
    }


    @Override
    public String toString() {
        return name;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public static int getType(DbSettingsObject object) {
        if (object instanceof LogSettings) return SETTINGS_TYPE_LOG;
        if (object instanceof DbSettings) return SETTINGS_TYPE_DB;
        if (object instanceof FileSettings) return SETTINGS_TYPE_FILE;

        return TYPE_UNKNOWN;
    }

    /*
     * Mask methods of DbObject
     */
    @Override
    protected void doSave() {}
    @Override
    public void save() {}
    @Override
    public void saveSynchronously() {}
    @Override
    protected void doDelete() {}
    @Override
    public void delete() {}

}
