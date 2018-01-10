package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;

public class SetItemsWizardDialog extends SetItemsWizardDialogLayout {


    public SetItemsWizardDialog(Application application, String title, Set set) {
        super(application, title, set);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    @Override
    protected void onOK() {
        next();
    }

    @Override
    protected void onCancel() {
        super.onCancel();
    }

    @Override
    protected void onNeutral() {
        previous();
    }
}
