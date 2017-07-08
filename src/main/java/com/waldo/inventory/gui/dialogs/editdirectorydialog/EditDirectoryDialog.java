package com.waldo.inventory.gui.dialogs.editdirectorydialog;


import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.classes.ProjectType;

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

    public List<ProjectType> getSelectedTypes() {
        List<ProjectType> typeList = new ArrayList<>();

        JCheckBox allCb = useTypeCb.getCheckBoxes()[0];
        if (allCb.isSelected()) {
            typeList = projectTypeList;
        } else {
            for (int i = 1; i < useTypeCb.getItemCount(); i++) {
                JCheckBox cb = useTypeCb.getCheckBoxes()[i];
                if (cb.isSelected()) {
                    typeList.add(projectTypeList.get(i-1));
                }
            }
        }

        return typeList;
    }

}
