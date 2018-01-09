package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class WizardAction  extends AbstractAction {

    private static final String name = "Wizard";

    protected WizardAction() {
        super(name, imageResource.readImage("Actions.Wizard"));
    }

    protected WizardAction(String name) {
        super(name, imageResource.readImage("Actions.Wizard"));
    }

    public void setName(String name) {
        putValue(AbstractAction.NAME, name);
    }

    public abstract void onMagic();

    @Override
    public void actionPerformed(ActionEvent e) {
        onMagic();
    }
}


