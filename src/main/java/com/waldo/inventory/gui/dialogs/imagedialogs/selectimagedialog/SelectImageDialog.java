package com.waldo.inventory.gui.dialogs.imagedialogs.selectimagedialog;

import com.waldo.inventory.Utils.Statics.ImageType;

import java.awt.*;
import java.io.File;

public class SelectImageDialog extends SelectImageDialogLayout {


    public SelectImageDialog(Window window, boolean multiple) {
        super(window, multiple, null);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public SelectImageDialog(Window window, boolean multiple, ImageType imageType) {
        super(window, multiple, imageType);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }


    public File getSelectedFile() {
        return new File(browsePnl.getText());
    }

    public ImageType getImageType() {
        if (imageType == null) {
            return (ImageType) imageTypeCb.getSelectedItem();
        } else {
            return imageType;
        }
    }

    public String getImageName() {
        return imageNameTf.getText();
    }


    @Override
    protected void onOK() {

        boolean ok = true;

        if (!multiple) {
            String name = imageNameTf.getText();
            if (name == null || name.isEmpty()) {
                imageNameTf.setError("Name can not be empty..");
                ok = false;
            }
        }

        File file = getSelectedFile();
        if (file == null || !file.exists()) {
            //browsePnl.setError("Select a valid file..");
        }

        if (ok) {
            super.onOK();
        }
    }
}