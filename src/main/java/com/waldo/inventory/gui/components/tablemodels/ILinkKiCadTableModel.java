package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.components.ILabel;

public class ILinkKiCadTableModel extends IAbstractTableModel<PcbItem> {
    private static final String[] COLUMN_NAMES = {"", "Part", "Value", "M"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, Boolean.class};

    public static final int LINK_COMPONENTS = 0;
    public static final int ORDER_COMPONENTS = 1;

    private int type;

    public ILinkKiCadTableModel(int type) {
        super(COLUMN_NAMES, COLUMN_CLASSES);

        this.type = type;
        if (type == ORDER_COMPONENTS) {
            setColumnName(3, "O");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItem component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Amount
                    return component;
                case 1: // LibSource value
                    return component.getPartName();
                case 2: // Value
                    return component.getValue();
                case 3:
                    if (type == LINK_COMPONENTS) {
                        return component.hasMatch();
                    } else {
                        return component.isOrdered();
                    }
            }
        }
        return null;
    }
}
