package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;

public class DbSettings extends DbSettingsObject {

    private static final String TABLE_NAME = "dbsettings";

    private String dbIp = "";
    private String dbName = "";
    private String dbUserName = "waldo";
    private String dbUserPw = "";
    private Statics.DbTypes dbType; // Online, Local, ...

    public DbSettings() {
        super(TABLE_NAME);
    }

    public DbSettings(String name) {
        this();
        setName(name);
    }


    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof DbSettings) {
                DbSettings ref = (DbSettings) obj;
                if ((ref.getDbIp().equals(getDbIp())) &&
                        (ref.getDbUserName().equals(getDbUserName())) &&
                        (ref.getDbUserPw().equals(getDbUserPw())) &&
                        (ref.getDbName().equals(getDbName())) &&
                        (ref.getDbType().equals(getDbType())) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public DbSettings createCopy(DbObject copyInto) {
        DbSettings copy = (DbSettings) copyInto;
        copyBaseFields(copy);
        copy.setDbIp(getDbIp());
        copy.setDbName(getDbName());
        copy.setDbUserName(getDbUserName());
        copy.setDbUserPw(getDbUserPw());
        copy.setDbType(getDbType());
        return copy;
    }

    @Override
    public DbSettings createCopy() {
        return createCopy(new DbSettings());
    }

    @Override
    public void tableChanged(int changedHow) {

    }

    public String createMySqlUrl() {
        return "jdbc:mysql:" +
                "//" + getDbIp() + "/" +
                getDbName();
    }

    public static String createMysqlUrl(String ip, String dbName) {
        return "jdbc:mysql:" +
                "//" + ip + "/" +
                dbName;
    }


    public String getDbIp() {
        if (dbIp == null) {
            dbIp = "";
        }
        return dbIp;
    }

    public void setDbIp(String dbIp) {
        this.dbIp = dbIp;
    }

    public String getDbName() {
        if (dbName == null) {
            dbName = "";
        }
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
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

    public Statics.DbTypes getDbType() {
        if (dbType == null) {
            dbType = Statics.DbTypes.Unknown;
        }
        return dbType;
    }

    public void setDbType(Statics.DbTypes dbType) {
        this.dbType = dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = Statics.DbTypes.fromString(dbType);
    }

}
