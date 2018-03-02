package com.waldo.inventory.gui.dialogs.resistordialog;

import java.awt.*;

public class ResistorDialog extends ResistorDialogLayout {


    public ResistorDialog(Window window, String title) {
        super(window, title);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}
