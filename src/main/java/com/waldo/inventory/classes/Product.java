package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Product extends DbObject {

    public static final String TABLE_NAME = "products";
    private static final String insertSql = "INSERT INTO "+TABLE_NAME+" (" +
            "name, iconpath, categoryid) VALUES " +
            "(?, ?, ?)";
    private static final String updateSql =
            "UPDATE "+TABLE_NAME+" " +
                    "SET name = ?, iconpath = ?, categoryid = ? " +
                    "WHERE id = ?;";

    private long categoryId;

    public Product() {
        super(TABLE_NAME, insertSql, updateSql);
    }

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, categoryId);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, categoryId);
        statement.setLong(4, id); // WHERE id
        statement.execute();
    }

    public static Product getUnknownProduct() {
        Product unknown =  new Product();
        unknown.setName(UNKNOWN_NAME);
        unknown.setId(UNKNOWN_ID);
        unknown.setCategoryId(UNKNOWN_ID);
        return unknown;

    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
