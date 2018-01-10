package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class DoItAction extends AbstractAction {

    private static final String name = "Create series";

    public DoItAction() {
        super(name, imageResource.readImage("Actions.SetItemCreateSeries"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onDoIt();

    @Override
    public void actionPerformed(ActionEvent e) {
        onDoIt();
    }
}
