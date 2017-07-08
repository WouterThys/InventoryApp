package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.database.SearchManager;

import java.util.ArrayList;
import java.util.List;

public class IOrderItemTableModel extends IAbstractTableModel<OrderItem> {

    private static final String[] COLUMN_NAMES = {"Name", "Description", "Manufacturer", "Reference", "Amount", "Price", "Total"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class, String.class, Number.class, Double.class, Double.class};

    public IOrderItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public IOrderItemTableModel(List<OrderItem> orderItemList) {
        super(COLUMN_NAMES, COLUMN_CLASSES, orderItemList);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OrderItem orderItem = getItemAt(rowIndex);
        if (orderItem != null) {
            switch (columnIndex) {
                case -1:
                    return orderItem;
                case 0: // Name
                    return orderItem.getItem().getName();
                case 1: // Description
                    return orderItem.getItem().getDescription();
                case 2: // Manufacturer
                    Manufacturer m = SearchManager.sm().findManufacturerById(orderItem.getItem().getManufacturerId());
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