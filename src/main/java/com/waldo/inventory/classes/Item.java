package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.*;

public class Item {

    private long id = -1;
    private String name;
    private String description;
    private double price;

    public void save() throws SQLException {
        try (Connection connection = DbManager.getConnection()) {
            if (id == -1) { // Save
                final String sql = "INSERT INTO items (name, description, price) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, name);
                    statement.setString(2, description);
                    statement.setDouble(3, price);
                    statement.execute();

                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getLong(1);
                    }
                }
            } else { // Update
                final String sql = "UPDATE items SET name = ?, description = ?, price = ? WHERE id = ? ";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, name);
                    statement.setString(2, description);
                    statement.setDouble(3, price);
                    statement.execute();
                }
            }

        }
    }

    public void delete() throws SQLException {
        if (id != -1) {
            final String sql = "DELETE FROM items WHERE id = ?";
            try (Connection connection = DbManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.execute();
                id = -1; // Contact is not in database
            }
        }
    }

    @Override
    public String toString() {
        if (name == null) {
            return "(No name)";
        }
        if (id == -1) {
            return name + "*";
        }
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
