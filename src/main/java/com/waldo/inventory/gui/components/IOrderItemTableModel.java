package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.database.DbManager;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class IOrderItemTableModel extends AbstractTableModel {

    private static final String[] columnNames = {"Name", "Description", "Manufacturer", "Reference", "Amount", "Price", "Total"};
    private static final Class[] columnClasses = {String.class, String.class, String.class, String.class, Number.class, Double.class, Double.class};

    private List<OrderItem> itemList;

    public IOrderItemTableModel() {
        itemList = new ArrayList<>();
    }

    public IOrderItemTableModel(List<OrderItem> itemList) {
        this.itemList = itemList;
    }

    public void setItemList(List<OrderItem> itemList) {
        this.itemList = itemList;
        fireTableDataChanged();
    }

    public List<OrderItem> getItemList() {
        return itemList;
    }

    public OrderItem getItem(int index) {
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
        OrderItem orderItem = getItem(rowIndex);
        if (orderItem != null) {
            switch (columnIndex) {
                case 0: // Name
                    return orderItem.getItem().getName();
                case 1: // Description
                    return orderItem.getItem().getDescription();
                case 2: // Manufacturer
                    Manufacturer m = DbManager.db().findManufacturerById(orderItem.getItem().getManufacturerId());
                    if (m != null && m.getId() != DbObject.UNKNOWN_ID) {
                        return m.getName();
                    }
                    return "";
                case 3: // Reference
                    return orderItem.getItemRef();
                case 4: // Amount
                    return orderItem.getAmount();
                case 5: // Price
                    return orderItem.getItem().getPrice();
                case 6: // Total
                    return orderItem.getAmount() * orderItem.getItem().getPrice(); // Amount * price
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return ((columnIndex == 3) || (columnIndex == 4)); // Reference and price are editable
    }
}
