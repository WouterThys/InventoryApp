package com.waldo.inventory.gui.dialogs.projecttypesdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import com.waldo.inventory.gui.dialogs.projecttypesdialog.detectiondialog.DetectionDialog;
import com.waldo.inventory.gui.dialogs.projecttypesdialog.launcherdialog.LauncherDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.Application.imageResource;

public class ProjectTypesDialog extends ProjectTypesDialogLayout {

    private boolean canClose = true;

    public ProjectTypesDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        db().addOnProjectTypeChangedListener(this);

        updateComponents(null);
    }

    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog(true);
        }

        if (canClose) {
            dialogResult = OK;
            dispose();
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            selectedProjectType.save();
            originalProjectType = selectedProjectType.createCopy();
            getButtonNeutral().setEnabled(false);
        }

    }

    private void setDetails() {
        if (selectedProjectType != null) {
            detailName.setText(selectedProjectType.getName());

            if (!selectedProjectType.getIconPath().isEmpty()) {
                detailLogo.setIcon(selectedProjectType.getIconPath(), 48,48);
            } else {
                detailLogo.setIcon(imageResource.readImage("Common.UnknownIcon48"));
            }

            detailProjectModel.removeAllElements();
            for (Project p : db().getProjectForProjectType(selectedProjectType.getId())) {
                detailProjectModel.addElement(p);
            }
        }
    }

    private void clearDetails() {
        detailName.setText("");
        detailLogo.setIcon((Icon) null);
        detailProjectModel.removeAllElements();
    }

    private void showSaveDialog(boolean closeAfter) {
        if (selectedProjectType != null) {
            String msg = selectedProjectType.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
//                    selectedManufacturer.setName(detailName.getText());
//                    selectedManufacturer.setWebsite(detailWebsite.getText());
                    selectedProjectType.save();
                    originalProjectType = selectedProjectType.createCopy();
                    if (closeAfter) {
                        dialogResult = OK;
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
        if (detailName.getText().isEmpty()) {
            detailName.setError("Name can't be empty");
            ok = false;
        }

        // More verifying??
        return ok;
    }

    private boolean checkChange() {
        return (selectedProjectType != null) && !(selectedProjectType.equals(originalProjectType));
    }

    //
    // Update listener
    //

    @Override
    public void updateComponents(Object object) {
        try {
            application.beginWait();
            // Get all menus
            projectTypeModel.removeAllElements();
            for (ProjectType pt : db().getProjectTypes()) {
                if (!pt.isUnknown()) {
                    projectTypeModel.addElement(pt);
                }
            }

            selectedProjectType = (ProjectType) object;
            updateEnabledComponents();

            if (selectedProjectType != null) {
                originalProjectType = selectedProjectType.createCopy();
                projectTypeList.setSelectedValue(selectedProjectType, true);
                setDetails();
            } else {
                originalProjectType = null;
            }
        } finally {
            application.endWait();
        }
    }


    //
    // Search listener
    //

    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        ProjectType ptFound = (ProjectType) foundObjects.get(0);
        projectTypeList.setSelectedValue(ptFound, true);
    }

    @Override
    public void onSearchCleared() {
        projectTypeList.setSelectedValue(selectedProjectType, true);
    }

    @Override
    public void nextSearchObject(DbObject next) {
        projectTypeList.setSelectedValue(next, true);
    }

    @Override
    public void previousSearchObject(DbObject previous) {
        projectTypeList.setSelectedValue(previous, true);
    }

    //
    // ProjectType listener
    //
    @Override
    public void onAdded(ProjectType projectType) {
        updateComponents(projectType);
    }

    @Override
    public void onUpdated(ProjectType newProjectType, ProjectType oldProjectType) {
        updateComponents(newProjectType);
    }

    @Override
    public void onDeleted(ProjectType projectType) {
        updateComponents(null);
    }


    //
    // List selection listener
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            JList list = (JList) e.getSource();
            Object selected = list.getSelectedValue();

            if (checkChange()) {
                showSaveDialog(false);
            }
            getButtonNeutral().setEnabled(false);
            updateComponents(selected);
            if (selectedProjectType != null && !selectedProjectType.isUnknown()) {
                setDetails();
            } else {
                clearDetails();
            }
        }
    }

    //
    // Tool bar
    //

    @Override
    public void onToolBarRefresh() {
        updateComponents(null);
    }

    @Override
    public void onToolBarAdd() {
        DbObjectDialog<ProjectType> dialog = new DbObjectDialog<>(application, "New Project Type", new ProjectType());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            ProjectType pt = dialog.getDbObject();
            pt.save();
        }
    }

    @Override
    public void onToolBarDelete() {
        if (selectedProjectType != null) {
            int res = JOptionPane.showConfirmDialog(ProjectTypesDialog.this, "Are you sure you want to delete \"" + selectedProjectType.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedProjectType.delete();
                selectedProjectType = null;
                originalProjectType = null;
            }
        }
    }

    @Override
    public void onToolBarEdit() {
        if (selectedProjectType != null) {
            DbObjectDialog<ProjectType> dialog = new DbObjectDialog<>(application, "Update " + selectedProjectType.getName(), selectedProjectType);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedProjectType.save();
                originalProjectType = selectedProjectType.createCopy();
            }
        }
    }

    //
    // Fields edited
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        return selectedProjectType;
    }

    //
    // Buttons clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (selectedProjectType != null) {
            if (source == detailLauncherBtn) {
                LauncherDialog dialog = new LauncherDialog(application, "Launcher",
                        selectedProjectType.isUseDefaultLauncher(),
                        selectedProjectType.getLauncherPath());

                if (dialog.showDialog() == IDialog.OK) {
                    selectedProjectType.setUseDefaultLauncher(dialog.isUseDefaultLauncher());
                    selectedProjectType.setLauncherPath(dialog.getLauncherPath());
                    getButtonNeutral().setEnabled(checkChange());
                }
            }
            if (source == detailDetectionBtn) {
                DetectionDialog dialog = new DetectionDialog(application, "Detection",
                        selectedProjectType.getExtension(),
                        selectedProjectType.isOpenAsFolder(),
                        selectedProjectType.isMatchExtension(),
                        selectedProjectType.isUseParentFolder());

                if (dialog.showDialog() == IDialog.OK) {
                    selectedProjectType.setExtension(dialog.getExtension());
                    selectedProjectType.setOpenAsFolder(dialog.isOpenAsFolder());
                    selectedProjectType.setMatchExtension(dialog.isMatchExtension());
                    selectedProjectType.setUseParentFolder(dialog.isUseParentFolder());
                    getButtonNeutral().setEnabled(checkChange());
                }
            }
        }
    }
}
