package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.gui.components.actions.IActions;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class CopyPastePopup extends JPopupMenu {

    protected CopyPastePopup(boolean enableCopy, boolean enablePaste) {
        super();

        init(enableCopy, enablePaste);
    }

    public abstract void onCopy();
    public abstract void onPaste();

    private void init(boolean enableCopy, boolean enablePaste) {

        // Actions
        IActions.CopyAction copyAction = new IActions.CopyAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCopy();
            }
        };

        IActions.PasteAction pasteAction = new IActions.PasteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPaste();
            }
        };

        copyAction.setEnabled(enableCopy);
        pasteAction.setEnabled(enablePaste);

        add(copyAction);
        add(pasteAction);
    }
}
