package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class OrderFileFormat extends DbObject {

    public static final String TABLE_NAME = "orderfileformat";

    private char separator;

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
            case DbManager.OBJECT_INSERT: {
                List<OrderFileFormat> offs = db().getOrderFileFormats();
                if (!offs.contains(this)) {
                    offs.add(this);
                }
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onOrderFileFormatChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onOrderFileFormatChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<OrderFileFormat> offs = db().getOrderFileFormats();
                if (offs.contains(this)) {
                    offs.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onOrderFileFormatChangedListenerList);
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

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public void setSeparator(String separator) {
        if (separator != null && !separator.isEmpty()) {
            this.separator = separator.charAt(0);
        }
    }
}
