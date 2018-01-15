package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectobjectdialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.ProjectOther;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class EditProjectOtherDialog extends EditProjectOtherDialogLayout {

    private boolean canClose = true;

    public EditProjectOtherDialog(Application application, String title, ProjectOther projectOther) {
        super(application, title);

        this.projectOther = projectOther;

        initializeComponents();
        initializeLayouts();
        updateComponents(this.projectOther);
    }

    private boolean checkChange() {
        return (projectOther != null) && !(projectOther.equals(originalOther));
    }

    private void showSaveDialog() {
        if (projectOther != null) {
            String msg = projectOther.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    projectOther.createName();
                    projectOther.save(); // TODO: dont save here, only OK button
                    projectOther = originalOther.createCopy();
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
        if (projectOther.getDirectory().isEmpty()) {
            directoryTf.setError("Directory can not be empty..");
            ok = false;
        } else {
            File dir = new File(projectOther.getDirectory());
            if (!dir.exists()) {
                directoryTf.setError("Directory does not exist..");
                ok = false;
            }
        }

        if (ok) {
            if (projectOther.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                if (projectOther.getProjectIDE().isUseParentFolder()) {
                    File parent = new File(projectOther.getDirectory());
                    if (!(parent.exists() && parent.isDirectory() && FileUtils.contains(parent, projectOther.getProjectIDE().getExtension()))) {
                        directoryTf.setWarning("Directory does not match with IDE");
                    }
                } else {
                    File file = new File(projectOther.getDirectory());
                    if (!(file.exists() && FileUtils.is(file, projectOther.getProjectIDE().getExtension()))) {
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
        if (projectOther != null && originalOther != null) {
            originalOther.createCopy(projectOther);
            projectOther.setCanBeSaved(true);
        }

        super.onCancel();
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            projectOther.createName();
            projectOther.save(); // TODO dont save here
            originalOther = projectOther.createCopy();
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
            return projectOther;
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
        if (projectOther.getProject() != null) {
            home = projectOther.getProject().getMainDirectory();
        }
        if (home.isEmpty()) {
            home = "/home/waldo/Documents/";
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setCurrentDirectory(new File(home));

        if (fileChooser.showDialog(EditProjectOtherDialog.this, "Select") == JFileChooser.APPROVE_OPTION) {
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
            directoryTf.setError(null);
            directoryTf.setText(dir);
            projectOther.setDirectory(dir);
            verify();
            onValueChanged(directoryTf, "", 0,0);
        }
    }
}