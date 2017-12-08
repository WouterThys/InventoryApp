package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class DeleteItemAction extends AbstractAction {

    private static final String name = "Delete item";

    public DeleteItemAction() {
        super(name, imageResource.readImage("Actions.ItemDelete"));
    }

    public abstract void onDeleteItem();

    @Override
    public void actionPerformed(ActionEvent e) {
        onDeleteItem();
    }
}
