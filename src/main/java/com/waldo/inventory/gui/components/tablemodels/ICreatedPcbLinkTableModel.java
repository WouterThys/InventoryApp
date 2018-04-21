package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.CreatedPcbLinkState;
import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableBallPanel;
import com.waldo.utils.icomponents.ITableLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ICreatedPcbLinkTableModel extends IAbstractTableModel<CreatedPcbLink> {

    private static final String[] COLUMN_NAMES = {"", "PCB item", "Linked item", "Used item", "Used"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, ILabel.class, ILabel.class, ILabel.class, String.class};

    public ICreatedPcbLinkTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CreatedPcbLink link = getItemAt(rowIndex);
        if (link != null) {
            switch (columnIndex) {
                case -1:
                    return link;
                case 0: // State
                    return link.getState();
                case 1: // PCB item
                    return link.getPcbItemProjectLink();
                case 2: // Linked item
                    return link.getPcbItemItemLink();
                case 3: // Used item
                    return link.getUsedItem();
                case 4:
                    if (link.getUsedAmount() >= 0) {
                        return String.valueOf(link.getUsedAmount());
                    } else {
                        return "/";
                    }
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

        private static final ImageIcon greenBall = imageResource.readIcon("Ball.green");
        private static final ImageIcon redBall = imageResource.readIcon("Ball.red");
        private static final ImageIcon yellowBall = imageResource.readIcon("Ball.yellow");
        private static final ImageIcon blueBall = imageResource.readIcon("Ball.blue");

        private static final ITableLabel stateLabel = new ITableLabel(Color.gray, 0, false, greenBall);
        private static final ITableBallPanel pcbPanel = new ITableBallPanel(Color.gray, 0, false, greenBall, "", "");
        private static final ITableBallPanel itemPanel = new ITableBallPanel(Color.gray, 0, false, greenBall, "", "");

        private boolean done = false;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof CreatedPcbLinkState) {
                if (!done && row == 0) {
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    tableColumn.setMaxWidth(32);
                    tableColumn.setMinWidth(32);
                    done = true;
                }

                CreatedPcbLinkState state = (CreatedPcbLinkState) value;
                stateLabel.updateWithTableComponent(component, row, isSelected);
                stateLabel.setIcon(state.getImageIcon());

                return stateLabel;
            }

            if (value instanceof PcbItemProjectLink) {
                PcbItemProjectLink pcbItemLink = (PcbItemProjectLink) value;

                pcbPanel.updateBallIcon(blueBall);
                pcbPanel.updateBallText(String.valueOf(pcbItemLink.getNumberOfItems()));
                pcbPanel.updateWithTableComponent(component, row, isSelected);
                pcbPanel.setText(pcbItemLink.getPrettyName());
                return pcbPanel;
            }

            if (value instanceof PcbItemItemLink) {
                Item item = ((PcbItemItemLink)value).getItem();

                String txt = String.valueOf(item.getAmount());
                itemPanel.updateWithTableComponent(component, row, isSelected);
                itemPanel.updateBallText(txt);
                itemPanel.setText(item.toString());

                if (item.getOrderState() == Statics.OrderStates.Ordered) {
                    itemPanel.updateBallIcon(blueBall);
                } else if (item.getOrderState() == Statics.OrderStates.Planned) {
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

