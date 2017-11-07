package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.PcbItemProjectLink;

public class IPcbItemUsedTableModel extends IAbstractTableModel<PcbItemProjectLink> {

    private static final String[] COLUMN_NAME = {"Pcb", "Item", "#"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, Integer.class};

    public IPcbItemUsedTableModel() {
        super(COLUMN_NAME, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItemProjectLink link = getItemAt(rowIndex);
        if (link != null) {
            switch (columnIndex) {
                case -1: // Reference to object itself
                    return link;
                case 0: // Project
                    return FileUtils.getLastPathPart(link.getProjectPcb().getDirectory());
                case 1: // Item name
                    return link.getPcbItem().getMatchedItemLink().getItem().toString();
                case 2: // Amount
                    return link.getUsedCount();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 2); // Amount is editable
    }
}
