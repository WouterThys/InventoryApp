package com.waldo.inventory.gui.components.tablemodels;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public abstract class IAbstractTableModel<T> extends AbstractTableModel {

    private String[] columnHeaderToolTips;
    final String[] columnNames;
    private final Class[] columnClasses;
    private Comparator<? super T> comparator;

    private List<T> itemList;

    IAbstractTableModel(String[] columnNames, Class[] columnClasses, String[] columnHeaderToolTips) {
        this(columnNames, columnClasses);
        this.columnHeaderToolTips = columnHeaderToolTips;
    }

    IAbstractTableModel(String[] columnNames, Class[] columnClasses) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnHeaderToolTips = columnNames;
        itemList = new ArrayList<>();
    }

    IAbstractTableModel(String[] columnNames, Class[] columnClasses, Comparator<? super T> comparator) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnHeaderToolTips = columnNames;
        this.comparator = comparator;
        itemList = new ArrayList<>();
    }

    IAbstractTableModel(String[] columnNames, Class[] columnClasses, List<T> itemList) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnHeaderToolTips = columnNames;
        this.itemList = itemList;
    }

    IAbstractTableModel(String[] columnNames, Class[] columnClasses, List<T> itemList, Comparator<? super T> comparator) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnHeaderToolTips = columnNames;
        this.comparator = comparator;
        setItemList(itemList);
    }

    public void setItemList(List<T> itemList) {
        this.itemList = itemList;

        if (comparator != null) {
            this.itemList.sort(comparator);
        }

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
        if (comparator != null) {
            this.itemList.sort(comparator);
        }
    }

    public void addItems(List<T> itemsToAdd) {
        for (T t : itemsToAdd) {
            if (!itemList.contains(t)) {
                itemList.add(t);

                if (comparator != null) {
                    this.itemList.sort(comparator);
                }

                fireTableDataChanged();
            }
        }
    }

    public void updateTable() {
        if (itemList != null) {
            if (comparator != null) {
                this.itemList.sort(comparator);
            }
            if (itemList.size() == 1) {
                fireTableRowsUpdated(0, 0);
            } else if (itemList.size() > 1){
                fireTableRowsUpdated(0, itemList.size() - 1);
            }
        }
    }

    public void updateItem(T item) {
        if (itemList.contains(item)) {
            int row = itemList.indexOf(item);
            if (row >= 0) {
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public int getModelIndex(T item) {
        if (itemList.contains(item)) {
            return itemList.indexOf(item);
        }
        return -1;
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

    public String[] getColumnHeaderToolTips() {
        return columnHeaderToolTips;
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
        if (columnIndex < columnClasses.length) {
            return columnClasses[columnIndex];
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
