package com.waldo.inventory.gui.dialogs.querydialog;

import com.waldo.inventory.gui.Application;


public class QueryDialog extends QueryDialogLayout {


    public QueryDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

}
