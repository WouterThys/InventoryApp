package com.waldo.inventory.classes;

public class KcItemMatch {

    // Bitwise matches
    public static final int MATCH_NAME = 1;
    public static final int MATCH_VALUE = 2;
    public static final int MATCH_FOOTPRINT = 4;

    private int match;
    private Item item;
    private SetItem setItem;

    private boolean isSetItem;
    private boolean isMatched;

    public KcItemMatch(int match, Item item) {
        this.match = match;
        this.item = item;
        isSetItem = false;
    }

    public KcItemMatch(int match, SetItem setItem) {
        this.match = match;
        this.setItem = setItem;
        isSetItem = true;
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
            return getItem().getName() + "/" + setItem.toString();
        } else {
            return item.getName();
        }
    }

    public int getAmount() {
        if (isSetItem()) {
            return setItem.getAmount();
        } else {
            return item.getAmount();
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
            return setItem.getItem();
        }
        return item;
    }

    public SetItem getSetItem() {
        return setItem;
    }

    public boolean isSetItem() {
        return isSetItem;
    }

    public void setIsSetItem(boolean setItem) {
        isSetItem = setItem;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
