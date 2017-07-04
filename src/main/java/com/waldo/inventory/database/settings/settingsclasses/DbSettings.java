package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.gui.Application;

public class DbSettings extends DbSettingsObject {

    private static final String TABLE_NAME = "dbsettings";

    private String dbFile = Application.startUpPath + "inventory.db";
    private String dbUserName = "waldo";
    private String dbUserPw = "";

    private int dbMaxIdleConnections = 50;
    private int dbMaxActiveConnections = -1; // No limit
    private int dbInitialSize = 5;
    private int dbRemoveAbandonedTimeout = 60;

    private boolean dbPoolPreparedStatements = true;
    private boolean dbLogAbandoned = false;
    private boolean dbRemoveAbandoned = true;



    public DbSettings() {
        super(TABLE_NAME);
    }


    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof DbSettings) {
                DbSettings ref = (DbSettings) obj;
                if ((ref.getDbFile().equals(getDbFile())) &&
                        (ref.getDbUserName().equals(getDbUserName())) &&
                        (ref.getDbUserPw().equals(getDbUserPw())) &&
                        (ref.getDbMaxIdleConnections() == (getDbMaxIdleConnections())) &&
                        (ref.getDbMaxActiveConnections() == (getDbMaxActiveConnections())) &&
                        (ref.getDbInitialSize() == (getDbInitialSize())) &&
                        (ref.getDbRemoveAbandonedTimeout() == (getDbRemoveAbandonedTimeout())) &&
                        (ref.isDbPoolPreparedStatements() == isDbPoolPreparedStatements()) &&
                        (ref.isDbLogAbandoned() == isDbLogAbandoned()) &&
                        (ref.isDbRemoveAbandoned() == isDbRemoveAbandoned())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public DbSettings createCopy() {
        DbSettings copy = new DbSettings();
        copyBaseFields(copy);
        copy.setDbFile(dbFile);
        copy.setDbUserName(dbUserName);
        copy.setDbUserPw(dbUserPw);
        copy.setDbMaxIdleConnections(dbMaxIdleConnections);
        copy.setDbMaxActiveConnections(dbMaxActiveConnections);
        copy.setDbInitialSize(dbInitialSize);
        copy.setDbRemoveAbandonedTimeout(dbRemoveAbandonedTimeout);
        copy.setDbPoolPreparedStatements(dbPoolPreparedStatements);
        copy.setDbLogAbandoned(dbLogAbandoned);
        copy.setDbRemoveAbandoned(dbRemoveAbandoned);
        return copy;
    }


    public String getDbFile() {
        if (dbFile == null) {
            dbFile = "";
        }
        return dbFile;
    }

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }

    public String getDbUserName() {
        if (dbUserName == null) {
            dbUserName = "";
        }
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbUserPw() {
        if (dbUserPw == null) {
            dbUserPw = "";
        }
        return dbUserPw;
    }

    public void setDbUserPw(String dbUserPw) {
        this.dbUserPw = dbUserPw;
    }

    public int getDbMaxIdleConnections() {
        return dbMaxIdleConnections;
    }

    public void setDbMaxIdleConnections(int dbMaxIdleConnections) {
        this.dbMaxIdleConnections = dbMaxIdleConnections;
    }

    public int getDbMaxActiveConnections() {
        return dbMaxActiveConnections;
    }

    public void setDbMaxActiveConnections(int dbMaxActiveConnections) {
        this.dbMaxActiveConnections = dbMaxActiveConnections;
    }

    public int getDbInitialSize() {
        return dbInitialSize;
    }

    public void setDbInitialSize(int dbInitialSize) {
        this.dbInitialSize = dbInitialSize;
    }

    public int getDbRemoveAbandonedTimeout() {
        return dbRemoveAbandonedTimeout;
    }

    public void setDbRemoveAbandonedTimeout(int dbRemoveAbandonedTimeout) {
        this.dbRemoveAbandonedTimeout = dbRemoveAbandonedTimeout;
    }

    public boolean isDbPoolPreparedStatements() {
        return dbPoolPreparedStatements;
    }

    public void setDbPoolPreparedStatements(boolean dbPoolPreparedStatements) {
        this.dbPoolPreparedStatements = dbPoolPreparedStatements;
    }

    public boolean isDbLogAbandoned() {
        return dbLogAbandoned;
    }

    public void setDbLogAbandoned(boolean dbLogAbandoned) {
        this.dbLogAbandoned = dbLogAbandoned;
    }

    public boolean isDbRemoveAbandoned() {
        return dbRemoveAbandoned;
    }

    public void setDbRemoveAbandoned(boolean dbRemoveAbandoned) {
        this.dbRemoveAbandoned = dbRemoveAbandoned;
    }
}
