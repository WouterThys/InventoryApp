package com.waldo.inventory.gui.dialogs.alllinkeditemsdialog;

import com.waldo.inventory.classes.dbclasses.PcbItem;

import java.awt.*;

public class AllLinkedItemsDialog extends AllLinkedItemsDialogLayout {


    public AllLinkedItemsDialog(Window parent, PcbItem pcbItem) {
        super(parent, pcbItem);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}