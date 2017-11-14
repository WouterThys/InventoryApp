package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.PackageType;

public class IPackageTypeTableModel extends IAbstractTableModel<PackageType> {
    private static final String[] COLUMN_NAMES = {"Name", "Pins", "Description"};
    private static final Class[] COLUMN_CLASSES = {String.class, Integer.class, String.class};

    public IPackageTypeTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PackageType type = getItemAt(rowIndex);
        if (type != null) {
            switch (columnIndex) {
                case -1:
                    return type;
                case 0: // Name
                    return type.toString();
                case 1: // Pins
                    return type.getDefaultPins();
                case 2: // Description
                    return type.getDescription();
            }
        }
        return null;
    }
}
