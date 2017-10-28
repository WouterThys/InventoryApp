package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.PcbItem;

public class ILinkedPcbItemTableModel extends IAbstractTableModel<PcbItem> {
    private static final String[] COLUMN_NAMES = {"#", "Pcb item", "Item"};
    private static final Class[] COLUMN_CLASSES = {Integer.class, String.class, String.class};

    public ILinkedPcbItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItem pcbItem = getItemAt(rowIndex);
        if (pcbItem != null) {
            switch (columnIndex) {
                case -1:
                    return pcbItem;
                case 0: // Amount
                    return pcbItem.getOrderAmount();
                case 1: // Pcb item name and value
                    if (pcbItem.getMatchedItemLink().isSetItem()) {
                        return pcbItem.getPartName() + " (Set)";
                    } else {
                        String name = pcbItem.getPartName();
                        String value = pcbItem.getValue();
                        if (name.equals(value)) {
                            return name;
                        } else {
                            return name + " - " + value;
                        }
                    }
                case 2: // Item name
                    return pcbItem.getMatchedItemLink().getItem().toString();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0); // Amount is editable
    }
}