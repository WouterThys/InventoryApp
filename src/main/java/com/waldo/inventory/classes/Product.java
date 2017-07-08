package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Product extends DbObject {

    public static final String TABLE_NAME = "products";
    private long categoryId;

    public Product() {
        super(TABLE_NAME);
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

    @Override
    public Product createCopy(DbObject copyInto) {
        Product product = new Product();
        copyBaseFields(product);
        product.setCategoryId(getCategoryId());
        return product;
    }

    @Override
    public Product createCopy() {
        return createCopy(new Product());
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
