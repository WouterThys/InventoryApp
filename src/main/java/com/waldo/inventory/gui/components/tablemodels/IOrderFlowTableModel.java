package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DistributorOrderFlow;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IOrderFlowTableModel extends IAbstractTableModel<DistributorOrderFlow> {

    private static final String[] COLUMN_NAMES = {"", "Name", "State", "Description"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, ILabel.class, String.class};

    public IOrderFlowTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, new ComparatorUtils.DistributorOrderFlowComparator());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DistributorOrderFlow flow = getItemAt(rowIndex);
        if (flow != null) {
            switch (columnIndex) {
                case -1:
                    return flow;
                case 0: // Sequence
                    return flow;
                case 1: // Name
                    return flow.toString();
                case 2: // State
                    return flow.getOrderState();
                case 3: // Description
                    return flow.getDescription();
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
        private static final ImageIcon receivedLbl = imageResource.readIcon("Orders.Table.Received");
        private static final ImageIcon orderedLbl = imageResource.readIcon("Orders.Table.Ordered");
        private static final ImageIcon plannedLbl = imageResource.readIcon("Orders.Table.Planned");

        private static final ITableLabel sequenceLbl = new ITableLabel(Color.gray, 0, false, greenBall, "");
        private static final ITableLabel stateLbl = new ITableLabel(Color.gray, 0, false, plannedLbl, "");

        boolean done = false;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof DistributorOrderFlow) {
                if (row == 0 && !done) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(0);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                    tableColumn = table.getColumnModel().getColumn(2);
                    tableColumn.setMaxWidth(64);
                    tableColumn.setMinWidth(64);
                    done = true;
                }

                DistributorOrderFlow flow = (DistributorOrderFlow) value;

                sequenceLbl.updateBackground(c.getBackground(), row, isSelected);
                sequenceLbl.setText(String.valueOf(flow.getSequenceNumber()));

                return sequenceLbl;
            }

            if (value instanceof Statics.OrderStates) {
                Statics.OrderStates state = (Statics.OrderStates) value;
                stateLbl.updateBackground(c.getBackground(), row, isSelected);
                stateLbl.setToolTipText(state.toString());
                switch (state) {
                    case NoOrder:
                        stateLbl.setIcon(null);
                        break;
                    case Planned:
                        stateLbl.setIcon(plannedLbl);
                        break;
                    case Ordered:
                        stateLbl.setIcon(orderedLbl);
                        break;
                    case Received:
                        stateLbl.setIcon(receivedLbl);
                        break;
                }
                return stateLbl;
            }

            return c;
        }
    }
}
