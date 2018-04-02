package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.components.actions.IActions;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class DivisionPopup extends JPopupMenu {

    protected DivisionPopup(final Division division) {
        super();

        init(division);
    }

    public abstract void onAddDivision();
    public abstract void onEditDivision();
    public abstract void onDeleteDivision();

    private void init(final Division division) {

        IActions.AddAction addDivisionAction = new IActions.AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddDivision();
            }
        };

        IActions.EditAction editDivisionAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditDivision();
            }
        };

        IActions.DeleteAction deleteDivisionAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteDivision();
            }
        };

        addDivisionAction.setName("Add division to " + division);
        editDivisionAction.setName("Edit " + division);
        deleteDivisionAction.setName("Delete " + division);
        add(addDivisionAction);
        add(editDivisionAction);
        add(deleteDivisionAction);

    }
}
