package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.Log;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

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
    public boolean hasTableCellRenderer() {
        return true;
    }

    private static final IRenderer renderer = new IRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }


    private static class IRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon infoIcon = imageResource.readImage("Log.InfoS");
        private static final ImageIcon debugIcon = imageResource.readImage("Log.DebugS");
        private static final ImageIcon warnIcon = imageResource.readImage("Log.WarnS");
        private static final ImageIcon errorIcon = imageResource.readImage("Log.ErrorS");
        private static final ImageIcon logIcon = imageResource.readImage("Log.LogS");

        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, infoIcon, "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Log) {
                if (row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                }

                label.updateBackground(c.getBackground(), row, isSelected);
                Log log = (Log) value;
                switch (log.getLogType()) {
                    case Info:
                        label.setIcon(infoIcon);
                        break;
                    case Debug:
                        label.setIcon(debugIcon);
                        break;
                    case Warn:
                        label.setIcon(warnIcon);
                        break;
                    case Error:
                        label.setIcon(errorIcon);
                        break;
                    default:
                        label.setIcon(logIcon);
                        break;
                }

                return label;
            }
            return c;
        }
    }
}
