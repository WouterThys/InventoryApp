package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.*;

public class Item extends DbObject {

    public static final String TABLE_NAME = "items";

    private String description = "";
    private double price = 0;

    private long category = -1;
    private long product = -1;
    private long type = -1;
    private String localDataSheet = "";
    private String onlineDataSheet = "";

    private static final String insertSql = "INSERT INTO items (" +
                "name, description, price, category, product, type, localdatasheet, onlinedatasheet) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?)";



    private static final String updateSql =
            "UPDATE items " +
                    "SET name = ?, description = ?, price = ?, category = ?, product = ?, type = ?, localdatasheet = ?, onlinedatasheet = ? " +
                "WHERE id = ?;";

    private static final String deleteSql = "DELETE FROM items WHERE id = ?";

    public Item() {
        super(TABLE_NAME, insertSql, updateSql, deleteSql);
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, description);
        statement.setDouble(3, price);
        statement.setLong(4, category);
        statement.setLong(5, product);
        statement.setLong(6, type);
        statement.setString(7, localDataSheet);
        statement.setString(8, onlineDataSheet);
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, description);
        statement.setDouble(3, price);
        statement.setLong(4, category);
        statement.setLong(5, product);
        statement.setLong(6, type);
        statement.setString(7, localDataSheet);
        statement.setString(8, onlineDataSheet);
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

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public long getProduct() {
        return product;
    }

    public void setProduct(long product) {
        this.product = product;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getLocalDataSheet() {
        return localDataSheet;
    }

    public void setLocalDataSheet(String localDataSheet) {
        this.localDataSheet = localDataSheet;
    }

    public String getOnlineDataSheet() {
        return onlineDataSheet;
    }

    public void setOnlineDataSheet(String onlineDataSheet) {
        this.onlineDataSheet = onlineDataSheet;
    }
}
