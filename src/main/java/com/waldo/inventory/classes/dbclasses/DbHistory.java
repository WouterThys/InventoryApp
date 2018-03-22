package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics.QueryType;
import com.waldo.inventory.managers.CacheManager;
import com.waldo.utils.DateUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class DbHistory extends DbObject {

    public static final String TABLE_NAME = "dbhistory";

    // Variables
    private Date date;
    private QueryType dbQueryType;
    private int dbObjectType;
    private long dbObjectId;

    public DbHistory() {
        super(TABLE_NAME);
    }

    public DbHistory(QueryType dbQueryType, DbObject dbObject) {
        this();
        this.dbQueryType = dbQueryType;
        this.dbObjectType = DbObject.getType(dbObject);
        this.dbObjectId = dbObject.getId();
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        date = DateUtils.now();

        statement.setTimestamp(ndx++, new Timestamp(date.getTime()));
        statement.setInt(ndx++, getDbQueryType().getValue());
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
        cpy.setDbQueryType(getDbQueryType());
        cpy.setDbObjectType(getDbObjectType());
        cpy.setDbObjectId(getDbObjectId());

        return cpy;
    }

    @Override
    public DbHistory createCopy() {
        return createCopy(new DbHistory());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                List<DbHistory> list = CacheManager.cache().getDbHistory();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case Update: {

                break;
            }
            case Delete: {
                List<DbHistory> list = CacheManager.cache().getDbHistory();
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

    public QueryType getDbQueryType() {
        if (dbQueryType == null) {
            dbQueryType = QueryType.Unknown;
        }
        return dbQueryType;
    }

    public void setDbQueryType(QueryType dbQueryType) {
        this.dbQueryType = dbQueryType;
    }

    public void setDbQueryType(int dbQueryType) {
        this.dbQueryType = QueryType.fromInt(dbQueryType);
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