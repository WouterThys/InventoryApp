package com.waldo.inventory.gui.dialogs.filechooserdialog;

import com.waldo.inventory.classes.dbclasses.ProjectIDE;

import javax.swing.*;
import java.util.List;

public class IDEFileChooser {

    public static JFileChooser getFileChooser(List<ProjectIDE> projectIDES) {
        JFileChooser fileChooser = new JFileChooser();

        // TODO fileChooser.addChoosableFileFilter(FileUtils.getIDEFilter(projectIDES));
        fileChooser.setAcceptAllFileFilterUsed(false);
        //fileChooser.add

        return fileChooser;
    }
}
