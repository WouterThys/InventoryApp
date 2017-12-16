package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OpenItemDataSheetLocalAction extends AbstractAction {

    private static final String name = "Local data sheet";

    protected OpenItemDataSheetLocalAction() {
        super(name, imageResource.readImage("Actions.ItemDataSheetLocal"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onOpenLocalDataSheet();

    @Override
    public void actionPerformed(ActionEvent e) {
        onOpenLocalDataSheet();
    }
}
