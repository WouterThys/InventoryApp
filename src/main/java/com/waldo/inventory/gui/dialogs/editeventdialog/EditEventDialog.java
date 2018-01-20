package com.waldo.inventory.gui.dialogs.editeventdialog;

import com.waldo.inventory.classes.database.DbEvent;

import java.awt.*;

public class EditEventDialog extends EditEventDialogLayout {


    public EditEventDialog(Dialog owner, String title, DbEvent dbEvent) {
        super(owner, title, dbEvent);

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