package com.waldo.inventory.gui.dialogs.allaliasesdialog;

import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class AllAliasesDialog extends AllAliasesDialogLayout {


    public AllAliasesDialog(Window parent, String title, String currentAlias) {
        super(parent, title, currentAlias);

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