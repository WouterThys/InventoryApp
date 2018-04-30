package com.waldo.inventory.gui.dialogs.createpcbdialog;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;

import java.awt.*;
import java.awt.event.ItemEvent;

public class SelectPcbCacheDialog extends SelectPcbCacheDialogLayout {

    public SelectPcbCacheDialog(Window window, String title, ProjectPcb projectPcb) {
        super(window, title, projectPcb);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public CreatedPcb getCreatedPcb() {
        return selectedPcb;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            selectedPcb = (CreatedPcb) createdPcbCb.getSelectedItem();
            getButtonOK().setEnabled(selectedPcb != null);
        }
    }
}
