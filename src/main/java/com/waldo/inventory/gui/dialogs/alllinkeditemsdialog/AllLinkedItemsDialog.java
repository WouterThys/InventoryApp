package com.waldo.inventory.gui.dialogs.alllinkeditemsdialog;

import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.gui.Application;

public class AllLinkedItemsDialog extends AllLinkedItemsDialogLayout {


    public AllLinkedItemsDialog(Application application, PcbItem pcbItem) {
        super(application, pcbItem);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}