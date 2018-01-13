package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.Application;

import java.util.List;

public class UsedPcbItemsDialog extends UsedPcbItemsDialogLayout {


    public UsedPcbItemsDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        selectedPcb = projectPcb;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    private void removeUnusedItems() {
        for (PcbItemProjectLink link : pcbItemPnl.pcbTableGetItemList()) {
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
    // Move to used panel
    //
    @Override
    public void onAdd() {
        pcbItemPnl.createUsedLinks();
        usedPnl.usedTableInit(pcbItemPnl.getAllLinks());
    }

    //
    // Confirm used components
    //
    @Override
    public void onSetUsed() {
        List<PcbItemProjectLink> usedItems = usedPnl.getItemsToProcess();
        for (PcbItemProjectLink link : usedItems) {
            PcbItemItemLink itemLink = link.getPcbItemItemLink();

            Item item = itemLink.getItem();
            int newAmount = item.getAmount() - link.getUsedCount();
            if (newAmount < 0) {
                // todo: Warning?
                newAmount = 0;
            }
            item.setAmount(newAmount);
            item.save();

            link.setUsed(true);
            link.setProcessed(true);
            link.save();
        }
    }
}