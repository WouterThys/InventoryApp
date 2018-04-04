package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.actions.IActions;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class SetPopup extends JPopupMenu {

    protected SetPopup(final Set set) {
        super();

        init(set);
    }

    public abstract void onEditSet(Set set);
    public abstract void onDeleteSet(Set set);
    public abstract void onAddItemsToSet(Set set);
    public abstract void onSetWizard(Set set);

    private void init(final Set set) {

        IActions.AddAction addItemsToSetAction = new IActions.AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddItemsToSet(set);
            }
        };

        IActions.EditAction editSetAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditSet(set);
            }
        };

        IActions.DeleteAction deleteSetAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteSet(set);
            }
        };

        IActions.WizardAction wizardAction  = new IActions.WizardAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSetWizard(set);
            }
        };

        addItemsToSetAction.setName("Add items to " + set);
        editSetAction.setName("Edit " + set);
        deleteSetAction.setName("Delete " + set);
        wizardAction.setName("Set item wizard");

        add(editSetAction);
        add(deleteSetAction);
        addSeparator();
        add(addItemsToSetAction);
        add(wizardAction);

    }
}

