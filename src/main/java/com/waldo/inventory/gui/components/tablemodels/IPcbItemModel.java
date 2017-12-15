package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.components.ILabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IPcbItemModel extends IAbstractTableModel<PcbItem> {

    private static final String[] COLUMN_NAMES = {"", "Part", "Reference", "", "", ""};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, ImageIcon.class, ImageIcon.class, ImageIcon.class};

    private static final ImageIcon linked = imageResource.readImage("Projects.Pcb.Linked");
    private static final ImageIcon ordered = imageResource.readImage("Projects.Pcb.Ordered");
    private static final ImageIcon used = imageResource.readImage("Projects.Pcb.Used");
    private static final ImageIcon greenBall = imageResource.readImage("Ball.green");

    public interface PcbItemListener {
        PcbItemProjectLink onGetProjectLink(PcbItem pcbItem);
    }

    private PcbItemListener pcbItemListener;

    public IPcbItemModel(PcbItemListener itemListener) {
        super(COLUMN_NAMES, COLUMN_CLASSES);
        this.pcbItemListener = itemListener;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PcbItem component = getItemAt(rowIndex);
        if (component != null) {
            switch (columnIndex) {
                case -1:
                    return component;
                case 0: // Amount
                    return component;
                case 1: // LibSource value
                    String part = component.getPartName();
                    String value = component.getValue();
                    if (part.equals(value)) {
                        return part;
                    } else {
                        return part + " - " + value;
                    }
                case 2: // Reference
                    return component.getReferenceString();
                case 3: // Linked
                    if (component.hasMatch()) {
                        return linked;
                    }
                    break;
                case 4: // Ordered
                    if (component.isOrdered()) {
                        return ordered;
                    }
                    break;
                case 5: // Used
                    PcbItemProjectLink link = pcbItemListener.onGetProjectLink(component);
                    if (link != null && link.isUsed()) {
                        return used;
                    }
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
                if (value instanceof PcbItem) {
                    if (row == 0) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        tableColumn.setMaxWidth(32);
                        tableColumn.setMinWidth(32);
                    }

                    ILabel lbl;
                    PcbItem pcbItem = (PcbItem) value;

                    String txt = String.valueOf(pcbItem.getReferences().size());
                    lbl = GuiUtils.getTableIconLabel(c.getBackground(), row, isSelected, greenBall, txt);

                    return lbl;
                }
                return c;
            }
        };
    }
}
