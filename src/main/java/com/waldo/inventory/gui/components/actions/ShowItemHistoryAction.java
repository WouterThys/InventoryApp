package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ShowItemHistoryAction extends AbstractAction {

    private static final String name = "Show history";

    protected ShowItemHistoryAction() {
        super(name, imageResource.readImage("Actions.ItemHistory"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onShowHistory();

    @Override
    public void actionPerformed(ActionEvent e) {
        onShowHistory();
    }
}
