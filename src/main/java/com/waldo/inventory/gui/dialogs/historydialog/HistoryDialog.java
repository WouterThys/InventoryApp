package com.waldo.inventory.gui.dialogs.historydialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;

public class HistoryDialog extends HistoryDialogLayout {



    public HistoryDialog(Application application, String title, Item item) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(item);
    }
}
