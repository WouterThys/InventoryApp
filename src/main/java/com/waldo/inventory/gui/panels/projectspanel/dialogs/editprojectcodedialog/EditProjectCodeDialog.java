package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectcodedialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.ProjectCode;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.filechooserdialog.IDEFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.SearchManager.sm;

public class EditProjectCodeDialog extends EditProjectCodeDialogLayout {

    private boolean canClose;

    public EditProjectCodeDialog(Application application, String title, ProjectCode projectCode) {
        super(application, title);

        this.projectCode = projectCode;

        initializeComponents();
        initializeLayouts();
        updateComponents(projectCode);
    }

    public ProjectCode getProjectCode() {
        return projectCode;
    }

    private boolean checkChange() {
        return (projectCode != null) && !(projectCode.equals(originalProjectCode));
    }

    private void showSaveDialog(boolean closeAfter) {
        if (projectCode != null) {
            String msg = projectCode.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    projectCode.save();
                    projectCode = originalProjectCode.createCopy();
                    if (closeAfter) {
                        dispose();
                    }
                }
            }
        } else {
            if (closeAfter) {
                dialogResult = OK;
                dispose();
            }
        }
        canClose = true;
    }

    private boolean verify() {
        boolean ok = true;
        if (directoryTf.getText().isEmpty()) {
            directoryTf.setError("Directory can not be empty..");
            ok = false;
        }

        // TODO check if project IDE matches the selected project type -> should also be in the file open dialog, only with the extension files..

        return ok;
    }


    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog(true);
        }
        if (canClose) {
            super.onOK();
        }
    }

    @Override
    protected void onCancel() {
        if (projectCode != null && originalProjectCode != null) {
            originalProjectCode.createCopy(projectCode);
            projectCode.setCanBeSaved(true);
        }

        super.onCancel();
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            projectCode.save();
            originalProjectCode = projectCode.createCopy();
            getButtonNeutral().setEnabled(false);
            canClose = true;
        }
    }

    //
    // Edited listener
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        if (!application.isUpdating()) {
            return projectCode;
        }
        return null;
    }

    //
    // Browse button
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        List<ProjectIDE> ideList = new ArrayList<>();
        if (projectCode.getProjectId() > DbObject.UNKNOWN_ID) {
            ideList.add(projectCode.getProjectIDE());
        } else {
            ideList = sm().findProjectIDEsByType(Statics.ProjectTypes.Code);
        }
        JFileChooser fileChooser = IDEFileChooser.getFileChooser(ideList);
        if (projectCode.getProject() != null) {
            // TODO get project dir
        }
        fileChooser.setCurrentDirectory(new File("home/Documents/"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showDialog(EditProjectCodeDialog.this, "Select") == JFileChooser.APPROVE_OPTION) {
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
            directoryTf.setText(dir);
            projectCode.setDirectory(dir);
            onValueChanged(directoryTf, "", 0,0);
        }
    }
}