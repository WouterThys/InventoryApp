package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Product extends DbObject {

    public static final String TABLE_NAME = "products";
    private static final String insertSql = "INSERT INTO "+TABLE_NAME+" (" +
            "name, categoryid) VALUES " +
            "(?, ?)";
    private static final String updateSql =
            "UPDATE "+TABLE_NAME+" " +
                    "SET name = ?, categoryid = ? " +
                    "WHERE id = ?;";
    private static final String deleteSql = "DELETE FROM items WHERE id = ?";

    private long categoryId;

    public Product() {
        super(TABLE_NAME, insertSql, updateSql, deleteSql);
    }

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setLong(2, categoryId);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setLong(2, categoryId);
        statement.setLong(3, id); // WHERE id
        statement.execute();
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
