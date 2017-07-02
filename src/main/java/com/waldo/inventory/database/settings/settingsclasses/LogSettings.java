package com.waldo.inventory.database.settings.settingsclasses;

public class LogSettings extends DbSettingsObject {

    private boolean logInfo;
    private boolean logDebug;
    private boolean logWarn;
    private boolean logError;

    public boolean isLogInfo() {
        return logInfo;
    }

    @Override
    public DbSettingsObject creatCopy(DbSettingsObject original) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    public void setLogInfo(boolean logInfo) {
        this.logInfo = logInfo;
    }

    public void setLogInfo(String logInfo) {
        try {
            this.logInfo = Boolean.parseBoolean(logInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLogDebug() {
        return logDebug;
    }

    public void setLogDebug(boolean logDebug) {
        this.logDebug = logDebug;
    }

    public void setLogDebug(String logDebug) {
        try {
            this.logDebug = Boolean.parseBoolean(logDebug);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLogWarn() {
        return logWarn;
    }

    public void setLogWarn(boolean logWarn) {
        this.logWarn = logWarn;
    }

    public void setLogWarn(String logWarn) {
        try {
            this.logWarn = Boolean.parseBoolean(logWarn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLogError() {
        return logError;
    }

    public void setLogError(boolean logError) {
        this.logError = logError;
    }

    public void setLogError(String logError) {
        try {
            this.logError = Boolean.parseBoolean(logError);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
