package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItem extends DbObject {

    public static final String TABLE_NAME = "orderitems";

    private static final String insertSql = "INSERT INTO "+TABLE_NAME+" (" +
            "(name, iconpath, itemid) VALUES " +
            "(?, ?, ?)";



    private static final String updateSql =
            "UPDATE "+TABLE_NAME+" " +
                    "SET name = ?, iconpath = ?, itemid = ? " +
                    "WHERE id = ?;";

    public OrderItem() {
        super(TABLE_NAME, insertSql, updateSql);
    }


    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, super.getId());
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setLong(3, super.getId());
        statement.setLong(4, id); // WHERE id
        statement.execute();
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
           

        }
        return false;
    }

    @Override
    public long getId() {
        return super.getId();
    }
}
