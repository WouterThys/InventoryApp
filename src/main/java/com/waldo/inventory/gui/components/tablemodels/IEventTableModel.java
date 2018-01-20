package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.database.DbEvent;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITableIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IEventTableModel extends IAbstractTableModel<DbEvent> {

    private static final String[] COLUMN_NAMES = {"", "Name", "Comment", "Type"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class};

    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
    private static final ImageIcon redBall = imageResource.readImage("Ball.red");

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

    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof DbEvent) {
                    if (row == 0) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setMaxWidth(32);
                        tableColumn.setMinWidth(32);
                    }

                    ILabel lbl;
                    DbEvent event = (DbEvent) value;

                    if (event.isEnabled()) {
                        lbl = new ITableIcon(c.getBackground(), row, isSelected, greenBall);
                    } else {
                        lbl = new ITableIcon(c.getBackground(), row, isSelected, redBall);
                    }

                    return lbl;
                }
                return c;
            }
        };
    }
}
