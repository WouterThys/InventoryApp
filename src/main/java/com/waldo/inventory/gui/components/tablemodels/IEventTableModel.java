package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.database.DbEvent;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IEventTableModel extends IAbstractTableModel<DbEvent> {

    private static final String[] COLUMN_NAMES = {"", "Name", "Comment", "Type"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class};

    public IEventTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DbEvent event = getItemAt(rowIndex);

        if (event != null) {
            switch (columnIndex) {
                case -1:
                    return event;
                case 0: // Enabled
                    return event;
                case 1: // Name
                    return event.toString();
                case 2: // Comment
                    return event.getComment();
                case 3: // Type
                    return event.getType().toString();
            }
        }
        return null;
    }

    private static final IRenderer renderer = new IRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    private static class IRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon greenBall = imageResource.readIcon("Ball.green");
        private static final ImageIcon redBall = imageResource.readIcon("Ball.red");

        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, greenBall, "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof DbEvent) {
                if (row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                }

                label.updateBackground(c.getBackground(), row, isSelected);
                DbEvent event = (DbEvent) value;

                if (event.isEnabled()) {
                    label.setIcon(greenBall);
                } else {
                    label.setIcon(redBall);
                }

                return label;
            }
            return c;
        }
    }
}
