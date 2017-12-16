package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Type extends DbObject {

    public static final String TABLE_NAME = "types";
    private long productId;
    private Product product;

    public Type() {
        super(TABLE_NAME);
    }

    public Type(long productId) {
        super(TABLE_NAME);
        this.productId = productId;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, productId);
        return ndx;
    }

    @Override
    public Type createCopy(DbObject copyInto) {
        Type type = (Type) copyInto;
        copyBaseFields(type);
        type.setProductId(getProductId());
        return type;
    }

    @Override
    public Type createCopy() {
        return createCopy(new Type());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                java.util.List<Type> list = cache().getTypes();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                java.util.List<Type> list = cache().getTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public static Type getUnknownType() {
        Type unknown =  new Type();
        unknown.setName(UNKNOWN_NAME);
        unknown.setId(UNKNOWN_ID);
        unknown.setProductId(UNKNOWN_ID);
        unknown.setCanBeSaved(false);
        return unknown;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        product = null;
        this.productId = productId;
    }

    public Product getProduct() {
        if (product == null) {
            product = SearchManager.sm().findProductById(productId);
        }
        return product;
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
