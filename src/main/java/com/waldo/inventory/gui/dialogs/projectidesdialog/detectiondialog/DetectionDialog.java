package com.waldo.inventory.gui.dialogs.projectidesdialog.detectiondialog;

import java.awt.*;
import java.awt.event.ActionEvent;

public class DetectionDialog extends DetectionDialogLayout {


    public DetectionDialog(Window parent, String title,
                           String extension,
                           boolean openAsFolder,
                           boolean matchExtension,
                           boolean useParentFolder) {
        super(parent, title, extension, openAsFolder, matchExtension, useParentFolder);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }


    public String getExtension() {
        return extensionTf.getText();
    }

    public boolean isOpenAsFolder() {
        return openAsFolderCb.getSelectedIndex() == 0;
    }

    public boolean isMatchExtension() {
        return extensionMatchCb.getSelectedIndex() == 0;
    }

    public boolean isUseParentFolder() {
        return extensionUsingCb.getSelectedIndex() == 0;
    }


    //
    // Combo box changed
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        //if (source.equals(openAsFolderCb)) {
            extensionMatchCb.setEnabled(isOpenAsFolder());
            extensionUsingCb.setEnabled(isOpenAsFolder());
        //}
//        if (source.equals(extensionMatchCb)) {
//
//        }
//        if (source.equals(extensionUsingCb)) {
//
//        }
    }
}
