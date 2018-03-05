package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.waldo.inventory.gui.Application.colorResource;
import static com.waldo.inventory.gui.Application.imageResource;

public class IPcbItemModel extends IAbstractTableModel<PcbItemProjectLink> {

    private static final String[] COLUMN_NAMES = {"", "Part", "Reference", "", "", ""};
    private static final String[] COLUMN_TOOLTIPS = {"Parsed", "PCB part or item", "PCB Reference", "Is linked", "Is ordered", "Is used"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, Icon.class, Icon.class, Icon.class};

    private static final ImageIcon linked = imageResource.readImage("Projects.Pcb.Linked");
    private static final ImageIcon ordered = imageResource.readImage("Projects.Pcb.Ordered");
    private static final ImageIcon used = imageResource.readImage("Projects.Pcb.Used");
    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");

    public IPcbItemModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, COLUMN_TOOLTIPS);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItemProjectLink link = getItemAt(rowIndex);

        if (link != null) {
            PcbItem component = link.getPcbItem();
            switch (columnIndex) {
                case -1:
                case 0: // Amount
                case 1: // LibSource value
                    return link;
                case 2: // Reference
                    return link.getReferenceString();
                case 3: // Linked
                    if (link.hasMatchedItem()) {
                        return linked;
                    }
                    break;
                case 4: // Ordered
                    if (component.isOrdered()) {
                        return ordered;
                    }
                    break;
                case 5: // Used
                    // TODO #13
//                    if (link.isUsed()) {
//                        return used;
//                    }
                    break;
            }
        }
        return null;
    }



    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof PcbItemProjectLink) {

                    TableColumn tableColumn = table.getColumnModel().getColumn(column);
                    PcbItemProjectLink projectLink = (PcbItemProjectLink) value;
                    ILabel lbl;
                    if (table.getColumnName(column).equals("Part")) {
                        if (projectLink.hasMatchedItem()) {
                            PcbItemItemLink link = projectLink.getPcbItemItemLink();
                            lbl = new ITableIcon(c.getBackground(), row, isSelected,link.getItem().getPrettyName());
                            lbl.setForeground(colorResource.readColor("Green"));
                            lbl.setFont(Font.BOLD);
                        } else {
                            lbl = new ITableIcon(c.getBackground(), row, isSelected, projectLink.getPrettyName());
                        }
                    } else {
                        if (row == 0) {
                            tableColumn.setMaxWidth(32);
                            tableColumn.setMinWidth(32);
                        }

                        String txt = String.valueOf(projectLink.getNumberOfItems());
                        lbl = new ITableIcon(c.getBackground(), row, isSelected, greenBall, txt);
                    }

                    return lbl;
                }
                if (value instanceof ImageIcon) {
                    return new ITableIcon(c.getBackground(), row, isSelected, (ImageIcon) value);
                }
                return this;
            }
        };
    }
}
