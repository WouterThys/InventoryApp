package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.database.settings.SettingsManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DbSettingsObject extends DbObject {

    public static final int SETTINGS_TYPE_LOG = 200;
    public static final int SETTINGS_TYPE_FILE = 201;
    public static final int SETTINGS_TYPE_DB = 202;
    public static final int SETTINGS_TYPE_GENERAL = 203;

    private boolean isSaved = false;

    protected DbSettingsObject(String tableName) {
        super(tableName);
    }

    public boolean isDefault() {
        return name.equals(SettingsManager.DEFAULT);
    }

    @Override
    public int addParameters(PreparedStatement stmt) throws SQLException {
        return -2;
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
        if (object instanceof GeneralSettings) return SETTINGS_TYPE_GENERAL;

        return TYPE_UNKNOWN;
    }

    /*
     * Mask methods of DbObject
     */
    @Override
    public void save() {}
    @Override
    public void delete() {}

}
