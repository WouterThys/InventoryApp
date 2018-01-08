package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class AutoCalculateUsedAction extends AbstractAction {

    private static final String name = "Auto set used count";

    protected AutoCalculateUsedAction() {
        super(name, imageResource.readImage("Actions.AutoSetUsed"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onAutoSetUsed();

    @Override
    public void actionPerformed(ActionEvent e) {
        onAutoSetUsed();
    }
}
