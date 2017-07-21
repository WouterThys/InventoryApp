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
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, categoryId);
        return ndx;
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
