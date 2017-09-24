package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectpcbdialog;

import com.waldo.inventory.gui.Application;

public class EditProjectPcbDialog extends EditProjectPcbDialogLayout {


    public EditProjectPcbDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);

    }

}