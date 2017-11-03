package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.classes.Order;

import java.util.List;

public class IOrderHistoryTableModel extends IAbstractTableModel<Order> {

    private static final String[] COLUMN_NAMES = {"Name", "Date ordered", "Open"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class};

    public IOrderHistoryTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public IOrderHistoryTableModel(List<Order> orderList) {
        super(COLUMN_NAMES, COLUMN_CLASSES, orderList);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Order component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Name
                    return component.toString();
                case 1: // Date
                    if (component.isOrdered()) {
                        return DateUtils.formatDateTime(component.getDateOrdered());
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