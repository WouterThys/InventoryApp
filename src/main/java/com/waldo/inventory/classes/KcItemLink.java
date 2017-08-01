package com.waldo.inventory.classes;

import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class KcItemLink extends DbObject {

    public static final String TABLE_NAME = "kcitemlink";

    // Bitwise matches
    public static final int MATCH_NAME = 1;
    public static final int MATCH_VALUE = 2;
    public static final int MATCH_FOOTPRINT = 4;

    private int match;
    private long itemId;
    private Item item;
    private long setItemId;
    private SetItem setItem;
    private long kcComponentId;
    private KcComponent kcComponent;

    private boolean isSetItem;
    private boolean isMatched;

    public KcItemLink() {
        super(TABLE_NAME);
    }

    public KcItemLink(int match, KcComponent kcComponent, Item item) {
        super(TABLE_NAME);
        this.match = match;
        this.item = item;
        this.kcComponent = kcComponent;

        if (kcComponent != null) {
            kcComponentId = kcComponent.getId();
        }

        if (item != null) {
            itemId = item.getId();
        }

        isSetItem = false;
        setItemId = -1;
    }

    public KcItemLink(int match, KcComponent kcComponent, SetItem setItem) {
        super(TABLE_NAME);
        this.match = match;
        this.setItem = setItem;
        this.kcComponent = kcComponent;

        if (kcComponent != null) {
            kcComponentId = kcComponent.getId();
        }

        if (setItem != null) {
            setItemId = setItem.getId();
        }

        isSetItem = true;
        itemId = -1;
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
        statement.setLong(ndx++, getKcComponentId());

        return ndx;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<KcItemLink> list = db().getKcItemLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }

                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<KcItemLink> list = db().getKcItemLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onKcItemLinkChangedListenerList);
    }

    @Override
    public KcItemLink createCopy() {
        return createCopy(new KcItemLink());
    }

    @Override
    public KcItemLink createCopy(DbObject copyInto) {
        KcItemLink cpy = (KcItemLink) copyInto;

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
        return itemId;
    }

    public void setSetItemId(long id) {
        this.setItemId = id;
    }

    public long getSetItemId() {
        return setItemId;
    }

    public long getKcComponentId() {
        return kcComponentId;
    }

    public void setKcComponentId(long kcComponentId) {
        kcComponent = null;
        this.kcComponentId = kcComponentId;
    }

    public KcComponent getKcComponent() {
        if (kcComponent == null) {
            kcComponent = SearchManager.sm().findKcComponentById(kcComponentId);
        }
        return kcComponent;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
