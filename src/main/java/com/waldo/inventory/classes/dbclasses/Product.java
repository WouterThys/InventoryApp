package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Product extends DbObject {

    public static final String TABLE_NAME = "products";
    private long categoryId;
    private Category category;

    public Product() {
        super(TABLE_NAME);
    }

    public Product(long categoryId) {
        super(TABLE_NAME);
        this.categoryId = categoryId;
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, categoryId);
        return ndx;
    }

    @Override
    public Product createCopy(DbObject copyInto) {
        Product product = new Product();
        copyBaseFields(product);
        product.setCategoryId(getCategoryId());
        return product;
    }

    @Override
    public Product createCopy() {
        return createCopy(new Product());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<Product> list = cache().getProducts();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<Product> list = cache().getProducts();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public static Product getUnknownProduct() {
        Product unknown =  new Product();
        unknown.setName(UNKNOWN_NAME);
        unknown.setId(UNKNOWN_ID);
        unknown.setCategoryId(UNKNOWN_ID);
        return unknown;

    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        if (category != null && category.getId() != categoryId) {
            category = null;
        }
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        if (category == null) {
            category = SearchManager.sm().findCategoryById(categoryId);
        }
        return category;
    }
}
