package com.waldo.inventory.gui.dialogs.allaliasesdialog;

import com.waldo.inventory.gui.Application;

import javax.swing.event.ListSelectionEvent;

public class AllAliasesDialog extends AllAliasesDialogLayout {


    public AllAliasesDialog(Application application, String title, String currentAlias) {
        super(application, title, currentAlias);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public String getSelectedAlias() {
        return aliasList.getSelectedValue();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String val = aliasList.getSelectedValue();
            if (val != null && !val.isEmpty()) {
                getButtonOK().setEnabled(true);
            } else {
                getButtonOK().setEnabled(false);
            }
        }
    }
}