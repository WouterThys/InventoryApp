package com.waldo.inventory.classes.dbclasses;


import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class SetItemLink extends DbObject {

    public static final String TABLE_NAME = "setitemlinks";

    // Variables
    private long setId;
    private Set set;

    private long itemId;
    private Item item;


    public SetItemLink() {
        super(TABLE_NAME);
    }

    public SetItemLink(Set set, Item item) {
        super(TABLE_NAME);

        this.set = set;
        if (set != null) {
            setId = set.getId();
        }

        this.item = item;
        if (item != null) {
            itemId = item.getId();
        }
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        // Add paramaters
        statement.setLong(ndx++, getSetId());
        statement.setLong(ndx++, getItemId());

        return ndx;
    }

    @Override
    public SetItemLink createCopy(DbObject copyInto) {
        SetItemLink cpy = (SetItemLink) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setSetId(getSetId());
        cpy.setItemId(getItemId());

        return cpy;
    }

    @Override
    public SetItemLink createCopy() {
        return createCopy(new SetItemLink());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<SetItemLink> list = cache().getSetItemLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<SetItemLink> list = cache().getSetItemLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public static SetItemLink getUnknownSetItemLink() {
        SetItemLink u = new SetItemLink();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    // Getters and setters

    public long getSetId() {
        return setId;
    }

    public void setSetId(long setId) {
        if (set != null && set.getId() != setId) {
            set = null;
        }
        this.setId = setId;
    }

    public Set getSet() {
        if (set == null) {
            set = SearchManager.sm().findSetById(setId);
        }
        return set;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        if (item != null && item.getId() != itemId) {
            item = null;
        }
        this.itemId = itemId;
    }

    public Item getItem() {
        if (item == null) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }
}