package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.database.DbManager;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class DbHistory extends DbObject {

    public static final String TABLE_NAME = "dbhistory";

    // Variables
    private Date date;
    private int dbAction; // Add / edit / delete
    private int dbObjectType;
    private long dbObjectId;

    public DbHistory() {
        super(TABLE_NAME);
    }

    public DbHistory(int dbAction, DbObject dbObject) {
        this();
        this.dbAction = dbAction;
        this.dbObjectType = DbObject.getType(dbObject);
        this.dbObjectId = dbObject.getId();
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        date = DateUtils.now();

        statement.setTimestamp(ndx++, new Timestamp(date.getTime()));
        statement.setInt(ndx++, getDbAction());
        statement.setInt(ndx++, getDbObjectType());
        statement.setLong(ndx++, getDbObjectId());

        return ndx;
    }

    @Override
    public DbHistory createCopy(DbObject copyInto) {
        DbHistory cpy = (DbHistory) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setDate(getDate());
        cpy.setDbAction(getDbAction());
        cpy.setDbObjectType(getDbObjectType());
        cpy.setDbObjectId(getDbObjectId());

        return cpy;
    }

    @Override
    public DbHistory createCopy() {
        return createCopy(new DbHistory());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<DbHistory> list = DbManager.db().getDbHistory();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {

                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<DbHistory> list = DbManager.db().getDbHistory();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public static DbHistory getUnknownDbHistory() {
        DbHistory u = new DbHistory();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    // Getters and setters

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(Timestamp date) {
        if (date != null) {
            this.date = new Date(date.getTime());
        }
    }

    public int getDbAction() {
        return dbAction;
    }

    public void setDbAction(int dbAction) {
        this.dbAction = dbAction;
    }

    public int getDbObjectType() {
        return dbObjectType;
    }

    public void setDbObjectType(int dbObjectType) {
        this.dbObjectType = dbObjectType;
    }

    public long getDbObjectId() {
        return dbObjectId;
    }

    public void setDbObjectId(long dbObjectId) {
        this.dbObjectId = dbObjectId;
    }
}