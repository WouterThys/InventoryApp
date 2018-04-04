package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ILinkItemTableModel extends IAbstractTableModel<PcbItemItemLink> {

    private static final String[] COLUMN_HEADER_TOOLTIPS = {null, "Item name", "Matches name", "Matches value", "Matches footprint"};
    private static final String[] COLUMN_NAMES = {"", "Name", "N", "V", "FP"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, Boolean.class, Boolean.class, Boolean.class};

    public ILinkItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, COLUMN_HEADER_TOOLTIPS);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItemItemLink match = getItemAt(rowIndex);

        if (match != null) {
            switch (columnIndex) {
                case -1:
                    return match;
                case 0: // Amount
                    return match;
                case 1: // Name
                    return match.toString();
                case 2: // Match name
                    return match.hasNameMatch();
                case 3: // Value match
                    return match.hasValueMatch();
                case 4: // Footprint match
                    return match.hasFootprintMatch();
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
        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, greenBall, "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof PcbItemItemLink) {
                if (row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                }

                PcbItemItemLink link = (PcbItemItemLink) value;
                int amount = link.getItem().getAmount();

                label.setText(String.valueOf(amount));
                label.updateBackground(c.getBackground(), row, isSelected);

                if (amount > 0) {
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