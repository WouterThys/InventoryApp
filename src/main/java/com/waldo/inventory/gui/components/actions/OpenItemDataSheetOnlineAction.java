package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OpenItemDataSheetOnlineAction extends AbstractAction {

    private static final String name = "Online data sheet";

    protected OpenItemDataSheetOnlineAction() {
        super(name, imageResource.readImage("Actions.ItemDataSheetOnline"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onOpenOnlineDataSheet();

    @Override
    public void actionPerformed(ActionEvent e) {
        onOpenOnlineDataSheet();
    }
}
