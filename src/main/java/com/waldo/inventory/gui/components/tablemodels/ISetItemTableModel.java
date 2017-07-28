package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.gui.components.ILabel;

import java.util.List;

public class ISetItemTableModel extends IAbstractTableModel<SetItem> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Value"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class};

    public ISetItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public ISetItemTableModel(List<SetItem> itemList) {
        super(COLUMN_NAMES, COLUMN_CLASSES, itemList);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SetItem setItem = getItemAt(rowIndex);

        if (setItem != null) {
            switch (columnIndex) {
                case -1:
                    return setItem;
                case 0: // Amount label
                    return setItem;
                case 1: // Name
                    return setItem.getName();
                case 2: // Value
                    return setItem.getValue();
            }
        }

        return null;
    }
}
