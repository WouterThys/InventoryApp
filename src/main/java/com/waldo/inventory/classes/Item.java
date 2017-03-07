package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.*;

public class Item extends DbObject {

    public static final String TABLE_NAME = "items";

    private String description;
    private double price;

    private int category = -1;
    private int product = -1;
    private int type = -1;

    private static final String insertSql = "INSERT INTO items (" +
                "name, description, price, category, product, type) VALUES " +
                "(?, ?, ?, ?, ?, ?)";



    private static final String updateSql = "UPDATE items SET " +
                "name = ?, " +
                "description = ?, " +
                "price = ? " +
                "category = ? " +
                "product = ? " +
                "type = ? " +
                "WHERE id = ? ";

    private static final String deleteSql = "DELETE FROM items WHERE id = ?";

    public Item() {
        super(TABLE_NAME, insertSql, updateSql, deleteSql);
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, description);
        statement.setDouble(3, price);
        statement.setInt(4, category);
        statement.setInt(5, product);
        statement.setInt(6, type);
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, description);
        statement.setDouble(3, price);
        statement.setInt(4, category);
        statement.setInt(5, product);
        statement.setInt(6, type);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
