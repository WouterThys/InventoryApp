package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class AddDivisionAction extends AbstractAction {

    private static final String name = "Add division";

    public AddDivisionAction() {
        super(name, imageResource.readImage("Actions.DivisionAdd"));
    }

    public abstract void onAddDivision();

    @Override
    public void actionPerformed(ActionEvent e) {
        onAddDivision();
    }
}
