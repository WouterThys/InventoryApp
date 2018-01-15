package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class SearchAction extends AbstractAction {

    private static final String name = "Search";

    protected SearchAction() {
        super(name, imageResource.readImage("Actions.Search"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onSearch();

    @Override
    public void actionPerformed(ActionEvent e) {
        onSearch();
    }
}
