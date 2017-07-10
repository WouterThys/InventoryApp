package com.waldo.inventory.gui.dialogs.historydialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;

public class HistoryDialog extends HistoryDialogLayout {

    public HistoryDialog(Application application, Item item) {
        super(application);

        initializeComponents();
        initializeLayouts();
        updateComponents(item);

    }
}
