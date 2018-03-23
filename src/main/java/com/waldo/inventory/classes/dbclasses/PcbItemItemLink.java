package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbItemItemLink extends DbObject {

    public static final String TABLE_NAME = "pcbitemitemlinks";

    // Bitwise matches
    public static final int MATCH_MANUAL = 0; // User selected item
    public static final int MATCH_NAME = 1;
    public static final int MATCH_VALUE = 2;
    public static final int MATCH_FOOTPRINT = 4;

    private int match;

    private long itemId;
    private Item item;

    private long pcbItemId;
    private PcbItem pcbItem;

    public PcbItemItemLink() {
        super(TABLE_NAME);
    }

    public PcbItemItemLink(@NotNull PcbItem pcbItem,@NotNull Item item) {
        super(TABLE_NAME);
        this.pcbItem = pcbItem;
        if (pcbItem != null) {
            pcbItemId = pcbItem.getId();
        }
        setMatchedItem(item);
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

        statement.setLong(ndx++, getItemId());
        statement.setInt(ndx++, getMatch());
        statement.setLong(ndx++, getPcbItemId());

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
    public PcbItemItemLink createCopy() {
        return createCopy(new PcbItemItemLink());
    }

    @Override
    public PcbItemItemLink createCopy(DbObject copyInto) {
        PcbItemItemLink cpy = (PcbItemItemLink) copyInto;

        copyBaseFields(cpy);
        cpy.setItemId(getItemId());
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
        if (getItem() != null) {
            return getItem().getName();
        }
        return "";
    }


    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public Item getItem() {
        if (item == null) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }

    public void setItemId(long id) {
        if (item != null && item.getId() != id) {
            item = null;
        }
        this.itemId = id;
    }

    public long getItemId() {
        return itemId;
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

    public void setMatchedItem(@NotNull Item matchedItem) {
        this.item = matchedItem;
        this.itemId = item.getId();
    }



    //
    // Helpers
    //
    public String getLinkedItemName() {
        if (getItem() != null) {
            return item.toString();
        }
        return "";
    }

    public int getLinkedItemAmount() {
        if (getItem() != null) {
            return item.getAmount();
        }
        return 0;
    }
}
