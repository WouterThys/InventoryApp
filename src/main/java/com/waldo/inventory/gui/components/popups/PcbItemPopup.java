package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
        IActions.EditAction editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemProjectLink.hasMatchedItem()) {
                    onEditItem(itemProjectLink.getPcbItemItemLink());
                }
            }
        };
        editAction.setName("Edit item");

        IActions.EditAction editPcbItemAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPcbItemEdit(itemProjectLink);
            }
        };
        editPcbItemAction.setName("Edit pcb item");

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

