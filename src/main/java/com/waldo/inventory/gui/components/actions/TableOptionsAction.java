package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class TableOptionsAction extends AbstractAction {

    private static final String name = "Table options";

    protected TableOptionsAction() {
        super(name, imageResource.readImage("Toolbar.Table.ApplySort"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onTableOptions(ActionEvent e);

    @Override
    public void actionPerformed(ActionEvent e) {
        onTableOptions(e);
    }
}
