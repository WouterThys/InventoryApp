package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.classes.dbclasses.Log;
import com.waldo.inventory.gui.components.ILabel;

public class ISystemLogTableModel extends IAbstractTableModel<Log> {

    private static final String[] COLUMN_NAMES = {"", "Time", "Class", "Message"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class};

    public ISystemLogTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Log log = getItemAt(rowIndex);
        if (log != null) {
            switch (columnIndex) {
                case -1: // Return object itself
                    return log;
                case 0: // Type label, will be set with with the ITableEditor LogTypeRenderer
                    return log;
                case 1: // Time
                    return DateUtils.formatTime(log.getLogTime());
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
