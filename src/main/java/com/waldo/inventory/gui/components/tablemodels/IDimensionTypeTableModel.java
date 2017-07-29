package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.DimensionType;

public class IDimensionTypeTableModel extends IAbstractTableModel<DimensionType> {

    private static final String[] COLUMN_NAMES = {"Name", "Width", "Height"};
    private static final Class[] COLUMN_CLASSES = {String.class, Double.class, Double.class};

    public IDimensionTypeTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DimensionType component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Name
                    return component.getName();
                case 1: // Width
                    return component.getWidth();
                case 2: // Height
                    return component.getHeight();
            }
        }
        return null;
    }
}
