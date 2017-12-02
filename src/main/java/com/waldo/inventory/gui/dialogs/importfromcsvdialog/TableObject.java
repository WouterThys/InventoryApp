package com.waldo.inventory.gui.dialogs.importfromcsvdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;

import java.util.ArrayList;
import java.util.List;

public class TableObject {

    private String itemReference; // Reference from csv
    private Item item; // Reference to known item
    private List<String> extraData;
    private int found = 0;

    public boolean isValid() {
        return (item != null && item.getId() > DbObject.UNKNOWN_ID);
    }

    public String getItemReference() {
        return itemReference;
    }

    public void setItemReference(String itemReference) {
        this.itemReference = itemReference;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<String> getExtraData() {
        if (extraData == null) {
            extraData = new ArrayList<>();
        }
        return extraData;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public void setExtraData(List<String> extraData) {
        this.extraData = extraData;
    }

    @Override
    public String toString() {
        return "TableObject{" +
                "itemReference='" + itemReference + '\'' +
                ", item=" + item +
                ", extraData=" + extraData +
                ", found=" + found +
                '}';
    }
}
