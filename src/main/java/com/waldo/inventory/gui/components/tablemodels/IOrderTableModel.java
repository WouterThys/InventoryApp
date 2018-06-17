package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IOrderTableModel extends IAbstractTableModel<AbstractOrder> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Distributor", "Name", "Modified", "Ordered", "Received"};
    private static final String[] TOOLTIPS = {"State", "Ordered by", "Order name", "Date modified", "Date ordered", "Date received"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class, String.class, String.class};

    public IOrderTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, TOOLTIPS);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AbstractOrder order = getItemAt(rowIndex);

        if (order != null) {
            switch (columnIndex) {
                case -1:
                    return order;
                case 0: // State label
                    return order.getOrderState();
                case 1: // Distributor
                    if (order.getDistributor() != null) {
                        return order.getDistributor();
                    }
                    return "";
                case 2: // Name
                    return order.toString();
                case 3: // Modified
                    return DateUtils.formatDate(order.getDateModified());
                case 4: // Ordered
                    return DateUtils.formatDate(order.getDateOrdered());
                case 5: // Received
                    return DateUtils.formatDate(order.getDateReceived());

            }
        }
        return null;
    }


    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    private static final OrderTableRenderer renderer = new OrderTableRenderer();
    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return renderer;
    }

    static class OrderTableRenderer extends DefaultTableCellRenderer {

        private static final ImageIcon planned = imageResource.readIcon("Orders.Table.Planned");
        private static final ImageIcon ordered = imageResource.readIcon("Orders.Table.Ordered");
        private static final ImageIcon received = imageResource.readIcon("Orders.Table.Received");

        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, planned, "");


        private boolean done = false;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!done && row == 0) {
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                tableColumn.setMaxWidth(32);
                tableColumn.setMinWidth(32);
                done = true;
            }

            if (value instanceof Statics.OrderStates) {
                label.updateWithTableComponent(component, row, isSelected);
                switch ((Statics.OrderStates) value) {
                    case Planned:
                        label.setIcon(planned);
                        break;
                    case Ordered:
                        label.setIcon(ordered);
                        break;
                    case Received:
                        label.setIcon(received);
                        break;

                        default:
                            break;
                }
                return label;
            }

            return component;
        }
    }

}

