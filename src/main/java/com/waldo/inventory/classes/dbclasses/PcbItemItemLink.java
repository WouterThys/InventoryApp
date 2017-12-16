package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Main;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbItemItemLink extends DbObject {

    public static final String TABLE_NAME = "pcbitemitemlinks";

    // Bitwise matches
    public static final int MATCH_NAME = 1;
    public static final int MATCH_VALUE = 2;
    public static final int MATCH_FOOTPRINT = 4;

    private int match;

    private long itemId;
    private Item item;

    private long setItemId;
    private SetItem setItem;

    private long pcbItemId;
    private PcbItem pcbItem;

    private boolean isSetItem;
    private boolean isMatched;

    public PcbItemItemLink() {
        super(TABLE_NAME);
    }

    public PcbItemItemLink(int match, PcbItem pcbItem, Item item) {
        super(TABLE_NAME);
        this.match = match;
        this.item = item;
        this.pcbItem = pcbItem;

        if (pcbItem != null) {
            pcbItemId = pcbItem.getId();
        }

        if (item != null) {
            itemId = item.getId();
        }

        isSetItem = false;
        setItemId = -1;
    }

    public PcbItemItemLink(int match, PcbItem pcbItem, SetItem setItem) {
        super(TABLE_NAME);
        this.match = match;
        this.setItem = setItem;
        this.pcbItem = pcbItem;

        if (pcbItem != null) {
            pcbItemId = pcbItem.getId();
        }

        if (setItem != null) {
            setItemId = setItem.getId();
        }

        isSetItem = true;
        itemId = -1;
    }

    @Override
    public String toString() {
        String s = getName();
        if (Main.DEBUG_MODE) {
            s += " (" + getId() + ")";
        }
        return s;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        if (getItemId() < UNKNOWN_ID) {
            setItemId(UNKNOWN_ID);
        }
        if (getSetItemId() < UNKNOWN_ID) {
            setSetItemId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getItemId());
        statement.setLong(ndx++, getSetItemId());
        statement.setBoolean(ndx++, isSetItem());
        statement.setInt(ndx++, getMatch());
        statement.setLong(ndx++, getPcbItemId());

        return ndx;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<PcbItemItemLink> list = cache().getPcbItemItemLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }

                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<PcbItemItemLink> list = cache().getPcbItemItemLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    @Override
    public PcbItemItemLink createCopy() {
        return createCopy(new PcbItemItemLink());
    }

    @Override
    public PcbItemItemLink createCopy(DbObject copyInto) {
        PcbItemItemLink cpy = (PcbItemItemLink) copyInto;

        copyBaseFields(cpy);
        cpy.setItemId(getItemId());
        cpy.setSetItemId(getSetItemId());
        cpy.setIsSetItem(isSetItem());
        cpy.setMatch(getMatch());

        return cpy;
    }

    public boolean hasNameMatch() {
        return (match & MATCH_NAME) == MATCH_NAME;
    }

    public boolean hasValueMatch() {
        return (match & MATCH_VALUE) == MATCH_VALUE;
    }

    public boolean hasFootprintMatch() {
        return (match & MATCH_FOOTPRINT) == MATCH_FOOTPRINT;
    }

    public String getName() {
        if (isSetItem) {
            return getItem().getName() + "/" + getSetItem().toString();
        } else {
            return getItem().getName();
        }
    }

    public int getAmount() {
        if (isSetItem()) {
            return getSetItem().getAmount();
        } else {
            return getItem().getAmount();
        }
    }


    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public Item getItem() {
        if (isSetItem) {
            return getSetItem().getItem();
        }
        if (item == null) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }


    public SetItem getSetItem() {
        if (setItem == null) {
            setItem = SearchManager.sm().findSetItemById(setItemId);
        }
        return setItem;
    }

    public boolean isSetItem() {
        return isSetItem;
    }

    public void setIsSetItem(boolean setItem) {
        isSetItem = setItem;
    }

    public void setItemId(long id) {
        this.itemId = id;
    }

    public long getItemId() {
        if (isSetItem) {
            itemId = getItem().getId();
        }
        return itemId;
    }

    public void setSetItemId(long id) {
        this.setItemId = id;
    }

    public long getSetItemId() {
        return setItemId;
    }

    public long getPcbItemId() {
        return pcbItemId;
    }

    public void setPcbItemId(long pcbItemId) {
        pcbItem = null;
        this.pcbItemId = pcbItemId;
    }

    public PcbItem getPcbItem() {
        if (pcbItem == null) {
            pcbItem = SearchManager.sm().findPcbItemById(pcbItemId);
        }
        return pcbItem;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }



    //
    // Helpers
    //
    public String getLinkedItemName() {
        if (isSetItem) {
            return getSetItem().toString();
        } else {
            return getItem().toString();
        }
    }

    public int getLinkedItemAmount() {
        if (isSetItem) {
            return getSetItem().getAmount();
        } else {
            return getItem().getAmount();
        }
    }
}
