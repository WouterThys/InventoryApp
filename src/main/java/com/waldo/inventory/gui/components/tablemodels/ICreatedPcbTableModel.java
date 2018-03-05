package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;

public class ICreatedPcbTableModel extends IAbstractTableModel<CreatedPcbLink> {

    private static final String[] COLUMN_NAMES = {"PCB item", "Linked item", "Used item", "Used amount"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, ILabel.class, ILabel.class, Integer.class};

    public ICreatedPcbTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CreatedPcbLink link = getItemAt(rowIndex);
        if (link != null) {
            switch (columnIndex) {
                case -1:
                    return link;
                case 0: // PCB item
                    return link.getPcbItem().toString();
                case 1: // Linked item
                    PcbItemItemLink itemLink = link.getPcbItemItemLink();
                    if (itemLink != null) {
                        return itemLink.getItem();
                    } else {
                        return null;
                    }
                case 2: // Used item
                    return link.getUsedItem();
                case 3:
                    return link.getUsedAmount();
            }
        }
        return null;
    }
}

