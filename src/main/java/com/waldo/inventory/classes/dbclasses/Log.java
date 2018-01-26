package com.waldo.inventory.classes.dbclasses;


import com.waldo.inventory.Utils.Statics.LogTypes;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.utils.DateUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Log extends DbObject {

    public static final String TABLE_NAME = "logs";

    private LogTypes logType;
    private Date logTime;
    private String logClass;
    private String logMessage;
    private String logException;

    public Log() {
        this(LogTypes.Info, "Log", "", "");
    }

    public Log(String logMessage) {
        this(LogTypes.Info, "Log", logMessage, "");
    }

    public Log(LogTypes logType, String logClass, String logMessage) {
        this(logType, logClass, logMessage, "");
    }

    public Log(LogTypes logType, String logClass, String logMessage, Throwable logException) {
        this(logType, logClass, logMessage, ExceptionUtils.getStackTrace(logException));
    }

    public Log(LogTypes logType, String logClass, String logMessage, String logException) {
        this(logType, DateUtils.now(), logClass, logMessage, logException);
    }

    public Log(LogTypes logType, Date logTime, String logClass, String logMessage, String logException) {
        super(TABLE_NAME);
        this.logType = logType;
        this.logTime = logTime;
        this.logClass = logClass;
        this.logMessage = logMessage;
        this.logException = logException;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (logType) {
            case Info: builder.append(" - INFO "); break;
            case Debug: builder.append(" - DEBUG "); break;
            case Warn: builder.append(" - WARNING "); break;
            case Error: builder.append(" - ERROR "); break;
        }

        builder.append("(").append(getLogClass()).append(") ");
        builder.append(" - ").append(logMessage);
        if (!logException.isEmpty()) {
            builder.append("EXCEPTION: \r\n").append(logException);
        }

        return builder.toString();
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;
        statement.setInt(ndx++, getLogType().getValue());
        statement.setTimestamp(ndx++, new Timestamp(getLogTime().getTime()));
        statement.setString(ndx++, getLogClass());
        statement.setString(ndx++, getLogMessage());
        statement.setString(ndx++, getLogException());
        return ndx;
    }

    @Override
    public DbObject createCopy(DbObject copyInto) {
        return null;
    }

    @Override
    public DbObject createCopy() {
        return null;
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<Log> list = cache().getLogs();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<Log> list = cache().getLogs();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }


    public LogTypes getLogType() {
        return logType;
    }

    public void setLogType(LogTypes logType) {
        this.logType = logType;
    }

    public void setLogType(int logType) {
        this.logType = LogTypes.fromInt(logType);
    }

    public Date getLogTime() {
        if (logTime == null) {
            logTime = new Date(-10);
        }
        return logTime;
    }

    public void setLogTime(Timestamp logTime) {
        if (logTime != null) {
            this.logTime = new Date(logTime.getTime());
        } else {
            this.logTime = new Date(0);
        }
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getLogClass() {
        if (logClass == null) {
            logClass = "";
        }
        return logClass;
    }

    public void setLogClass(String logClass) {
        this.logClass = logClass;
    }

    public String getLogMessage() {
        if (logMessage == null) {
            logMessage = "";
        }
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getLogException() {
        if (logMessage == null) {
            logMessage = "";
        }
        return logException;
    }

    public void setLogException(String logException) {
        this.logException = logException;
    }

    public static class LogComparator implements Comparator<Log> {

        @Override
        public int compare(Log o1, Log o2) {
            return o2.getLogTime().compareTo(o1.getLogTime());
        }
    }
}
