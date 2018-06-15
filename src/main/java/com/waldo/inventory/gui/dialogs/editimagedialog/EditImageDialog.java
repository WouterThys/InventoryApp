package com.waldo.inventory.gui.dialogs.editimagedialog;

import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class EditImageDialog extends EditImageDialogLayout {


    public EditImageDialog(Window window) {
        super(window, "Images");

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }


    @Override
    public void onImageClicked(MouseEvent e, DbImage image) {
        selectedImage = image;
        updateEnabledComponents();
    }


    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateComponents();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {

    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedImage != null) {
            int res = JOptionPane.showConfirmDialog(
                    EditImageDialog.this,
                    "Do you really want to delete " + selectedImage + "?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (res == JOptionPane.YES_OPTION) {
                selectedImage.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {

    }
}
