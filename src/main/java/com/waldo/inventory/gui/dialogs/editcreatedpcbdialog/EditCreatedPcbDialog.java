package com.waldo.inventory.gui.dialogs.editcreatedpcbdialog;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;

import java.awt.*;

public class EditCreatedPcbDialog extends EditCreatedPcbDialogLayout {


    public EditCreatedPcbDialog(Window window, String title, ProjectPcb projectPcb, CreatedPcb createdPcb) {
        super(window, title, projectPcb, createdPcb);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}
