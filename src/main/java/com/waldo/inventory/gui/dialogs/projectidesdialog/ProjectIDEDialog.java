package com.waldo.inventory.gui.dialogs.projectidesdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import com.waldo.inventory.gui.dialogs.projectidesdialog.detectiondialog.DetectionDialog;
import com.waldo.inventory.gui.dialogs.projectidesdialog.launcherdialog.LauncherDialog;
import com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog.ParserDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ProjectIDEDialog extends ProjectIDEDialogLayout implements CacheChangedListener<ProjectIDE> {

    private boolean canClose = true;

    public ProjectIDEDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        cache().addListener(ProjectIDE.class, this);

        updateWithFirstProjectType();
    }

    private void updateWithFirstProjectType() {
        if (cache().getProjectIDES().size() > 0) {
            updateComponents(cache().getProjectIDES().get(0));
        } else {
            updateComponents();
        }
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
            selectedProjectIDE.save();
            originalProjectIDE = selectedProjectIDE.createCopy();
            getButtonNeutral().setEnabled(false);
        }
    }

    @Override
    protected void onCancel() {
        if (selectedProjectIDE != null && originalProjectIDE != null) {
            originalProjectIDE.createCopy(selectedProjectIDE);
            selectedProjectIDE.setCanBeSaved(true);
        }
        super.onCancel();
    }

    private void setDetails() {
        if (selectedProjectIDE != null) {
            detailName.setText(selectedProjectIDE.getName());
            projectTypeCb.setSelectedItem(selectedProjectIDE.getProjectType());

            detailProjectModel.removeAllElements();
            for (Project project : SearchManager.sm().findProjectsWithIde(selectedProjectIDE.getId())) {
                detailProjectModel.addElement(project);
            }
        } else {
            detailName.setText("");
        }
        detailLogo.updateComponents(selectedProjectIDE);
    }

    private void clearDetails() {
        detailName.setText("");
        projectTypeCb.setSelectedItem(null);
        detailLogo.updateComponents((DbObject) null);
        detailProjectModel.removeAllElements();
    }

    private void showSaveDialog(boolean closeAfter) {
        if (selectedProjectIDE != null) {
            String msg = selectedProjectIDE.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
//                    selectedManufacturer.setNameTxt(detailName.getText());
//                    selectedManufacturer.setText(detailWebsite.getText());
                    selectedProjectIDE.save();
                    originalProjectIDE = selectedProjectIDE.createCopy();
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
        return (selectedProjectIDE != null) && !(selectedProjectIDE.equals(originalProjectIDE));
    }

    //
    // Update listener
    //

    @Override
    public void updateComponents(Object... object) {
        beginWait();
        try {
            // Get all menus
            setProjectIdeList(cache().getProjectIDES());

            selectedProjectIDE = (ProjectIDE) object[0];
            updateEnabledComponents();

            if (selectedProjectIDE != null) {
                originalProjectIDE = selectedProjectIDE.createCopy();
                projectIdeList.setSelectedValue(selectedProjectIDE, true);
                setDetails();
            } else {
                originalProjectIDE = null;
            }
        } finally {
            endWait();
        }
    }


    //
    // Search listener
    //
    @Override
    public void onObjectsFound(List<ProjectIDE> foundObjects) {
        if (foundObjects != null && foundObjects.size() > 0) {
            setProjectIdeList(foundObjects);
            ProjectIDE m = foundObjects.get(0);
            projectIdeList.setSelectedValue(m, true);
            searchPanel.setCurrentObject(m);
        } else {
            searchPanel.clearSearch();
        }
    }

    @Override
    public void onNextObjectSelected(ProjectIDE next) {
        projectIdeList.setSelectedValue(next, true);
    }

    @Override
    public void onPreviousObjectSelected(ProjectIDE previous) {
        projectIdeList.setSelectedValue(previous, true);
    }

    @Override
    public void onSearchCleared() {
        updateComponents(selectedProjectIDE);
    }

    //
    // ProjectIDE listener
    //
    @Override
    public void onInserted(ProjectIDE projectIDE) {
        updateComponents(projectIDE);
    }

    @Override
    public void onUpdated(ProjectIDE newProjectIDE) {
        updateComponents(newProjectIDE);
    }

    @Override
    public void onDeleted(ProjectIDE projectIDE) {
        updateWithFirstProjectType();
    }

    @Override
    public void onCacheCleared() {}

    //
    // List selection listener
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !isUpdating()) {
            JList list = (JList) e.getSource();
            Object selected = list.getSelectedValue();

            if (checkChange()) {
                showSaveDialog(false);
            }
            getButtonNeutral().setEnabled(false);
            updateComponents(selected);
            if (selectedProjectIDE != null && !selectedProjectIDE.isUnknown()) {
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
    public void onToolBarRefresh(IdBToolBar source) {
        updateWithFirstProjectType();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        DbObjectDialog<ProjectIDE> dialog = new DbObjectDialog<>(this, "New Project Type", new ProjectIDE());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            ProjectIDE pt = dialog.getDbObject();
            pt.save();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedProjectIDE != null) {
            int res = JOptionPane.showConfirmDialog(ProjectIDEDialog.this, "Are you sure you want to delete \"" + selectedProjectIDE.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedProjectIDE.delete();
                selectedProjectIDE = null;
                originalProjectIDE = null;
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectIDE != null) {
            DbObjectDialog<ProjectIDE> dialog = new DbObjectDialog<>(this, "Update " + selectedProjectIDE.getName(), selectedProjectIDE);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedProjectIDE.save();
                originalProjectIDE = selectedProjectIDE.createCopy();
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
        return selectedProjectIDE;
    }

    //
    // Buttons clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (selectedProjectIDE != null) {
            if (source == detailLauncherBtn) {
                LauncherDialog dialog = new LauncherDialog(this, "Launcher",
                        selectedProjectIDE.isUseDefaultLauncher(),
                        selectedProjectIDE.getLauncherPath());

                if (dialog.showDialog() == IDialog.OK) {
                    selectedProjectIDE.setUseDefaultLauncher(dialog.isUseDefaultLauncher());
                    selectedProjectIDE.setLauncherPath(dialog.getLauncherPath());
                    getButtonNeutral().setEnabled(checkChange());
                }
            }
            if (source == detailDetectionBtn) {
                DetectionDialog dialog = new DetectionDialog(this, "Detection",
                        selectedProjectIDE.getExtension(),
                        selectedProjectIDE.isOpenAsFolder(),
                        selectedProjectIDE.isMatchExtension(),
                        selectedProjectIDE.isUseParentFolder());

                if (dialog.showDialog() == IDialog.OK) {
                    selectedProjectIDE.setExtension(dialog.getExtension());
                    selectedProjectIDE.setOpenAsFolder(dialog.isOpenAsFolder());
                    selectedProjectIDE.setMatchExtension(dialog.isMatchExtension());
                    selectedProjectIDE.setUseParentFolder(dialog.isUseParentFolder());
                    getButtonNeutral().setEnabled(checkChange());
                }
            }
            if (source == detailParserBtn) {
                ParserDialog dialog = new ParserDialog(this, "Parser", selectedProjectIDE.hasParser(), selectedProjectIDE.getPcbItemParser());

                if (dialog.showDialog() == IDialog.OK) {
                    if (selectedProjectIDE != null) {
                        if (dialog.useParser()) {
                            selectedProjectIDE.setParserName(dialog.getParser().getName());
                        } else {
                            selectedProjectIDE.setParserName("");
                        }
                        getButtonNeutral().setEnabled(checkChange());
                    }
                }
            }
        }
    }
}
