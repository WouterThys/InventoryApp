package com.waldo.inventory.gui.dialogs.addimagedialog;

import com.waldo.inventory.gui.dialogs.filechooserdialog.ImageFileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class AddImageDialog extends AddImageDialogLayout {


    public AddImageDialog(Window window) {
        super(window);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    public ImageIcon getImage() {
        return imageIcon;
    }

    public String getImageName() {
        if (imageFile != null) {
            return imageFile.getName();
        }
        return "";
    }


    @Override
    void onBrowseImage() {

        String defaultPath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        JFileChooser fileChooser = ImageFileChooser.getFileChooser();
        fileChooser.setCurrentDirectory(new File(defaultPath));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showDialog(AddImageDialog.this, "Open") == JFileChooser.APPROVE_OPTION) {
            imageIcon = null;
            imageFile = null;
            imageFile = fileChooser.getSelectedFile();
            if (imageFile != null && imageFile.exists()) {
                Image image;
                try {
                    image = ImageIO.read(imageFile);
                    imageIcon = new ImageIcon(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (imageIcon != null) {
                    getButtonNeutral().setEnabled(true);
                    setImage(imageIcon);
                } else {
                    getButtonNeutral().setEnabled(false);
                    setImage(null);
                }
            }
        }
    }

    @Override
    void onPasteImage() {

    }

    @Override
    void onMouseRightClicked(MouseEvent e) {

    }
}