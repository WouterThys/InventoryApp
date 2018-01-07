package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ViewAllLinksAction extends AbstractAction {

    private static final String name = "All links";

    protected ViewAllLinksAction() {
        super(name, imageResource.readImage("Actions.ViewAllLinks"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onViewAllLinks();

    @Override
    public void actionPerformed(ActionEvent e) {
        onViewAllLinks();
    }
}

