package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class DeleteDivisionAction extends AbstractAction {

    private static final String name = "Delete division";

    public DeleteDivisionAction() {
        super(name, imageResource.readImage("Actions.DivisionDelete"));
    }

    public abstract void onDeleteDivision();

    @Override
    public void actionPerformed(ActionEvent e) {
        onDeleteDivision();
    }
}
