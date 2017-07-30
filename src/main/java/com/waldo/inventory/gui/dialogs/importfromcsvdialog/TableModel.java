package com.waldo.inventory.gui.dialogs.importfromcsvdialog;

import com.waldo.inventory.gui.components.ILabel;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {

    // Names and classes
    private String[] columnNames;
    private Class[] columnClasses;

    private List<TableObject> objectList;

    public TableModel() {
        columnNames = new String[] {"Check", "Name"};
        columnClasses = new Class[] {ILabel.class, String.class};
        objectList = new ArrayList<>();
    }

    public TableModel(String[] extraNames) {
        columnNames = new String[]{"Check", "Name"};
        columnNames = ArrayUtils.addAll(columnNames, extraNames);
        columnClasses = new Class[] {ILabel.class, String.class};
        Class[] extraClasses = new Class[extraNames.length];
        for (int i = 0; i < extraNames.length; i++) {
            extraClasses[i] = String.class;
        }
        columnClasses = ArrayUtils.addAll(columnClasses, extraClasses);
        objectList = new ArrayList<>();
    }

    public List<TableObject> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<TableObject> objectList) {
        this.objectList = objectList;
        fireTableDataChanged();
    }

    public boolean hasData() {
        return ((objectList != null) && (objectList.size() > 0));
    }

    public TableObject getObject(int index) {
        if (index >= 0 && index < objectList.size()) {
            return objectList.get(index);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return objectList.size();
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

//    @Override
//    public void fireTableDataChanged() {
//        if (getRowCount() > 1) {
//            fireTableChanged(new TableModelEvent(this, //tableModel
//                    0, //firstRow
//                    getRowCount() - 1, //lastRow
//                    TableModelEvent.ALL_COLUMNS, //column
//                    TableModelEvent.UPDATE)); //changeType
//        }
//    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TableObject object = getObject(rowIndex);
        if (object != null) {
            switch (columnIndex) {
                case 0: // Check
                    return object;
                case 1: // Reference
                    return object.getItemReference();
                case 2: // Extra 1
                    if (object.getExtraData().size() > 0) {
                        return object.getExtraData().get(0);
                    } else {
                        return "";
                    }
                case 3: // Extra 2
                    if (object.getExtraData().size() > 1) {
                        return object.getExtraData().get(1);
                    } else {
                        return "";
                    }
                case 4: // Extra 3
                    if (object.getExtraData().size() > 2) {
                        return object.getExtraData().get(2);
                    } else {
                        return "";
                    }
            }
        }
        return null;
    }

    public void removeRow(int row) {
        objectList.remove(row);
        fireTableRowsDeleted(row, row);
    }


}
