package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITableIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Comparator;

import static com.waldo.inventory.gui.Application.imageResource;

public class ISetItemTableModel extends IAbstractTableModel<Item> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Value", "Location"};
    private static final Class[] COLUMN_CLASSES = {ITableIcon.class, String.class, String.class, String.class};

    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
    private static final ImageIcon redBall = imageResource.readImage("Ball.red");

    public ISetItemTableModel(Comparator<Item> comparator) {
        super(COLUMN_NAMES, COLUMN_CLASSES, comparator);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item setItem = getItemAt(rowIndex);

        if (setItem != null) {
            switch (columnIndex) {
                case -1:
                    return setItem;
                case 0: // Amount label
                    return setItem;
                case 1: // Name
                    return setItem.toString();
                case 2: // Value
                    return setItem.getValue();
                case 3: // Locations
                    if (setItem.getLocation() != null) {
                        return setItem.getLocation().getPrettyString();
                    }
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
                if (value instanceof Item) {
                    if (row == 0) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setMaxWidth(32);
                        tableColumn.setMinWidth(32);
                    }

                    ILabel lbl;
                    Item setItem = (Item) value;
                    int amount = setItem.getAmount();

                    if (amount > 0) {
                        lbl = new ITableIcon(c.getBackground(), row, isSelected, greenBall, String.valueOf(amount));
                    } else {
                        lbl = new ITableIcon(c.getBackground(), row, isSelected, redBall, String.valueOf(amount));
                    }

                    return lbl;
                }
                return c;
            }
        };
    }
}
