package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class AddDivisionAction extends AbstractAction {

    private static final String name = "Add division";

    protected AddDivisionAction() {
        super(name, imageResource.readImage("Actions.DivisionAdd"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onAddDivision();

    @Override
    public void actionPerformed(ActionEvent e) {
        onAddDivision();
    }
}
