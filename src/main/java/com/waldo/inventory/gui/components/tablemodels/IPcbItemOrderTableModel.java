package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.ItemOrderLine;
import com.waldo.utils.icomponents.IAbstractTableModel;

public class IPcbItemOrderTableModel extends IAbstractTableModel<ItemOrderLine> {

    private static final String[] COLUMN_NAME = {"ItemOrder", "Item", "#"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, Integer.class};

    public IPcbItemOrderTableModel() {
        super(COLUMN_NAME, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ItemOrderLine orderItem = getItemAt(rowIndex);
        if (orderItem != null) {
            switch (columnIndex) {
//                case -1: // Reference to object itself
//                    return orderItem;
//                case 0: // ItemOrder name
//                    return orderItem.getPcbOrder().getName();
//                case 1: // Item name
//                    return orderItem.getItem().toString();
//                case 2: // Amount
//                    return orderItem.getAmount();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 2); // Amount is editable
    }
}
