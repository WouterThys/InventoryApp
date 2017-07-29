package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.gui.components.ITable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public abstract class IAbstractTableModel<T> extends AbstractTableModel {

    private final String[] columnNames;
    private final Class[] columnClasses;

    private List<T> itemList;

    IAbstractTableModel(String[] columnNames, Class[] columnClasses) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        itemList = new ArrayList<T>();
    }

    IAbstractTableModel(String[] columnNames, Class[] columnClasses, List<T> itemList) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.itemList = itemList;
    }

    public void setItemList(List<T> itemList) {
        this.itemList = itemList;
        fireTableDataChanged();
    }

    public void clearItemList() {
        this.itemList.clear();
        fireTableDataChanged();
    }

    public void removeItems(List<T> itemsToDelete) {
        for(T t : itemsToDelete) {
            int ndx = itemList.indexOf(t);
            itemList.remove(ndx);
            fireTableRowsDeleted(ndx, ndx);
        }
    }

    public void addItems(List<T> itemsToAdd) {
        for (T t : itemsToAdd) {
            if (!itemList.contains(t)) {
                itemList.add(t);
                int ndx = itemList.indexOf(t);
                fireTableRowsInserted(ndx, ndx);
            }
        }
    }

    public void updateTable() {
        if (itemList != null && itemList.size() > 0) {
            fireTableRowsUpdated(0, itemList.size() - 1);
        }
    }

    public List<T> getItemList() {
        return itemList;
    }

    T getItemAt(int index) {
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


}
