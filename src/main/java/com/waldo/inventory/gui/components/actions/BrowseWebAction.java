package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class BrowseWebAction extends AbstractAction {

    private static final String name = "Browse";

    protected BrowseWebAction() {
        super(name, imageResource.readImage("Actions.BrowseWeb"));
    }

    protected BrowseWebAction(String name) {
        super(name, imageResource.readImage("Actions.BrowseWeb"));
    }

    public void setName(String name) {
        putValue(AbstractAction.NAME, name);
    }

    public abstract void onBrowseWeb(ActionEvent e);

    @Override
    public void actionPerformed(ActionEvent e) {
        onBrowseWeb(e);
    }
}
