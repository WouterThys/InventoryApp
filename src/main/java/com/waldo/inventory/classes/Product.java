package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Product extends DbObject {

    public static final String TABLE_NAME = "products";

    public Product() {
        super(TABLE_NAME);
    }
}
