package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Type extends DbObject {

    public static final String TABLE_NAME = "types";
    private long productId;

    public Type() {
        super(TABLE_NAME);
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
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                java.util.List<Type> list = cache().getTypes();
                if (!list.contains(this)) {
                    list.add(this);
                }
                cache().notifyListeners(DbManager.OBJECT_INSERT, this, cache().onTypesChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                cache().notifyListeners(DbManager.OBJECT_UPDATE, this, cache().onTypesChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                java.util.List<Type> list = cache().getTypes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                cache().notifyListeners(DbManager.OBJECT_DELETE, this, cache().onTypesChangedListenerList);
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
        this.productId = productId;
    }
}