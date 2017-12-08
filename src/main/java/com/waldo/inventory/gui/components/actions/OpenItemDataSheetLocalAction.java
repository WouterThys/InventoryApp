package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OpenItemDataSheetLocalAction extends AbstractAction {

    private static final String name = "Local data sheet";

    public OpenItemDataSheetLocalAction() {
        super(name, imageResource.readImage("Actions.ItemDataSheetLocal"));
    }

    public abstract void onOpenLocalDataSheet();

    @Override
    public void actionPerformed(ActionEvent e) {
        onOpenLocalDataSheet();
    }
}
