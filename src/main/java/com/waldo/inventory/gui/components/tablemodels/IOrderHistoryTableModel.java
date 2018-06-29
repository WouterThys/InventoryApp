package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IAbstractTableModel;

import java.util.List;

public class IOrderHistoryTableModel extends IAbstractTableModel<ItemOrder> {

    private static final String[] COLUMN_NAMES = {"Name", "Date ordered", "Open"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class};

    public IOrderHistoryTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public IOrderHistoryTableModel(List<ItemOrder> itemOrderList) {
        super(COLUMN_NAMES, COLUMN_CLASSES, itemOrderList);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ItemOrder component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Name
                    return component.toString();
                case 1: // Date
                    if (component.isOrdered()) {
                        return DateUtils.formatDate(component.getDateOrdered());
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