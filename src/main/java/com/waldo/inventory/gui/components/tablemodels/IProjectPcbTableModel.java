package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.utils.icomponents.IAbstractTableModel;

public class IProjectPcbTableModel extends IAbstractTableModel<ProjectPcb> {

    private static final String[] COLUMN_NAME = {"Name", "Description", "Project"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class};

    public IProjectPcbTableModel() {
        super(COLUMN_NAME, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProjectPcb pcb = getItemAt(rowIndex);
        if (pcb != null) {
            switch (columnIndex) {
                case -1: // Reference to object itself
                    return pcb;
                case 0: // Name
                    return pcb.toString();
                case 1: // Description
                    return pcb.getDescription();
                case 2: // Project
                    if (pcb.getProject() != null) {
                        return pcb.getProject().toString();
                    }
                    return "";
            }
        }
        return null;
    }
}