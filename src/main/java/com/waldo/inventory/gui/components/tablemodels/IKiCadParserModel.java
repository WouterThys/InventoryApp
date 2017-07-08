package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.parser.KiCad.KcComponent;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class IKiCadParserModel extends AbstractTableModel {
    private static final String[] columnNames = {"Part", "Value", "Reference"};
    private static final Class[] columnClasses = {String.class, String.class, String.class};

    private List<KcComponent> componentList;

    public IKiCadParserModel() {
        componentList = new ArrayList<>();
    }

    public IKiCadParserModel(List<KcComponent> componentList) {
        this.componentList = componentList;
    }

    public void setComponentList(List<KcComponent> componentList) {
        this.componentList = componentList;
        fireTableDataChanged();
    }

    public List<KcComponent> getComponentList() {
        return componentList;
    }

    public KcComponent getComponentItem(int index) {
        if (index >= 0 && index < componentList.size()) {
            return componentList.get(index);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return componentList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KcComponent component = getComponentItem(rowIndex);
        if (component != null) {
            switch (columnIndex) {
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

    public void removeRow(int row) {
        componentList.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
