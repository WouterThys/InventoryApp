package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class SetItem extends DbObject {

    public static final String TABLE_NAME = "setitems";

    private int amount;
    private String value;
    private long itemId;
    private Item item;

    public SetItem() {
        super(TABLE_NAME);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setInt(ndx++, amount);
        statement.setString(ndx++, getValue());
        statement.setLong(ndx++, getItemId());
        return ndx;
    }

    @Override
    public String toString() {
        return getName() + " - " + getValue();
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<SetItem> list = db().getSetItems();
                if (!list.contains(this)) {
                    list.add(this);
                }
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onSetItemChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onSetItemChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<SetItem> list = db().getSetItems();
                if (list.contains(this)) {
                    list.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onSetItemChangedListenerList);
                break;
            }
        }
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            if (getValue().toUpperCase().contains(getValue().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result  = super.equals(obj);
        if (result) {
            if (!(obj instanceof SetItem)) {
                return false;
            } else {
                SetItem ref = (SetItem) obj;
                if (!(ref.getValue().equals(getValue()))) return false;
                if (!(ref.getAmount() == getAmount())) return false;
                if (!(ref.getItemId() == getItemId())) return false;
            }
        }
        return result;
    }

    @Override
    public SetItem createCopy() {
        return createCopy(new SetItem());
    }

    @Override
    public SetItem createCopy(DbObject copyInto) {
        SetItem setItem = (SetItem) copyInto;
        copyBaseFields(setItem);

        setItem.setAmount(getAmount());
        setValue(getValue());
        return setItem;
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getValue() {
        if (value == null) {
            value = "";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        item = null;
        this.itemId = itemId;
    }

    public Item getItem() {
        if (item == null) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }
}
