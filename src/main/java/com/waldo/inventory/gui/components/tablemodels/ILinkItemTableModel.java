package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.KcItemLink;
import com.waldo.inventory.gui.components.ILabel;

public class ILinkItemTableModel extends IAbstractTableModel<KcItemLink> {
    private static final String[] COLUMN_NAMES = {"", "Name", "N", "V", "FP"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, Boolean.class, Boolean.class, Boolean.class};

    public ILinkItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KcItemLink match = getItemAt(rowIndex);

        if (match != null) {
            switch (columnIndex) {
                case -1:
                    return match;
                case 0: // Amount
                    if (match.isSetItem()) {
                        return match.getSetItem();
                    } else {
                        return match.getItem();
                    }
                case 1: // Name
                    return match.getName();
                case 2: // Match name
                    return match.hasNameMatch();
                case 3: // Value match
                    return match.hasValueMatch();
                case 4: // Footprint match
                    return match.hasFootprintMatch();
            }
        }
        return null;
    }
}