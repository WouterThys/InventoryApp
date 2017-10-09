package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.ProjectPcb;

public class IPcbHistoryTableModel extends IAbstractTableModel<ProjectPcb> {

    private static final String[] COLUMN_NAMES = {"Name", "Project", "Open"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class};

    public IPcbHistoryTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProjectPcb component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Name
                    return component.getName();
                case 1: // Project
                    return component.getProject().getName();
                case 2: // Open
                    return "";
            }
        }
        return null;
    }

}