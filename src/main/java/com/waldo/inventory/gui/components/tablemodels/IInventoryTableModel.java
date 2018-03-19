package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ITableBallPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IInventoryTableModel extends IAbstractTableModel<Item> {

    private static final String[] COLUMN_NAME = {"Item", "Amount"};
    private static final Class[] COLUMN_CLASSES = {String.class, Integer.class};

    public IInventoryTableModel() {
        super(COLUMN_NAME, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = getItemAt(rowIndex);
        if (item != null) {
            switch (columnIndex) {
                case -1: // Reference to object itself
                    return item;
                case 0: // Item
                    return item;
                case 1: // Amount
                    return item.getAmount();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 1); // Amount is editable
    }

    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    private final CreatedPcbCellRenderer renderer = new CreatedPcbCellRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    static class CreatedPcbCellRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
        private static final ImageIcon redBall = imageResource.readImage("Ball.red");
        private static final ImageIcon yellowBall = imageResource.readImage("Ball.yellow");
        private static final ImageIcon blueBall = imageResource.readImage("Ball.blue");

        private static final ITableBallPanel itemPanel = new ITableBallPanel(Color.gray, 0, false, greenBall, "", "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof Item) {
                Item item = (Item) value;

                String txt = String.valueOf(item.getAmount());
                itemPanel.updateWithTableComponent(component, row, isSelected);
                itemPanel.updateBallText(txt);
                itemPanel.setText(item.toString());

                if (item.getOrderState() == Statics.ItemOrderStates.Ordered) {
                    itemPanel.updateBallIcon(blueBall);
                } else if (item.getOrderState() == Statics.ItemOrderStates.Planned) {
                    itemPanel.updateBallIcon(yellowBall);
                } else {
                    if (item.getAmount() > 0) {
                        itemPanel.updateBallIcon(greenBall);
                    } else {
                        itemPanel.updateBallIcon(redBall);
                    }
                }

                return itemPanel;
            }

            return component;
        }
    }
}
