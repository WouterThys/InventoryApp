package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class UpdateSetItemLocationsAction extends AbstractAction {

    private static final String name = "Create series";

    public UpdateSetItemLocationsAction() {
        super(name, imageResource.readImage("Actions.SetItemUpdateLocations"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onUpdateLocations();

    @Override
    public void actionPerformed(ActionEvent e) {
        onUpdateLocations();
    }
}
