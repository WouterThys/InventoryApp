package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

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
    public int addParameters(PreparedStatement statement) throws SQLException {
        return addBaseParameters(statement);
    }

    @Override
    public Category createCopy(DbObject copyInto) {
        Category category = (Category) copyInto;
        copyBaseFields(category);
        return category;
    }

    @Override
    public Category createCopy() {
        return createCopy(new Category());
    }

    public static Category getUnknownCategory() {
        Category unknown =  new Category();
        unknown.setName(UNKNOWN_NAME);
        unknown.setId(UNKNOWN_ID);
        unknown.setCanBeSaved(false);
        return unknown;
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }
}
