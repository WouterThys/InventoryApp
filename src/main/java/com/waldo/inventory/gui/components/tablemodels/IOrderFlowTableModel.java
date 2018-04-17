package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.ComparatorUtils;
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
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class};

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
                    return flow.getOrderState().toString();
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
        private static final ITableLabel label = new ITableLabel(Color.gray, 0, false, greenBall, "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof DistributorOrderFlow) {
                if (row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                }

                DistributorOrderFlow flow = (DistributorOrderFlow) value;

                label.updateBackground(c.getBackground(), row, isSelected);
                label.setText(String.valueOf(flow.getSequenceNumber()));

                return label;
            }
            return c;
        }
    }
}
