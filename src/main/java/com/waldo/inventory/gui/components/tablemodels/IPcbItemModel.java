package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.components.ILabel;

public class IPcbItemModel extends IAbstractTableModel<PcbItem> {
    private static final String[] COLUMN_NAMES = {"", "Part", "Value", "Reference"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class};

    public IPcbItemModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItem component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Amount
                    return component;
                case 1: // LibSource value
                    return component.getPartName();
                case 2: // Value
                    return component.getValue();
                case 3: // Reference
                    return component.getReferenceString();
            }
        }
        return null;
    }
}
