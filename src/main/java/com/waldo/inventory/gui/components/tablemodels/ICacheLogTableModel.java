package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.ObjectLog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Comparator;

import static com.waldo.inventory.gui.Application.imageResource;

public class ICacheLogTableModel extends IAbstractTableModel<ObjectLog> {

    private static final String[] COLUMN_NAMES = {"", "Size", "List", "Fetched"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, Integer.class, String.class, String.class};

    public ICacheLogTableModel(Comparator<? super ObjectLog> comparator) {
        super(COLUMN_NAMES, COLUMN_CLASSES, comparator);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ObjectLog log = getItemAt(rowIndex);
        if (log != null) {
            switch (columnIndex) {
                case -1: // Return object itself
                    return log;
                case 0: // Type label, will be set with with the ITableEditor LogTypeRenderer
                    return log;
                case 1: // Size
                    int size = log.getCacheListSize();
                    if (size >= 0) {
                        return size;
                    }
                    return null;
                case 2: // List name
                    return log.getListName();
                case 3: // Date fetched
                    if (log.getCacheList().isFetched()) {
                        return DateUtils.formatDetailTime(log.getCacheList().getInitialisationTime());
                    }
                    return null;
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

        private static final ImageIcon fetchedIcon = imageResource.readIcon("Ball.green");
        private static final ImageIcon notFetchedIcon = imageResource.readIcon("Ball.red");

        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, fetchedIcon, "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof ObjectLog) {
                if (row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                }
                label.updateBackground(component.getBackground(), row, isSelected);

                ObjectLog log = (ObjectLog) value;
                if (log.getCacheList().isFetched()) {
                    label.setIcon(fetchedIcon);
                } else {
                    label.setIcon(notFetchedIcon);
                }
                return label;
            }
            return component;
        }
    }
}