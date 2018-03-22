package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class OrderFileFormat extends DbObject {

    public static final String TABLE_NAME = "orderfileformat";

    private String separator;

    public OrderFileFormat() {
        super(TABLE_NAME);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;
        statement.setString(ndx++, getName());
        statement.setString(ndx++, String.valueOf(getSeparator()));
        return ndx;
    }

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

    @Override
    public OrderFileFormat createCopy() {
        return createCopy(new OrderFileFormat());
    }

    @Override
    public OrderFileFormat createCopy(DbObject copyInto) {
        OrderFileFormat off = (OrderFileFormat) copyInto;
        copyBaseFields(off);
        off.setSeparator(getSeparator());
        return off;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
