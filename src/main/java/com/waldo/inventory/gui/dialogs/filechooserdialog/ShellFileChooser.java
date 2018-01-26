package com.waldo.inventory.gui.dialogs.filechooserdialog;

import com.waldo.utils.FileUtils;

import javax.swing.*;

public class ShellFileChooser {
    public static JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.addChoosableFileFilter(FileUtils.getShellFilter());
        fileChooser.setAcceptAllFileFilterUsed(true);

        //fileChooser.setFileView(new ImageFileChooser.ImageFileView());
        //fileChooser.setAccessory(new ImageFileChooser.ImagePreview(fileChooser));

        return fileChooser;
    }
}
