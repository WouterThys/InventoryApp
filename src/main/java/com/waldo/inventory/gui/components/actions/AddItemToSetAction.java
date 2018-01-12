package com.waldo.inventory.gui.components.actions;

import com.waldo.inventory.classes.dbclasses.Set;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class AddItemToSetAction extends AbstractAction {

    private final Set set;

    protected AddItemToSetAction(Set set) {
        super(set.toString(), imageResource.readImage("Sets.Small"));
        this.set = set;
    }

    public abstract void onAddToSet(ActionEvent e, Set set);

    @Override
    public void actionPerformed(ActionEvent e) {
        onAddToSet(e, set);
    }
}

