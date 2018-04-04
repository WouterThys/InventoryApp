package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ILinkedItemsTableModel extends IAbstractTableModel<PcbItemItemLink> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Manufacturer"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class};

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

    private static final IRenderer renderer = new IRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    private static class IRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon greenBall = imageResource.readIcon("Ball.green");
        private static final ImageIcon redBall = imageResource.readIcon("Ball.red");
        private static final ImageIcon yellowBall = imageResource.readIcon("Ball.yellow");
        private static final ImageIcon blueBall = imageResource.readIcon("Ball.blue");

        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, greenBall, "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof PcbItemItemLink) {
                if (row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                }

                int amount;
                Statics.ItemOrderStates orderState;
                Item item;

                PcbItemItemLink link = (PcbItemItemLink) value;
                item = link.getItem();
                orderState = item.getOrderState();
                amount = item.getAmount();

                label.updateBackground(component.getBackground(), row, isSelected);
                label.setText(String.valueOf(amount));

                if (orderState == Statics.ItemOrderStates.Ordered) {
                    label.setIcon(blueBall);
                } else if (orderState== Statics.ItemOrderStates.Planned) {
                    label.setIcon(yellowBall);
                } else {
                    if (amount > 0) {
                        label.setIcon(greenBall);
                    } else {
                        label.setIcon(redBall);
                    }
                }

                return label;
            }
            return component;
        }
    }

}
