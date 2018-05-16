package com.waldo.inventory.gui.dialogs.solderiteminfodialog;

import com.waldo.inventory.classes.dbclasses.SolderItem;

import java.awt.*;

public class SolderItemInfoDialog extends SolderItemInfoDialogLayout {


    public SolderItemInfoDialog(Window window, SolderItem solderItem) {
        super(window, solderItem);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}
