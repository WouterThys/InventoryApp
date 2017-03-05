package com.waldo.inventory.database;

import com.waldo.inventory.classes.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDbManager {

    private static final ItemDbManager INSTANCE = new ItemDbManager();

    public static ItemDbManager getInstance() {
        return INSTANCE;
    }

    private ItemDbManager() {}

    public List<Item> getItems() throws SQLException {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT * FROM items ORDER BY id";
        try (Connection connection = DbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Item i = new Item();
                i.setId(rs.getLong("id"));
                i.setName(rs.getString("name"));
                i.setDescription(rs.getString("description"));
                i.setPrice(rs.getDouble("price"));
                items.add(i);
            }

            return items;
        }
    }

}
