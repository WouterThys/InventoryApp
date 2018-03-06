package com.waldo.inventory.gui.dialogs.resistorpreviewdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.OfficeUtils;
import com.waldo.inventory.gui.components.IDialog;

import javax.swing.*;
import java.awt.*;

abstract class ResistorPreviewDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JButton exportBtn;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel previewPanel;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ResistorPreviewDialogLayout(Window window, String title, JPanel previewPanel) {
        super(window, title);

        this.previewPanel = previewPanel;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        exportBtn = new JButton("Export");
        exportBtn.addActionListener(e -> OfficeUtils.test(GuiUtils.imageFromPanel(previewPanel)));
    }

    @Override
    public void initializeLayouts() {

        getContentPanel().add(exportBtn);
        getContentPanel().add(previewPanel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {

        } else {

        }
    }
}