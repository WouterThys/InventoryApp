package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITableIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IFoundItemsTableModel extends IAbstractTableModel<Item> {

    private static final String[] COLUMN_NAMES = {"", "Name"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class};

    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
    private static final ImageIcon redBall = imageResource.readImage("Ball.red");
    private static final ImageIcon yellowBall = imageResource.readImage("Ball.yellow");
    private static final ImageIcon blueBall = imageResource.readImage("Ball.blue");

    public IFoundItemsTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = getItemAt(rowIndex);

        if (item != null) {
            switch (columnIndex) {
                case -1:
                    return item;
                case 0: // Amount label
                    return item;
                case 1: // Name
                    return item.toString();
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
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Item) {
                    if (row == 0) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setMaxWidth(32);
                        tableColumn.setMinWidth(32);
                    }

                    ILabel lbl;
                    Item item = (Item) value;
                    String txt = String.valueOf(item.getAmount());

                    if (item.getOrderState() == Statics.ItemOrderStates.ORDERED) {
                        lbl = new ITableIcon(component.getBackground(), row, isSelected, blueBall, txt);
                    } else if (item.getOrderState() == Statics.ItemOrderStates.PLANNED) {
                        lbl = new ITableIcon(component.getBackground(), row, isSelected, yellowBall, txt);
                    } else {
                        if (item.getAmount() > 0) {
                            lbl = new ITableIcon(component.getBackground(), row, isSelected, greenBall, txt);
                        } else {
                            lbl = new ITableIcon(component.getBackground(), row, isSelected, redBall, txt);
                        }
                    }

                    return lbl;
                }
                return component;
            }
        };
    }

}
