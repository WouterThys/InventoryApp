package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.components.ILabel;

import javax.swing.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IPcbItemModel extends IAbstractTableModel<PcbItem> {

    private static final String[] COLUMN_NAMES = {"", "Part", "Reference", "", "", ""};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, ImageIcon.class, ImageIcon.class, ImageIcon.class};

    private static final ImageIcon linked = imageResource.readImage("Projects.Pcb.Linked");
    private static final ImageIcon ordered = imageResource.readImage("Projects.Pcb.Ordered");
    private static final ImageIcon used = imageResource.readImage("Projects.Pcb.Used");

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
}
