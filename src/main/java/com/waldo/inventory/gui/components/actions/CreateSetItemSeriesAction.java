package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class CreateSetItemSeriesAction extends AbstractAction {

    private static final String name = "Create series";

    public CreateSetItemSeriesAction() {
        super(name, imageResource.readImage("Actions.SetItemCreateSeries"));
    }

    public abstract void onCreateSeries();

    @Override
    public void actionPerformed(ActionEvent e) {
        onCreateSeries();
    }
}
