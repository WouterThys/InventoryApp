package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;

public class UsedPcbItemsDialog extends UsedPcbItemsDialogLayout {


    public UsedPcbItemsDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        selectedPcb = projectPcb;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }



}