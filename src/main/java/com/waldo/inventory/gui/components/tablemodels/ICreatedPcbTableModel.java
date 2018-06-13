package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ICreatedPcbTableModel extends IAbstractTableModel<CreatedPcb> {

    private static final String[] COLUMN_NAMES = {"", "Name", "Order", "Created"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, ILabel.class, ILabel.class, ILabel.class};

    public ICreatedPcbTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CreatedPcb pcb = getItemAt(rowIndex);
        if (pcb != null) {
            switch (columnIndex) {
                case -1:
                    return pcb;
                case 0: // State
                    return pcb;
                case 1: // Name
                    return pcb.toString();
                case 2: // Order
                    if (pcb.getOrderId() > DbObject.UNKNOWN_ID) {
                        return pcb.getOrder().toString();
                    }
                    return "";
                case 3: // Date
                    return DateUtils.formatDate(pcb.getDateCreated());
            }
        }
        return null;
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

        private static final ImageIcon redBall = imageResource.readIcon("Ball.red");;
        private static final ImageIcon receivedIcon = imageResource.readIcon("Actions.Received");
        private static final ImageIcon inProgressIcon = imageResource.readIcon("Actions.Created");
        private static final ImageIcon doneIcon = imageResource.readIcon("Actions.Ok");
        private static final ImageIcon destroyedIcon = imageResource.readIcon("Actions.Destroyed");

        private static final ITableLabel stateLabel = new ITableLabel(Color.gray, 0, false, receivedIcon);

        private boolean done = false;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof CreatedPcb) {
                if (!done && row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                    done = true;
                }

                CreatedPcb pcb = (CreatedPcb) value;
                if (pcb.isCreated()) {

                    if (pcb.isDestroyed()) {
                        stateLabel.setIcon(destroyedIcon);
                        stateLabel.setToolTipText("Destroyed PCB: " + DateUtils.formatDateTime(pcb.getDateDestroyed()));
                    } else {
                        int total = pcb.getAmountOfSolderItems();
                        int done = pcb.getAmountDone();

                        if (done == 0) {
                            stateLabel.setIcon(receivedIcon);
                            stateLabel.setToolTipText("Received PCB: " + DateUtils.formatDateTime(pcb.getDateCreated()));
                        } else if (done < total) {
                            stateLabel.setIcon(inProgressIcon);
                            stateLabel.setToolTipText("In progress: " + DateUtils.formatDateTime(pcb.getDateSoldered()));
                        } else {
                            stateLabel.setIcon(doneIcon);
                            stateLabel.setToolTipText("Done: " + DateUtils.formatDateTime(pcb.getDateSoldered()));
                        }
                    }
                } else {
                    stateLabel.setIcon(redBall);
                    stateLabel.setToolTipText(null);
                }
                stateLabel.updateWithTableComponent(component, row, isSelected);

                return stateLabel;
            }

            return component;
        }
    }

}
