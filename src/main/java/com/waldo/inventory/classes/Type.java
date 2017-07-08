package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Type extends DbObject {

    public static final String TABLE_NAME = "types";
    private long productId;

    public Type() {
        super(TABLE_NAME);
    }

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, productId);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, productId);
        statement.setLong(4, id); // WHERE id
        statement.execute();
    }

    @Override
    public Type createCopy(DbObject copyInto) {
        Type type = (Type) copyInto;
        copyBaseFields(type);
        type.setProductId(getProductId());
        return type;
    }

    @Override
    public Type createCopy() {
        return createCopy(new Type());
    }

    public static Type getUnknownType() {
        Type unknown =  new Type();
        unknown.setName(UNKNOWN_NAME);
        unknown.setId(UNKNOWN_ID);
        unknown.setProductId(UNKNOWN_ID);
        unknown.setCanBeSaved(false);
        return unknown;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
