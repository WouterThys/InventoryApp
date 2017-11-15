package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.CacheManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Category> categories = CacheManager.cache().getCategories();
                if (!categories.contains(this)) {
                    categories.add(this);
                }
                CacheManager.cache().notifyListeners(DbManager.OBJECT_INSERT, this, CacheManager.cache().onCategoriesChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                CacheManager.cache().notifyListeners(DbManager.OBJECT_UPDATE, this, CacheManager.cache().onCategoriesChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Category> categories = CacheManager.cache().getCategories();
                if (categories.contains(this)) {
                    categories.remove(this);
                }
                CacheManager.cache().notifyListeners(DbManager.OBJECT_DELETE, this, CacheManager.cache().onCategoriesChangedListenerList);
                break;
            }
        }
    }
}