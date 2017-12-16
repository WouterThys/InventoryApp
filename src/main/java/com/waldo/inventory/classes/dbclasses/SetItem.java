package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Main;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class SetItem extends DbObject {

    public static final String TABLE_NAME = "setitems";

    private int amount;
    private Value value;
    private long itemId;
    private Item item;
    private long locationId = UNKNOWN_ID;
    private Location location;

    public SetItem() {
        super(TABLE_NAME);
        value = new Value();
    }

    public SetItem(String name) {
        super(TABLE_NAME);
        setName(name);
        value = new Value();
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setInt(ndx++, amount);
        statement.setDouble(ndx++, getValue().getDoubleValue());
        statement.setInt(ndx++, getValue().getMultiplier());
        statement.setString(ndx++, getValue().getDbUnit());
        statement.setLong(ndx++, getItemId());
        if (locationId < UNKNOWN_ID) {
            locationId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, getLocationId());
        return ndx;
    }

    @Override
    public String toString() {
        String s = getName() + " - " + getValue();
        if (Main.DEBUG_MODE) {
            s += " (" + getId() + ")";
        }
        return s;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<SetItem> list = cache().getSetItems();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<SetItem> list = cache().getSetItems();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            // TODO
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
                if (!(ref.getValue() == getValue())) return false;
                if (!(ref.getAmount() == getAmount())) return false;
                if (!(ref.getItemId() == getItemId())) return false;
                if (!(ref.getLocationId() == getLocationId())) return false;
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
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

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        location = null;
        this.locationId = locationId;
    }

    public Location getLocation() {
        if (location == null) {
            location = SearchManager.sm().findLocationById(locationId);
        }
        return location;
    }
}
