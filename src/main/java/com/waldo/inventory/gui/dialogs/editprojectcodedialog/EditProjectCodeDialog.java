package com.waldo.inventory.gui.dialogs.editprojectcodedialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectCode;
import com.waldo.utils.FileUtils;

import java.awt.*;
import java.io.File;

public class EditProjectCodeDialog extends EditProjectCodeDialogLayout {

    public EditProjectCodeDialog(Window window, ProjectCode projectCode) {
        super(window, projectCode);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    @Override
    public VerifyState verify() {
        VerifyState state = VerifyState.Ok;
        if (getProjectCode().getDirectory().isEmpty()) {
            directoryPnl.setWarning("Directory can not be empty..");
            state = VerifyState.Warning;
        } else {
            File dir = new File(getProjectCode().getDirectory());
            if (!dir.exists()) {
                directoryPnl.setWarning("Directory does not exist..");
                state = VerifyState.Warning;
            }
        }

        if (getProjectCode().getProjectIDEId() > DbObject.UNKNOWN_ID) {
            if (getProjectCode().getProjectIDE().isUseParentFolder()) {
                File parent = new File(getProjectCode().getDirectory());
                if (!(parent.exists() && parent.isDirectory() && FileUtils.contains(parent, getProjectCode().getProjectIDE().getExtension()))) {
                    directoryPnl.setWarning("Directory does not match with IDE");
                    state = VerifyState.Warning;
                }
            } else {
                File file = new File(getProjectCode().getDirectory());
                if (!(file.exists() && FileUtils.is(file, getProjectCode().getProjectIDE().getExtension()))) {
                    directoryPnl.setWarning("Directory does not match with IDE");
                    state = VerifyState.Warning;
                }
            }
        }

        return state;
    }
}