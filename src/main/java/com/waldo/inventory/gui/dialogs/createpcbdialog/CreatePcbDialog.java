package com.waldo.inventory.gui.dialogs.createpcbdialog;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.dialogs.editcreatedlinkspcbdialog.EditCreatedPcbLinksDialog;

import javax.swing.*;
import java.awt.*;

public class CreatePcbDialog extends CreatePcbDialogLayout implements CacheChangedListener<CreatedPcb> {

    private final Window parent;

    public CreatePcbDialog(Window window, String title, ProjectPcb projectPcb) {
        super(window, title, projectPcb);
        this.parent = window;
        addCacheListener(CreatedPcb.class, this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    //
    // Cache changed
    //
    @Override
    public void onInserted(CreatedPcb object) {
        updateComponents(object);
    }

    @Override
    public void onUpdated(CreatedPcb object) {
        updateComponents(object); // Should not happen?
    }

    @Override
    public void onDeleted(CreatedPcb object) {
        // Should not happen
    }

    @Override
    public void onCacheCleared() {
        // Don't care
    }


    //
    // Gui events
    //
    @Override
    void onAddNewPcb(ProjectPcb projectPcb) {
        String newName = JOptionPane.showInputDialog(CreatePcbDialog.this, "PCB name: ");
        if (newName != null && !newName.isEmpty()) {
            CreatedPcb pcb = new CreatedPcb(newName.trim(), projectPcb);
            pcb.save();
        }
    }

    @Override
    void onGoAction(CreatedPcb createdPcb) {
        if (createdPcb == null) {
            JOptionPane.showMessageDialog(CreatePcbDialog.this, "Selected a PCB", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        super.onOK();
        EditCreatedPcbLinksDialog dialog = new EditCreatedPcbLinksDialog(
                parent,
                "Edit pcb",
                projectPcb,
                createdPcb
        );
        dialog.showDialog();
    }
}
