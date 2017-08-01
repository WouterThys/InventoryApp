package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.gui.components.ILabel;

public class ILinkKiCadTableModel extends IAbstractTableModel<KcComponent> {
    private static final String[] COLUMN_NAMES = {"", "Part", "Value", "M"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, Boolean.class};

    public ILinkKiCadTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KcComponent component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Amount
                    return component;
                case 1: // LibSource value
                    return component.getLibSource().getPart();
                case 2: // Value
                    return component.getValue();
                case 3:
                    return component.hasMatch();
            }
        }
        return null;
    }
}
