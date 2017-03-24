package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Type extends DbObject {

    public static final String TABLE_NAME = "types";
    private static final String insertSql = "INSERT INTO "+TABLE_NAME+" (" +
            "name, iconpath, productid) VALUES " +
            "(?, ?, ?)";
    private static final String updateSql =
            "UPDATE "+TABLE_NAME+" " +
                    "SET name = ?, iconpath = ?, productid = ? " +
                    "WHERE id = ?;";

    private long productId;

    public Type() {
        super(TABLE_NAME, insertSql, updateSql);
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

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
