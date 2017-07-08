package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.parser.KiCad.KcComponent;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class IKiCadParserModel extends IAbstractTableModel<KcComponent> {
    private static final String[] COLUMN_NAMES = {"Part", "Value", "Reference"};
    private static final Class[] COLUMN_CLASSES = {String.class, String.class, String.class};

    private List<KcComponent> componentList;

    public IKiCadParserModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public IKiCadParserModel(List<KcComponent> componentList) {
        super(COLUMN_NAMES, COLUMN_CLASSES, componentList);
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KcComponent component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return  component;
                case 0: // LibSource value
                    return component.getLibSource().getPart();
                case 1: // Value
                    return component.getValue();
                case 2: // Reference
                    return component.getRef();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}
