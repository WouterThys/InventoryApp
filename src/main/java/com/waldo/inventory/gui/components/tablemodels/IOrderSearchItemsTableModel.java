package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.gui.components.wrappers.SelectableTableItem;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableBallPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IOrderSearchItemsTableModel extends IAbstractTableModel<SelectableTableItem> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Description", "Manufacturer"};
    private static final Class[] COLUMN_CLASSES = {Boolean.class, ILabel.class, String.class, String.class};

    public IOrderSearchItemsTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SelectableTableItem item = getItemAt(rowIndex);

        if (item != null) {
            switch (columnIndex) {
                case -1:
                    return item;
                case 0: // Select
                    return item.isSelected();
                case 1: // Name
                    return item;
                case 2: // Description
                    return item.getDescription();
                case 3: // Manufacturer
                    Manufacturer m = item.getManufacturer();
                    if (m != null && !m.isUnknown()) {
                        return m.toString();
                    }
                    return "";
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (isCellEditable(rowIndex, columnIndex)) {
            if (aValue instanceof Boolean) {
                SelectableTableItem item = (SelectableTableItem) getValueAt(rowIndex, -1);
                item.setSelected((Boolean) aValue);
            }
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    private static final ItemTableRenderer renderer = new ItemTableRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    static class ItemTableRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon greenBall = imageResource.readIcon("Ball.green");
        private static final ImageIcon redBall = imageResource.readIcon("Ball.red");
        private static final ImageIcon yellowBall = imageResource.readIcon("Ball.yellow");
        private static final ImageIcon blueBall = imageResource.readIcon("Ball.blue");

        //private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, greenBall, "");
        private static final ITableBallPanel namePnl = new ITableBallPanel(Color.gray, 0, false, greenBall, "1", "Name");


        private boolean done = false;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!done) {
                TableColumn tableColumn = table.getColumnModel().getColumn(0);
                tableColumn.setMaxWidth(32);
                tableColumn.setMinWidth(32);
                done = true;
            }

            if (value instanceof SelectableTableItem) {

                SelectableTableItem item = (SelectableTableItem) value;
                String txt = String.valueOf(item.getAmount());
                namePnl.updateBackground(component.getBackground(), row, isSelected);
                namePnl.setText(item.getItem().toString());
                namePnl.updateBallText(txt);

                if (item.getOrderState() == Statics.OrderStates.Ordered) {
                    namePnl.updateBallIcon(blueBall);
                } else if (item.getOrderState() == Statics.OrderStates.Planned) {
                    namePnl.updateBallIcon(yellowBall);
                } else {
                    if (item.getAmount() >= item.getMinimum()) {
                        namePnl.updateBallIcon(greenBall);
                    } else {
                        namePnl.updateBallIcon(redBall);
                    }
                }

                return namePnl;
            }
            return component;
        }
    }

}
