package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectcodedialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.ProjectCode;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

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
                    projectCode.createName();
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
        if (projectCode.getDirectory().isEmpty()) {
            directoryTf.setError("Directory can not be empty..");
            ok = false;
        } else {
            File dir = new File(projectCode.getDirectory());
            if (!dir.exists()) {
                directoryTf.setError("Directory does not exist..");
                ok = false;
            }
        }

        if (ok) {
            if (projectCode.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                if (projectCode.getProjectIDE().isUseParentFolder()) {
                    File parent = new File(projectCode.getDirectory());
                    if (!(parent.exists() && parent.isDirectory() && FileUtils.contains(parent, projectCode.getProjectIDE().getExtension()))) {
                        directoryTf.setWarning("Directory does not match with IDE");
                    }
                } else {
                    File file = new File(projectCode.getDirectory());
                    if (!(file.exists() && FileUtils.is(file, projectCode.getProjectIDE().getExtension()))) {
                        directoryTf.setWarning("Directory does not match with IDE");
                    }
                }
            }
        }

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
            projectCode.createName();
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
        JFileChooser fileChooser = new JFileChooser();
        String home = "";
        if (projectCode.getProject() != null) {
            home = projectCode.getProject().getMainDirectory();
        }
        if (home.isEmpty()) {
            home = "/home/waldo/Documents/";
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setCurrentDirectory(new File(home));

        if (fileChooser.showDialog(EditProjectCodeDialog.this, "Select") == JFileChooser.APPROVE_OPTION) {
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
            directoryTf.setError(null);
            directoryTf.setText(dir);
            projectCode.setDirectory(dir);
            verify();
            onValueChanged(directoryTf, "", 0,0);
        }
    }
}