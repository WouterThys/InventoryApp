package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
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
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<Category> categories = CacheManager.cache().getCategories();
                if (!categories.contains(this)) {
                    categories.add(this);
                }
                CacheManager.cache().notifyListeners(DatabaseAccess.OBJECT_INSERT, this, CacheManager.cache().onCategoriesChangedListenerList);
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                CacheManager.cache().notifyListeners(DatabaseAccess.OBJECT_UPDATE, this, CacheManager.cache().onCategoriesChangedListenerList);
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<Category> categories = CacheManager.cache().getCategories();
                if (categories.contains(this)) {
                    categories.remove(this);
                }
                CacheManager.cache().notifyListeners(DatabaseAccess.OBJECT_DELETE, this, CacheManager.cache().onCategoriesChangedListenerList);
                break;
            }
        }
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }
}
