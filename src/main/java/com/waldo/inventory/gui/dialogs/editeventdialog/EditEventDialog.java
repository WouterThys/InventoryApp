package com.waldo.inventory.gui.dialogs.editeventdialog;

import com.waldo.inventory.database.classes.DbEvent;

import java.awt.*;

public class EditEventDialog extends EditEventDialogLayout {


    public EditEventDialog(Window parent, String title, DbEvent dbEvent) {
        super(parent, title, dbEvent);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    @Override
    protected void onOK() {
        copyValues();
        super.onOK();
    }
}