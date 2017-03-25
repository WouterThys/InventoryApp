package com.waldo.inventory.classes;

import com.waldo.inventory.database.*;

import java.sql.*;

public abstract class DbObject {

    public static String TABLE_NAME;

    public static final int UNKNOWN = 1;

    protected long id = -1;
    protected String name = "";
    protected String iconPath = "";

    private TableChangedListener onTableChangedListener;

    private String sqlInsert = "INSERT INTO " + TABLE_NAME + " (name, iconpath) VALUES (?, ?)";
    private String sqlUpdate = "UPDATE " + TABLE_NAME + " SET name = ?, iconpath = ? WHERE id = ?";
    private String sqlDelete = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.execute();
    }

    protected void update(PreparedStatement statement) throws SQLException {
        statement.setString(1, name); // Set (name)
        statement.setString(2, iconPath); // Set (icon path)
        statement.setLong(3, id); // Where id
        statement.execute();
    }

    protected DbObject(String tableName) {
        TABLE_NAME = tableName;
    }

    protected DbObject(String tableName, String sqlInsert, String sqlUpdate, String sqlDelete) {
        this(tableName);
        this.sqlUpdate = sqlUpdate;
        this.sqlDelete = sqlDelete;
        this.sqlInsert = sqlInsert;
    }

    protected DbObject(String tableName, String sqlInsert, String sqlUpdate) {
        this(tableName, sqlInsert, sqlUpdate, "DELETE FROM " + TABLE_NAME + " WHERE id = ?");
    }

    public void save() throws SQLException {
        try (Connection connection = DbManager.getConnection()) {
            if (id == -1) { // Save
                try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    insert(statement);

                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getLong(1);
                    }
                    setOnTableChangedListener(DbManager.dbInstance());
                    if (onTableChangedListener != null) {
                        onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_ADDED, this);
                    }
                }
            } else { // Update
                try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
                    update(statement);
                    if (onTableChangedListener != null) {
                        onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_UPDATED, this);
                    }
                }
            }
        }
    }

    public void delete() throws SQLException {
        if (id != -1) {
            try (Connection connection = DbManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sqlDelete)) {
                statement.setLong(1, id);
                statement.execute();
                id = -1; // Not in database anymore
            }

            if (onTableChangedListener != null) {
                onTableChangedListener.onTableChanged(TABLE_NAME, DbManager.OBJECT_DELETED, this);
            }
        }
    }

    @Override
    public String toString() {
        if (name == null) {
            return "(No name)";
        }
        if (id == -1) {
            return name + "*";
        }
        return name;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconPath() {
        if (iconPath != null) {
            return iconPath;
        }
        return "";
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setOnTableChangedListener(TableChangedListener tableChangedListenerListener) {
        this.onTableChangedListener = tableChangedListenerListener;
    }
}
