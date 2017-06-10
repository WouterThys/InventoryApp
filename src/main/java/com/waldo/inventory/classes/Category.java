package com.waldo.inventory.classes;

public class Category extends DbObject {

    public static final String TABLE_NAME = "categories";

    public Category() {
        super(TABLE_NAME);
    }

    public Category(String name) {
        super(TABLE_NAME);
        setName(name);
    }

    @Override
    public DbObject createCopy(DbObject original) {
        Category category = new Category();
        category.setId(original.getId());
        category.setName(original.getName());
        category.setIconPath(original.getIconPath());
        return category;
    }

    public static Category getUnknownCategory() {
        Category unknown =  new Category();
        unknown.setName(UNKNOWN_NAME);
        unknown.setId(UNKNOWN_ID);
        unknown.setCanBeSaved(false);
        return unknown;
    }
}
