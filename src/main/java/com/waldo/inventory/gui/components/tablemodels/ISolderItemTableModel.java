package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.SolderItem;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableBallPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ISolderItemTableModel extends IAbstractTableModel<SolderItem> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Ref", "Used"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, ILabel.class};

    public ISolderItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SolderItem solderItem = getItemAt(rowIndex);

        if (solderItem != null) {
            switch (columnIndex) {
                case -1:
                    return solderItem;
                case 0: // State
                    return solderItem.getState();
                case 1: // Reference
                    return solderItem.toString();
                case 2: // Used item
                    return solderItem.getUsedItem();
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
        private static final ITableBallPanel itemLabel = new ITableBallPanel(Color.gray, 0, false, greenBall, "", "");

        private boolean first = true;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (first) {
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                tableColumn.setMaxWidth(32);
                tableColumn.setMinWidth(32);
                first = false;
            }

            // State
            if (value instanceof Statics.SolderItemState) {
                Statics.SolderItemState state = (Statics.SolderItemState) value;

                // Create icons for state

            }

            // Used item
            if (value instanceof Item) {

                Item item = (Item) value;
                int amount = item.getAmount();
                itemLabel.updateWithTableComponent(c, row, isSelected);
                itemLabel.setText(item.toString());
                itemLabel.updateBallText(String.valueOf(amount));
                if (amount > item.getMinimum()) {
                    itemLabel.updateBallIcon(greenBall);
                } else {
                    itemLabel.updateBallIcon(redBall);
                }

                return itemLabel;
            }
            return c;
        }
    }
}

