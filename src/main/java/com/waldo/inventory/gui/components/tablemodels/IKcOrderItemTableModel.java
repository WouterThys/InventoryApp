package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.OrderItem;

public class IKcOrderItemTableModel extends IAbstractTableModel<OrderItem> {

    private static final String[] COLUMN_NAME = {"Name", "Description", "#"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, Integer.class};

    public IKcOrderItemTableModel() {
        super(COLUMN_NAME, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OrderItem orderItem = getItemAt(rowIndex);
        if (orderItem != null) {
            switch (columnIndex) {
                case -1: // Reference to object itself
                    return orderItem;
                case 0: // Name
                    return orderItem.getItem().getName();
                case 1: // Description
                    return orderItem.getItem().getDescription();
                case 2: // Amount
                    return orderItem.getAmount();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 2); // Amount is editable
    }
}
