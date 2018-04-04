package com.waldo.inventory.gui.dialogs.editremarksdialog;

import com.waldo.inventory.gui.components.ITextEditor;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

abstract class EditRemarksDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextEditor textEditor;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    File file;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditRemarksDialogLayout(Window window, String title) {
        super(window, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onSave();

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readIcon("Remarks.Dialog.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setEnabled(true);

        // This
        textEditor = new ITextEditor();
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        getContentPanel().add(textEditor);
        getContentPanel().setPreferredSize(new Dimension(600, 600));

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            file = (File) args[0];
        } else {
            file = null;
        }

        textEditor.setDocument(file);
    }
}