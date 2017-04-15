package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.database.DbManager;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {

    // Names and classes
    private static final String[] columnNames = {"Name", "Description", "Manufacturer"};
    private static final Class[] columnClasses = {String.class, String.class, String.class};
    public static final Integer[] columnWidths = {25,55,20};

    private List<Item> itemList;

    public ItemTableModel() {
        itemList = new ArrayList<>();
    }

    public ItemTableModel(List<Item> itemList) {
        this.itemList = itemList;
        itemList.sort(new Item.ItemComparator());
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        this.itemList.sort(new Item.ItemComparator());
        fireTableDataChanged();
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
                case 0: // Name
                    return item.getName();
                case 1: // Description
                    return item.getDescription();
                case 2: // Manufacturer
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
