package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.gui.components.ILabel;

public class ILinkItemTableModel extends IAbstractTableModel<PcbItemItemLink> {

    private static final String[] COLUMN_HEADER_TOOLTIPS = {null, "Item name", "Matches name", "Matches value", "Matches footprint"};
    private static final String[] COLUMN_NAMES = {"", "Name", "N", "V", "FP"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, Boolean.class, Boolean.class, Boolean.class};

    public ILinkItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, COLUMN_HEADER_TOOLTIPS);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItemItemLink match = getItemAt(rowIndex);

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
                    return match.toString();
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