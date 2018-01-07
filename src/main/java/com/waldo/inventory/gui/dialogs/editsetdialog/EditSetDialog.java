package com.waldo.inventory.gui.dialogs.editsetdialog;

import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;

public class EditSetDialog extends EditSetDialogLayout {


    public EditSetDialog(Application application, String title, Set set) {
        super(application, title, set);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}