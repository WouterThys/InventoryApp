package com.waldo.inventory.gui.dialogs.editimagedialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbImage;
import com.waldo.inventory.database.interfaces.ImageChangedListener;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.addimagedialog.AddImageDialog;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.database.ImageDbAccess.imDb;

public class EditImageDialog extends EditImageDialogLayout implements ImageChangedListener {


    public EditImageDialog(Window window) {
        this(window, null);

    }

    public EditImageDialog(Window window, Statics.ImageType imageType) {
        super(window, "Images", imageType);

        initializeComponents();
        initializeLayouts();

        imDb().addImageChangedListener(this);

        updateComponents();

    }

    public DbImage getSelectedImage() {
        return selectedImage;
    }


    //
    // This
    //
    @Override
    public void onImageClicked(MouseEvent e, DbImage image) {
        selectedImage = image;
        if (image != null) {
            getButtonOK().setEnabled(true);
        }
        updateEnabledComponents();
    }


    //
    // Image changed
    //
    @Override
    public void onInserted(DbImage image) {
        updateComponents(); // TODO make more efficient
    }

    @Override
    public void onUpdated(DbImage image) {
        updateComponents(); // TODO make more efficient
    }

    @Override
    public void onDeleted(DbImage image) {
        updateComponents(); // TODO make more efficient
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateComponents();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        SwingUtilities.invokeLater(() -> {
            AddImageDialog dialog = new AddImageDialog(EditImageDialog.this);
            if (dialog.showDialog() == IDialog.OK) {
                ImageIcon newIcon = dialog.getImage();
                if (newIcon != null) {
                    DbImage dbImage = new DbImage(imageType, newIcon, dialog.getImageName());
                    dbImage.save();
                }
            }
        });
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
