package com.waldo.inventory.gui.dialogs.usedpcbitemsdialog;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.Application;

import java.awt.event.ActionEvent;
import java.util.List;

public class UsedPcbItemsDialog extends UsedPcbItemsDialogLayout {


    public UsedPcbItemsDialog(Application application, String title, List<PcbItem> pcbItemList) {
        super(application, title);

        this.pcbItemList = pcbItemList;

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}