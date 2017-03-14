package com.waldo.inventory.gui.adapters;

import com.waldo.inventory.classes.Item;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends AbstractTableModel {

    private final String[] columnNames = {"Name", "Description", "Price", "Data sheet"};
    private List<Item> itemList = new ArrayList<>();

    public ItemListAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    public Item getItemAt(int row) {
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

    public void tableClicked(JTable table, MouseEvent e) {
        int col = table.columnAtPoint(e.getPoint());
        if (col == 3) { // Data sheet column
            int row = table.rowAtPoint(e.getPoint());
            Item item = itemList.get(row);
            if (item != null) {
                System.out.print(item);
            }
        }
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
        final Item item = itemList.get(rowIndex);
        switch (columnIndex) {
            case 0: // Name
                return item.getName();
            case 1: // Description
                return item.getDescription();
            case 2: // Price
                return item.getPrice();
            case 3: // Data sheet
                return "Data sheet";
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
