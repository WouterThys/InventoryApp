package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.utils.FileUtils;
import com.waldo.utils.icomponents.IAbstractTableModel;

public class IPcbHistoryTableModel extends IAbstractTableModel<ProjectPcb> {

    private static final String[] COLUMN_NAMES = {"Project", "Pcb", "Open"};
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
                case 0: // Project
                    return component.getProject().toString();
                case 1: // Pcb
                    return FileUtils.getLastPathPart(component.getDirectory());
                case 2: // Open
                    return "";
            }
        }
        return null;
    }

}