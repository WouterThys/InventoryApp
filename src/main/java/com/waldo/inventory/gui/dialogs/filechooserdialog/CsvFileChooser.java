package com.waldo.inventory.gui.dialogs.filechooserdialog;

import com.waldo.inventory.Utils.FileUtils;

import javax.swing.*;

public class CsvFileChooser {

    public static JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.addChoosableFileFilter(FileUtils.getCsvFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);

        //fileChooser.setFileView(new ImageFileChooser.ImageFileView());
        //fileChooser.setAccessory(new ImageFileChooser.ImagePreview(fileChooser));

        return fileChooser;
    }


}
