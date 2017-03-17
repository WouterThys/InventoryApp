package com.waldo.inventory.classes;

public class Type extends DbObject {

    public static final String TABLE_NAME = "types";

    public Type() {
        super(TABLE_NAME);
    }

    public Type(String name) {
        super(TABLE_NAME);
        setName(name);
    }
}
