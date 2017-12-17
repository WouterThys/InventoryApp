package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.gui.Application;

public class AdvancedSearchDialog extends AdvancedSearchDialogLayout {


    public AdvancedSearchDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}
