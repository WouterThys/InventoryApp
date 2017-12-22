package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITableIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ILinkPcbItemTableModel extends IAbstractTableModel<PcbItem> {
    private static final String[] COLUMN_NAMES = {"", "Part", "Value", "M"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, Boolean.class};

    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");

    public static final int LINK_COMPONENTS = 0;
    public static final int ORDER_COMPONENTS = 1;

    private final int type;

    public ILinkPcbItemTableModel(int type) {
        super(COLUMN_NAMES, COLUMN_CLASSES);

        this.type = type;
        if (type == ORDER_COMPONENTS) {
            columnNames[3] = "O";
            fireTableStructureChanged();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItem component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Amount
                    return component;
                case 1: // LibSource value
                    return component.getPartName();
                case 2: // Value
                    return component.getValue();
                case 3:
                    if (type == LINK_COMPONENTS) {
                        return null; // TODO #24 component.hasMatchedItem();
                    } else {
                        return component.isOrdered();
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
                if (value instanceof PcbItem) {
                    if (row == 0) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setMaxWidth(32);
                        tableColumn.setMinWidth(32);
                    }
                    return new ITableIcon(c.getBackground(), row, isSelected, greenBall);
                }
                return c;
            }
        };
    }
}
