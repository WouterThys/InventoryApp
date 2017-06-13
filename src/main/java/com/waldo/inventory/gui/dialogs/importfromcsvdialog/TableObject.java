package com.waldo.inventory.gui.dialogs.importfromcsvdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.database.DbManager;

import java.util.ArrayList;
import java.util.List;

public class TableObject {

    private String itemReference; // Reference from csv
    private long itemId; // Reference to known item
    private List<String> extraData;

    public boolean isValid() {
        if (itemId <= DbObject.UNKNOWN_ID) {
            return false;
        }
        return (DbManager.db().findItemById(itemId)) != null;
    }

    public String getItemReference() {
        return itemReference;
    }

    public void setItemReference(String itemReference) {
        this.itemReference = itemReference;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public List<String> getExtraData() {
        if (extraData == null) {
            extraData = new ArrayList<>();
        }
        return extraData;
    }

    public void setExtraData(List<String> extraData) {
        this.extraData = extraData;
    }
}
