package com.waldo.inventory.gui.dialogs.resistorpreviewdialog;

import javax.swing.*;
import java.awt.*;

public class ResistorPreviewCacheDialog extends ResistorPreviewCacheDialogLayout {


    public ResistorPreviewCacheDialog(Window application, String title, JPanel previewPanel) {
        super(application, title, previewPanel);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}