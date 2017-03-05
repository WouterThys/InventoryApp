package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.*;

public class Item {

    private long id = -1;
    private String name;
    private String description;
    private double price;

    private int category = -1;
    private int product = -1;
    private int type = -1;

    public void save() throws SQLException {
        try (Connection connection = DbManager.getConnection()) {
            if (id == -1) { // Save
                final String sql = "INSERT INTO items (" +
                        "name, description, price, category, product, type) VALUES " +
                        "(?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, name);
                    statement.setString(2, description);
                    statement.setDouble(3, price);
                    statement.setInt(4, category);
                    statement.setInt(5, product);
                    statement.setInt(6, type);
                    statement.execute();

                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getLong(1);
                    }
                }
            } else { // Update
                final String sql = "UPDATE items SET " +
                        "name = ?, " +
                        "description = ?, " +
                        "price = ? " +
                        "category = ? " +
                        "product = ? " +
                        "type = ? " +
                        "WHERE id = ? ";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, name);
                    statement.setString(2, description);
                    statement.setDouble(3, price);
                    statement.setInt(4, category);
                    statement.setInt(5, product);
                    statement.setInt(6, type);
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
