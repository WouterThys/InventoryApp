package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;

public class OrderPcbItemDialog extends OrderPcbItemDialogLayout {


    public OrderPcbItemDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(projectPcb);

    }

    //
    // Actions
    //
    @Override
    void onAddAll() {
        for (PcbItem item : pcbTableGetItemList()) {
            if (item.getMatchedItemLink().isSetItem()) {
                item.setOrderAmount(1);
            } else {
                item.setOrderAmount(item.getReferences().size());
            }
        }
        pcbTableUpdate();
    }

    @Override
    void onRemAll() {
        for (PcbItem item : pcbTableGetItemList()) {
            item.setOrderAmount(0);
        }
        pcbTableUpdate();
    }

    @Override
    void onAddOne(PcbItem pcbItem) {
        if (pcbItem != null) {
            pcbItem.setOrderAmount(pcbItem.getOrderAmount() + 1);
            pcbTableUpdate();
        }
    }

    @Override
    void onRemOne(PcbItem pcbItem) {
        if (pcbItem != null) {
            if (pcbItem.getOrderAmount() > 0) {
                pcbItem.setOrderAmount(pcbItem.getOrderAmount() - 1);
                pcbTableUpdate();
            }
        }
    }

    @Override
    void onCalculate() {

    }
}