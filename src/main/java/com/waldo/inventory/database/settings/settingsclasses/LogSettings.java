package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.classes.dbclasses.DbObject;

public class LogSettings extends DbSettingsObject {

    private static final String TABLE_NAME = "logsettings";

    private boolean logInfo;
    private boolean logDebug;
    private boolean logWarn;
    private boolean logError;


    public LogSettings() {
        super(TABLE_NAME);
    }

    public LogSettings(String name) {
        this();
        setName(name);
    }

    @Override
    public void tableChanged(int changedHow) {
        //
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof LogSettings) {
                LogSettings ref = (LogSettings) obj;
                if ((ref.isLogInfo() == isLogInfo()) &&
                        (ref.isLogDebug() == isLogDebug()) &&
                        (ref.isLogWarn() == isLogWarn()) &&
                        (ref.isLogError() == isLogError())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public LogSettings createCopy(DbObject copyInto) {
        LogSettings copy = (LogSettings) copyInto;
        copyBaseFields(copy);
        copy.setLogInfo(logInfo);
        copy.setLogDebug(logDebug);
        copy.setLogWarn(logWarn);
        copy.setLogError(logError);
        return copy;
    }

    @Override
    public LogSettings createCopy() {
        return createCopy(new LogSettings());
    }

    public boolean isLogInfo() {
        return logInfo;
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

    public String getLogInfo() {
        return Boolean.toString(logInfo);
    }

    public String getLogDebug() {
        return Boolean.toString(logDebug);
    }

    public String getLogWarn() {
        return Boolean.toString(logWarn);
    }

    public String getLogError() {
        return Boolean.toString(logError);
    }
}
