package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.parser.KiCad.KcComponent;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class IKiCadParserModel extends IAbstractTableModel<KcComponent> {
    private static final String[] COLUMN_NAMES = {"Amount", "Part", "Value", "Reference"};
    private static final Class[] COLUMN_CLASSES = {Integer.class, String.class, String.class, String.class};

    public IKiCadParserModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KcComponent component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return  component;
                case 0: // Amount
                    return component.getReferences().size();
                case 1: // LibSource value
                    return component.getLibSource().getPart();
                case 2: // Value
                    return component.getValue();
                case 3: // Reference
                    return component.getReferenceString();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}
