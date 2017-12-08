package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OpenItemDataSheetOnlineAction extends AbstractAction {

    private static final String name = "Online data sheet";

    public OpenItemDataSheetOnlineAction() {
        super(name, imageResource.readImage("Actions.ItemDataSheetOnline"));
    }

    public abstract void onOpenOnlineDataSheet();

    @Override
    public void actionPerformed(ActionEvent e) {
        onOpenOnlineDataSheet();
    }
}
