package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.PendingOrder;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IAbstractTableModel;

public class IPendingOrdersTableModel extends IAbstractTableModel<PendingOrder> {

    private static final String[] COLUMN_NAME = {"Date", "Item", "Distributor"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class};

    public IPendingOrdersTableModel() {
        super(COLUMN_NAME, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PendingOrder pendingOrder = getItemAt(rowIndex);
        if (pendingOrder != null) {
            switch (columnIndex) {
                case -1: // Reference to object itself
                    return pendingOrder;
                case 0: // Date
                    return DateUtils.formatDateTime(pendingOrder.getOrderDate());
                case 1: // Item
                    if (pendingOrder.getItemId() > DbObject.UNKNOWN_ID) {
                        return pendingOrder.getItem().toString();
                    }
                    return "";
                case 2: // Distributor
                    if (pendingOrder.getDistributorId() > DbObject.UNKNOWN_ID) {
                        return pendingOrder.getDistributor().toString();
                    }
                    return "";
            }
        }
        return null;
    }
}
