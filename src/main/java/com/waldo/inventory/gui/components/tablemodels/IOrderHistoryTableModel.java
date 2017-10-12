package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.Order;

public class IOrderHistoryTableModel extends IAbstractTableModel<Order> {
    private static final String[] COLUMN_NAMES = {"Name", "Date", "Open"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class};

    public IOrderHistoryTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Order component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Name
                    return component.getName();
                case 1: // Date
                    if (component.isOrdered()) {
                        return component.getDateOrdered(); // TODO format
                    } else {
                        return "";
                    }
                case 2: // Open
                    return "";
            }
        }
        return null;
    }
}