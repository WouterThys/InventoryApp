package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class CheckItOutAction extends AbstractAction {

    private static final String name = "Check it out";

    protected CheckItOutAction() {
        super(name, imageResource.readImage("Actions.M.CheckItOut"));
    }

    public abstract void onCheckItOut();

    @Override
    public void actionPerformed(ActionEvent e) {
        onCheckItOut();
    }
}
