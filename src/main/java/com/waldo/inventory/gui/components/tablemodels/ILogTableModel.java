package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.Log;
import com.waldo.inventory.gui.components.ILabel;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ILogTableModel extends IAbstractTableModel<Log> {

    private static final String[] COLUMN_NAMES = {"", "Time", "Class", "Message"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class};

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private List<Log> logList;

    public ILogTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public ILogTableModel(List<Log> logList) {
        super(COLUMN_NAMES, COLUMN_CLASSES, logList);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Log log = getItemAt(rowIndex);
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

}
