package com.waldo.inventory.gui.dialogs.editdirectorydialog;


import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.classes.ProjectIDE;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EditDirectoryDialog extends EditDirectoryDialogLayout {

    public EditDirectoryDialog(Dialog dialog, String title, ProjectDirectory projectDirectory) {
        super(dialog, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(projectDirectory);
    }

    public String getDirectory() {
        return directoryTf.getText();
    }

    public List<ProjectIDE> getSelectedTypes() {
        List<ProjectIDE> typeList = new ArrayList<>();

        JCheckBox allCb = useTypeCb.getCheckBoxes()[0];
        if (allCb.isSelected()) {
            typeList = projectIDEList;
        } else {
            for (int i = 1; i < useTypeCb.getItemCount(); i++) {
                JCheckBox cb = useTypeCb.getCheckBoxes()[i];
                if (cb.isSelected()) {
                    typeList.add(projectIDEList.get(i-1));
                }
            }
        }

        return typeList;
    }

}
