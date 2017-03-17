package com.waldo.inventory.classes;

public class Product extends DbObject {

    public static final String TABLE_NAME = "products";

    public Product() {
        super(TABLE_NAME);
    }

    public Product(String name) {
        super(TABLE_NAME);
        setName(name);
    }
}
