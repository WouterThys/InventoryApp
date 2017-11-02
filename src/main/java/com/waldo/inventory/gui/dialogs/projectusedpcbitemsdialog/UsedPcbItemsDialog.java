package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.PcbItemProjectLink;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;

public class UsedPcbItemsDialog extends UsedPcbItemsDialogLayout {


    public UsedPcbItemsDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        selectedPcb = projectPcb;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    private void removeUnusedItems() {
        for (PcbItem pcbItem : pcbItemPnl.pcbTableGetItemList()) {
            PcbItemProjectLink link = pcbItemPnl.getLink(pcbItem);
            if (link != null && !link.isUsed()) {
                link.setUsedCount(0);
            }
        }
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        removeUnusedItems();
        super.onOK();
    }

    @Override
    protected void onCancel() {
        removeUnusedItems();
        super.onCancel();
    }

    //
    // Panels
    //
    @Override
    public void onAdd() {
        pcbItemPnl.createUsedLinks();
        usedPnl.usedTableInit(pcbItemPnl.getAllLinks());
    }

    @Override
    public void onSetUsed() {

    }
}