package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITableIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ILinkedItemsTableModel extends IAbstractTableModel<PcbItemItemLink> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Manufacturer"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class};

    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
    private static final ImageIcon redBall = imageResource.readImage("Ball.red");
    private static final ImageIcon yellowBall = imageResource.readImage("Ball.yellow");
    private static final ImageIcon blueBall = imageResource.readImage("Ball.blue");

    public ILinkedItemsTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItemItemLink link = getItemAt(rowIndex);

        if (link != null) {
            switch (columnIndex) {
                case -1:
                    return link;
                case 0: // Amount label
                    return link;
                case 1: // Name
                    return link.getLinkedItemName();
                case 2: // Manufacturer
                    Manufacturer m = link.getItem().getManufacturer();
                    if (m != null && !m.isUnknown()) {
                        return m.toString();
                    }
                    return "";
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
                if (value instanceof PcbItemItemLink) {
                    if (row == 0) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setMaxWidth(32);
                        tableColumn.setMinWidth(32);
                    }

                    ILabel lbl;

                    int amount;
                    Statics.ItemOrderStates orderState;
                    Item item;

                    PcbItemItemLink link = (PcbItemItemLink) value;
                    item = link.getItem();
                    orderState = item.getOrderState();
                    amount = item.getAmount();


                    if (orderState == Statics.ItemOrderStates.Ordered) {
                        lbl = new ITableIcon(component.getBackground(), row, isSelected, blueBall, String.valueOf(amount));
                    } else if (orderState== Statics.ItemOrderStates.Planned) {
                        lbl = new ITableIcon(component.getBackground(), row, isSelected, yellowBall, String.valueOf(amount));
                    } else {
                        if (amount > 0) {
                            lbl = new ITableIcon(component.getBackground(), row, isSelected, greenBall, String.valueOf(amount));
                        } else {
                            lbl = new ITableIcon(component.getBackground(), row, isSelected, redBall, String.valueOf(amount));
                        }
                    }

                    return lbl;
                }
                return component;
            }
        };
    }

}
