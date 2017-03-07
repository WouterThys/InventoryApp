package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Product extends DbObject {

    public Product() {
        super("products");
    }
}
