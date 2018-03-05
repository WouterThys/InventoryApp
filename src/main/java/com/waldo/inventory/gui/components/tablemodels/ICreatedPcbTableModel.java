package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.CreatedPcbLink;
import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableBallPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class ICreatedPcbTableModel extends IAbstractTableModel<CreatedPcbLink> {

    private static final String[] COLUMN_NAMES = {"PCB item", "Linked item", "Used item", "Used amount"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, ILabel.class, ILabel.class, Integer.class};

    public ICreatedPcbTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CreatedPcbLink link = getItemAt(rowIndex);
        if (link != null) {
            switch (columnIndex) {
                case -1:
                    return link;
                case 0: // PCB item
                    return link.getPcbItem();
                case 1: // Linked item
                    PcbItemItemLink itemLink = link.getPcbItemItemLink();
                    if (itemLink != null) {
                        return itemLink.getItem();
                    } else {
                        return null;
                    }
                case 2: // Used item
                    return link.getUsedItem();
                case 3:
                    return link.getUsedAmount();
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

        private static final ImageIcon greenBall = imageResource.readImage("Ball.green");
        private static final ImageIcon redBall = imageResource.readImage("Ball.red");
        private static final ImageIcon yellowBall = imageResource.readImage("Ball.yellow");
        private static final ImageIcon blueBall = imageResource.readImage("Ball.blue");

        private static final ITableBallPanel panel = new ITableBallPanel(Color.gray, 0, false, greenBall, "", "");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof PcbItem) {
                PcbItem pcbItem = (PcbItem) value;

                panel.updateBallIcon(blueBall);
                panel.updateBallText("1");
                panel.updateBackground(component.getBackground(), row, isSelected);
                panel.setText(pcbItem.toString());

                return panel;
            }

            return component;
        }
    }

}

