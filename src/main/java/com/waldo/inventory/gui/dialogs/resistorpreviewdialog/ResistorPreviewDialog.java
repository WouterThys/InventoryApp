package com.waldo.inventory.gui.dialogs.resistorpreviewdialog;

import javax.swing.*;
import java.awt.*;

public class ResistorPreviewDialog extends ResistorPreviewDialogLayout {


    public ResistorPreviewDialog(Window application, String title, JPanel previewPanel) {
        super(application, title, previewPanel);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}