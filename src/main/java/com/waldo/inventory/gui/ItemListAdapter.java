package com.waldo.inventory.gui;

import com.waldo.inventory.classes.Item;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends AbstractTableModel {

    private final String[] columnNames = {"Name", "Description", "Price"};
    private List<Item> itemList = new ArrayList<>();

    ItemListAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    Item getItemAt(int row) {
        return itemList.get(row);
    }

    public void add(Item item) {
        this.itemList.add(item);
        fireTableDataChanged();
    }

    public void removeAllItems() {
        this.itemList.clear();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = itemList.get(rowIndex);
        switch (columnIndex) {
            case 0: // Name
                return item.getName();
            case 1: // Description
                return item.getDescription();
            case 2: // Price
                return item.getPrice();
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
}
