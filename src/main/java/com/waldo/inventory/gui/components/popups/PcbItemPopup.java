package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.actions.*;

import javax.swing.*;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class PcbItemPopup extends JPopupMenu {

    private static final ImageIcon linked = imageResource.readImage("Projects.Pcb.Linked");
    private static final ImageIcon ordered = imageResource.readImage("Projects.Pcb.Ordered");
    private static final ImageIcon used = imageResource.readImage("Projects.Pcb.Used");

    protected PcbItemPopup(PcbItemProjectLink itemProjectLink) {
        super();

        init(itemProjectLink);
    }

    public abstract void onEditItem(PcbItemItemLink itemLink);
    public abstract void onPcbItemEdit(PcbItemProjectLink pcbItemProjectLink);

    private void init(final PcbItemProjectLink itemProjectLink) {

        // Actions
        EditAction editAction = new EditAction() {
            @Override
            public void onEdit() {
                if (itemProjectLink.hasMatchedItem()) {
                    PcbItemPopup.this.onEditItem(itemProjectLink.getPcbItemItemLink());
                }
            }
        };

        EditPcbItemAction editPcbItemAction = new EditPcbItemAction() {
            @Override
            public void onEditPcbItem() {
                onPcbItemEdit(itemProjectLink);
            }
        };

       PcbItem pcbItem = itemProjectLink.getPcbItem();
       boolean addHeader = false;
        JPanel iconPanel = new JPanel();
        if (itemProjectLink.hasMatchedItem()) {
            iconPanel.add(new ILabel(linked));
            addHeader = true;
        } else {
            editAction.setEnabled(false);
        }
        if (pcbItem.isOrdered()) {
            iconPanel.add(new ILabel(ordered));
            addHeader = true;
        }
        if (itemProjectLink.isUsed()) {
            iconPanel.add(new ILabel(used));
            addHeader = true;
        }

        if (addHeader) {
            add(iconPanel);
            addSeparator();
        }
        add(editAction);
        addSeparator();
        add(editPcbItemAction);
    }
}

