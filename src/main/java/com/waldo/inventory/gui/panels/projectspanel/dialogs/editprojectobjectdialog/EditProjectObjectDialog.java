package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectobjectdialog;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class EditProjectObjectDialog<P extends ProjectObject> extends EditProjectObjectDialogLayout {

    private boolean canClose;

    public EditProjectObjectDialog(Application application, String title, P projectObject) {
        super(application, title);

        this.projectObject = projectObject;

        initializeComponents();
        initializeLayouts();
        updateComponents(projectObject);
    }

    private boolean checkChange() {
        return (projectObject != null) && !(projectObject.equals(originalObject));
    }

    private void showSaveDialog() {
        if (projectObject != null) {
            String msg = projectObject.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    projectObject.createName();
                    projectObject.save(); // TODO: dont save here, only OK button
                    projectObject = (ProjectObject) originalObject.createCopy();
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
        if (projectObject.getDirectory().isEmpty()) {
            directoryTf.setError("Directory can not be empty..");
            ok = false;
        } else {
            File dir = new File(projectObject.getDirectory());
            if (!dir.exists()) {
                directoryTf.setError("Directory does not exist..");
                ok = false;
            }
        }

        if (ok) {
            if (projectObject.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                if (projectObject.getProjectIDE().isUseParentFolder()) {
                    File parent = new File(projectObject.getDirectory());
                    if (!(parent.exists() && parent.isDirectory() && FileUtils.contains(parent, projectObject.getProjectIDE().getExtension()))) {
                        directoryTf.setWarning("Directory does not match with IDE");
                    }
                } else {
                    File file = new File(projectObject.getDirectory());
                    if (!(file.exists() && FileUtils.is(file, projectObject.getProjectIDE().getExtension()))) {
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
        if (projectObject != null && originalObject != null) {
            originalObject.createCopy(projectObject);
            projectObject.setCanBeSaved(true);
        }

        super.onCancel();
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            projectObject.createName();
            projectObject.save(); // TODO dont save here
            originalObject = (ProjectObject) projectObject.createCopy();
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
            return projectObject;
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
        if (projectObject.getProject() != null) {
            home = projectObject.getProject().getMainDirectory();
        }
        if (home.isEmpty()) {
            home = "/home/waldo/Documents/";
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setCurrentDirectory(new File(home));

        if (fileChooser.showDialog(EditProjectObjectDialog.this, "Select") == JFileChooser.APPROVE_OPTION) {
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
            directoryTf.setError(null);
            directoryTf.setText(dir);
            projectObject.setDirectory(dir);
            verify();
            onValueChanged(directoryTf, "", 0,0);
        }
    }
}