package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.classes.*;
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
            PcbItemItemLink itemLink = link.getPcbItem().getMatchedItemLink();
            if (itemLink.isSetItem()) {
                SetItem setItem = itemLink.getSetItem();
                int newAmount = setItem.getAmount() - link.getUsedCount();
                if (newAmount < 0) {
                    // todo: Warning?
                    newAmount = 0;
                }
                setItem.setAmount(newAmount);
                setItem.save();
            } else {
                Item item = itemLink.getItem();
                int newAmount = item.getAmount() - link.getUsedCount();
                if (newAmount < 0) {
                    // todo: Warning?
                    newAmount = 0;
                }
                item.setAmount(newAmount);
                item.save();
            }
            link.setUsed(true);
            link.setProcessed(true);
            link.save();
        }
    }
}