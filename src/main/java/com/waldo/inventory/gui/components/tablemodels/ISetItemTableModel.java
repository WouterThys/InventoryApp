package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.SetItem;
import com.waldo.inventory.gui.components.ILabel;

import java.util.Comparator;

public class ISetItemTableModel extends IAbstractTableModel<SetItem> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Value", "Location"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class};

    public ISetItemTableModel(Comparator<SetItem> comparator) {
        super(COLUMN_NAMES, COLUMN_CLASSES, comparator);
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
                    return setItem.toString();
                case 2: // Value
                    return setItem.getValue();
                case 3: // Locations
                    if (setItem.getLocation() != null) {
                        return setItem.getLocation().getPrettyString();
                    }
            }
        }

        return null;
    }
}
