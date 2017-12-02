package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<OrderFileFormat> offs = cache().getOrderFileFormats();
                if (!offs.contains(this)) {
                    offs.add(this);
                }
                cache().notifyListeners(DatabaseAccess.OBJECT_INSERT, this, cache().onOrderFileFormatChangedListenerList);
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                cache().notifyListeners(DatabaseAccess.OBJECT_UPDATE, this, cache().onOrderFileFormatChangedListenerList);
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<OrderFileFormat> offs = cache().getOrderFileFormats();
                if (offs.contains(this)) {
                    offs.remove(this);
                }
                cache().notifyListeners(DatabaseAccess.OBJECT_DELETE, this, cache().onOrderFileFormatChangedListenerList);
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
