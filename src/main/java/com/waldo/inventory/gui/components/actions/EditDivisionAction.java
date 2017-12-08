package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditDivisionAction extends AbstractAction{

    private static final String name = "Edit division";

    public EditDivisionAction() {
        super(name, imageResource.readImage("Actions.DivisionEdit"));
    }

    public abstract void onEditDivision();

    @Override
    public void actionPerformed(ActionEvent e) {
        onEditDivision();
    }
}
