package com.waldo.inventory.classes.dbclasses;


import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.database.DbManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.Utils.Statics.LogTypes.*;
import static com.waldo.inventory.database.DbManager.db;

public class Log extends DbObject {

    public static final String TABLE_NAME = "logs";

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
        this(logType, DateUtils.now(), logClass, logMessage, logException);
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
        int ndx = 1;
        statement.setInt(ndx++, logType);
        statement.setTimestamp(ndx++, new Timestamp(logTime.getTime()));
        statement.setString(ndx++,logClass);
        statement.setString(ndx++, logMessage);
        statement.setString(ndx++, logException);
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

//    @Override
//    protected void doSave() throws SQLException {
//        // TODO
////        setOnDbTableChangedListener(DbManager.db());
////
////        try (Connection connection = DbManager.getConnection()) {
////            if (!connection.isValid(5)) {
////                throw new SQLException("Conenction invalid, timed out after 5s...");
////            }
////            if (id == -1) { // Save
////                try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
////                    insert(statement);
////
////                    try (ResultSet rs = statement.getGeneratedKeys()) {
////                        rs.next();
////                        id = rs.getLong(1);
////                    }
////                }
////            } else { // Update
////                // Save new object
////                try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
////                    update(statement);
////                }
////            }
////        }
//    }

//    @Override
//    public void save() {
//        SwingWorker worker = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {
//                try {
//                    doSave();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                // Ok
//            }
//        };
//        worker.execute();
//        try {
//            worker.get(10, TimeUnit.SECONDS);
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//            e.printStackTrace();
//        }
//    }


//    @Override
//    protected void doDelete() throws SQLException {
////        if (id != -1) {
////            try (Connection connection = DbManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
////                statement.setLong(1, id);
////                statement.execute();
////                id = -1; // Not in database anymore
////            }
////        }
//    }

//    @Override
//    public void delete() {
//        if (canBeSaved) {
////            SwingWorker worker = new SwingWorker() {
////                @Override
////                protected Object doInBackground() throws Exception {
//            try {
//                doDelete();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
////                    return null;
////                }
//
////                @Override
////                protected void done() {
////                    try {
////                        get(10, TimeUnit.SECONDS);
////                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
////                        e.printStackTrace();
////                    }
////                }
////            };
////            worker.execute();
//        }
//    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Log> list = db().getLogs();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Log> list = db().getLogs();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
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
