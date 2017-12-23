package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectpcbdialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class EditProjectPcbDialog extends EditProjectPcbDialogLayout {

    private boolean canClose = true;

    public EditProjectPcbDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        this.projectPcb = projectPcb;

        initializeComponents();
        initializeLayouts();
        updateComponents(projectPcb);
    }

    private boolean checkChange() {
        return (projectPcb != null) && !(projectPcb.equals(originalProjectPcb));
    }

    private void showSaveDialog() {
        if (projectPcb != null) {
            String msg = projectPcb.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    projectPcb.createName();
                    projectPcb.save();
                    projectPcb = originalProjectPcb.createCopy();
                    dispose();
                }
            }
        } else {
            dialogResult = OK;
            dispose();
        }
        canClose = true;
    }

    private boolean verify() {
        boolean ok = true;
        if (projectPcb.getDirectory().isEmpty()) {
            directoryTf.setError("Directory can not be empty..");
            ok = false;
        } else {
            File dir = new File(projectPcb.getDirectory());
            if (!dir.exists()) {
                directoryTf.setError("Directory does not exist..");
                ok = false;
            }
        }

        if (ok) {
            if (projectPcb.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                if (projectPcb.getProjectIDE().isUseParentFolder()) {
                    File parent = new File(projectPcb.getDirectory());
                    if (!(parent.exists() && parent.isDirectory() && FileUtils.contains(parent, projectPcb.getProjectIDE().getExtension()))) {
                        directoryTf.setWarning("Directory does not match with IDE");
                    }
                } else {
                    File file = new File(projectPcb.getDirectory());
                    if (!(file.exists() && FileUtils.is(file, projectPcb.getProjectIDE().getExtension()))) {
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
            showSaveDialog();
        }
        if (canClose) {
            super.onOK();
        }
    }

    @Override
    protected void onCancel() {
        if (projectPcb != null && originalProjectPcb != null) {
            originalProjectPcb.createCopy(projectPcb);
            projectPcb.setCanBeSaved(true);
        }

        super.onCancel();
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            projectPcb.createName();
            projectPcb.save();
            originalProjectPcb = projectPcb.createCopy();
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
            return projectPcb;
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
        if (projectPcb.getProject() != null) {
            home = projectPcb.getProject().getMainDirectory();
        }
        if (home.isEmpty()) {
            home = "/home/waldo/Documents/";
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setCurrentDirectory(new File(home));

        if (fileChooser.showDialog(EditProjectPcbDialog.this, "Select") == JFileChooser.APPROVE_OPTION) {
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
            directoryTf.setError(null);
            directoryTf.setText(dir);
            projectPcb.setDirectory(dir);
            verify();
            onValueChanged(directoryTf, "", 0,0);
        }
    }
}