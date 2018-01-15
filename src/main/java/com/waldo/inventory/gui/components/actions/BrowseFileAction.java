package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class BrowseFileAction extends AbstractAction {

    private static final String name = "Open";

    protected BrowseFileAction() {
        super(name, imageResource.readImage("Actions.BrowseFile"));
    }

    protected BrowseFileAction(String name) {
        super(name, imageResource.readImage("Actions.BrowseFile"));
    }

    public void setName(String name) {
        putValue(AbstractAction.NAME, name);
    }

    public abstract void onBrowseFile(ActionEvent e);

    @Override
    public void actionPerformed(ActionEvent e) {
        onBrowseFile(e);
    }
}