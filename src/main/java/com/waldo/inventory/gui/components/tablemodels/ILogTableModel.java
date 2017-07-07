package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.Log;
import com.waldo.inventory.gui.components.ILabel;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ILogTableModel extends AbstractTableModel {

    private static final String[] columnNames = {"", "Time", "Class", "Message"};
    private static final Class[] columnClasses = {ILabel.class, String.class, String.class, String.class};

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private List<Log> logList;

    public ILogTableModel() {
        logList = new ArrayList<>();
    }

    public ILogTableModel(List<Log> logList) {
        this.logList = logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
        fireTableDataChanged();
    }

    public List<Log> getLogList() {
        return logList;
    }

    public Log getLogItem(int index) {
        if (index >= 0 && index < logList.size()) {
            return logList.get(index);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return logList.size();
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
        Log log = getLogItem(rowIndex);
        if (log != null) {
            switch (columnIndex) {
                case 0: // Type label, will be set with with the ITableEditor LogTypeRenderer
                    return log;
                case 1: // Time
                    return sdf.format(log.getLogTime());
                case 2: // Class
                    return log.getLogClass();
                case 3: // Message
                    return log.getLogMessage();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void removeRow(int row) {
        logList.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
