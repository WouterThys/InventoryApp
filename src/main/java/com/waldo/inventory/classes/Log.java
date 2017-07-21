package com.waldo.inventory.classes;


import com.waldo.inventory.database.DbManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.waldo.inventory.Utils.Statics.LogTypes.*;

public class Log extends DbObject {

    public static final String TABLE_NAME = "log";

    private int logType;
    private Date logTime;
    private String logClass;
    private String logMessage;
    private String logException;

    public Log() {
        this(INFO, "Log", "", "");
    }

    public Log(String logMessage) {
        this(INFO, "Log", logMessage, "");
    }

    public Log(int logType, String logClass, String logMessage) {
        this(logType, logClass, logMessage, "");
    }

    public Log(int logType, String logClass, String logMessage, Throwable logException) {
        this(logType, logClass, logMessage, ExceptionUtils.getStackTrace(logException));
    }

    public Log(int logType, String logClass, String logMessage, String logException) {
        this(logType, new Date(Calendar.getInstance().getTime().getTime()), logClass, logMessage, logException);
    }

    public Log(int logType, Date logTime, String logClass, String logMessage, String logException) {
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
            case INFO: builder.append(" - INFO "); break;
            case DEBUG: builder.append(" - DEBUG "); break;
            case WARN: builder.append(" - WARNING "); break;
            case ERROR: builder.append(" - ERROR "); break;
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
        statement.setInt(1, logType);
        statement.setDate(2, logTime);
        statement.setString(3,logClass);
        statement.setString(4, logMessage);
        statement.setString(5, logException);
        return 6;
    }

    @Override
    public DbObject createCopy(DbObject copyInto) {
        return null;
    }

    @Override
    public DbObject createCopy() {
        return null;
    }

    @Override
    protected void doSave() throws SQLException {
        // TODO
//        setOnTableChangedListener(DbManager.db());
//
//        try (Connection connection = DbManager.getConnection()) {
//            if (!connection.isValid(5)) {
//                throw new SQLException("Conenction invalid, timed out after 5s...");
//            }
//            if (id == -1) { // Save
//                try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
//                    insert(statement);
//
//                    try (ResultSet rs = statement.getGeneratedKeys()) {
//                        rs.next();
//                        id = rs.getLong(1);
//                    }
//                }
//            } else { // Update
//                // Save new object
//                try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
//                    update(statement);
//                }
//            }
//        }
    }

    @Override
    public void save() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    doSave();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                // Ok
            }
        };
        worker.execute();
        try {
            worker.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveSynchronously() throws SQLException {
        // Don't do this
    }

    @Override
    protected void doDelete() throws SQLException {
//        if (id != -1) {
//            try (Connection connection = DbManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
//                statement.setLong(1, id);
//                statement.execute();
//                id = -1; // Not in database anymore
//            }
//        }
    }

    @Override
    public void delete() {
        if (canBeSaved) {
//            SwingWorker worker = new SwingWorker() {
//                @Override
//                protected Object doInBackground() throws Exception {
            try {
                doDelete();
            } catch (SQLException e) {
                e.printStackTrace();
            }
//                    return null;
//                }

//                @Override
//                protected void done() {
//                    try {
//                        get(10, TimeUnit.SECONDS);
//                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            worker.execute();
        }
    }

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public Date getLogTime() {
        if (logTime == null) {
            logTime = new Date(-10);
        }
        return logTime;
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
