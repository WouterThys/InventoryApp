package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Product extends DbObject {

    public static final String TABLE_NAME = "products";
    private long categoryId;

    public Product() {
        super(TABLE_NAME);
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
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Product> list = cache().getProducts();
                if (!list.contains(this)) {
                    list.add(this);
                }
                cache().notifyListeners(DbManager.OBJECT_INSERT, this, cache().onProductsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                cache().notifyListeners(DbManager.OBJECT_UPDATE, this, cache().onProductsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Product> list = cache().getProducts();
                if (list.contains(this)) {
                    list.remove(this);
                }
                cache().notifyListeners(DbManager.OBJECT_DELETE, this, cache().onProductsChangedListenerList);
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
        this.categoryId = categoryId;
    }
}
