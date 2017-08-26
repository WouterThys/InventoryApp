package com.waldo.inventory.gui.dialogs.graphsdialog;

import com.waldo.inventory.gui.Application;

public class GraphsDialog extends GraphsDialogLayout {


    public GraphsDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);

    }

}