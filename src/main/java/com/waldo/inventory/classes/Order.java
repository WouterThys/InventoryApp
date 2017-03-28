package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Order extends DbObject {

    public static final String TABLE_NAME = "orders";
    private static final String insertSql = "INSERT INTO "+TABLE_NAME+" (" +
            "name, iconpath) VALUES " +
            "(?, ?)";
    private static final String updateSql =
            "UPDATE "+TABLE_NAME+" " +
                    "SET name = ?, iconpath = ? " +
                    "WHERE id = ?;";

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, id); // WHERE id
        statement.execute();
    }

    public Order() {
        super(TABLE_NAME, insertSql, updateSql);
    }

    public Order(String tableName, String sqlInsert, String sqlUpdate) {
        super(tableName, sqlInsert, sqlUpdate);
    }
}
