package com.waldo.inventory.gui.components.wrappers;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;

public class SelectableTableItem {

    private final Item item;
    private boolean isSelected;

    public SelectableTableItem(Item item) {
        this.item = item;
        this.isSelected = true;
    }

    @Override
    public String toString() {
        return "SelectableTableItem{" +
                "item=" + item +
                ", isSelected=" + isSelected +
                '}';
    }

    public Item getItem() {
        return item;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDescription() {
        return item.getDescription();
    }

    public Manufacturer getManufacturer() {
        return item.getManufacturer();
    }

    public int getAmount() {
        return item.getAmount();
    }

    public int getMinimum() {
        return item.getMinimum();
    }

    public Statics.OrderStates getOrderState() {
        return item.getOrderState();
    }
}
