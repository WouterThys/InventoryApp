package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.database.DbManager;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class IItemTableModel extends AbstractTableModel {

    // Names and classes
    private static final String[] columnNames = {"", "Name", "Description", "Manufacturer"};
    private static final Class[] columnClasses = {ILabel.class, String.class, String.class, String.class};

    private List<Item> itemList;

    public IItemTableModel() {
        itemList = new ArrayList<>();
    }

    public IItemTableModel(List<Item> itemList) {
        this.itemList = itemList;
        itemList.sort(new Item.ItemComparator());
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        this.itemList.sort(new Item.ItemComparator());
        fireTableDataChanged();
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public Item getItem(int index) {
        if (index >= 0 && index < itemList.size()) {
            return itemList.get(index);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return itemList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = getItem(rowIndex);
        if (item != null) {
            switch (columnIndex) {
                case 0: // Amount label
                    return item.getAmount();
                case 1: // Name
                    return item.getName();
                case 2: // Description
                    return item.getDescription();
                case 3: // Manufacturer
                    Manufacturer m = DbManager.db().findManufacturerById(item.getManufacturerId());
                    if (m != null && m.getId() != DbObject.UNKNOWN_ID) {
                        return m.getName();
                    }
                    return "";
            }
        }
        return null;
    }
}
