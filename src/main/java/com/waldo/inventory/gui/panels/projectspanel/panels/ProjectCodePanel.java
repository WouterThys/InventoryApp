package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectCode;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.treemodels.IFileTreeModel;
import com.waldo.inventory.gui.panels.projectpanel.extras.ProjectGirdPanel;
import com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectcodedialog.EditProjectCodeDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProjectCodePanel extends JPanel implements
        GuiInterface,
        IdBToolBar.IdbToolBarListener,
        DbObjectChangedListener<ProjectCode> {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ProjectGirdPanel<ProjectCode> gridPanel;
    private IdBToolBar codeToolBar;
    private JTree codeFilesTree;
    private IFileTreeModel fileTreeModel;
    private ProjectCodeDetailPanel detailPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    private Project selectedProject;
    private ProjectCode selectedProjectCode;


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectCodePanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();

        DbManager.db().addOnProjectCodeChangedListener(this);

        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        boolean enabled = selectedProjectCode != null;
        codeToolBar.setDeleteActionEnabled(enabled);
        codeToolBar.setEditActionEnabled(enabled);
    }

    private void updateDetails() {
        detailPanel.updateComponents(selectedProjectCode);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Center
        gridPanel = new ProjectGirdPanel<>(projectObject -> {
            selectedProjectCode = projectObject;
            fileTreeModel = new IFileTreeModel(new File(selectedProjectCode.getDirectory()));
            codeFilesTree.setModel(fileTreeModel);
            updateEnabledComponents();
            updateDetails();
        });
        codeToolBar = new IdBToolBar(this);

        // East
        codeFilesTree = new JTree();
        codeFilesTree.setCellRenderer(ITree.getFilesRenderer());
        detailPanel = new ProjectCodeDetailPanel(application);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panels
        JPanel eastPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Add stuff to panels
        centerPanel.add(gridPanel, BorderLayout.CENTER);
        centerPanel.add(codeToolBar, BorderLayout.PAGE_START);

        JScrollPane pane = new JScrollPane(codeFilesTree);
        eastPanel.add(pane, BorderLayout.CENTER);
        eastPanel.add(detailPanel, BorderLayout.SOUTH);
        eastPanel.setMinimumSize(new Dimension(300, 0));

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, eastPanel);
        splitPane.setResizeWeight(.5d);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            selectedProject = (Project) object;
            gridPanel.drawTiles(selectedProject.getProjectCodes());
        } else {
            selectedProject = null;
        }
        selectedProjectCode = null;
        fileTreeModel = new IFileTreeModel();
        codeFilesTree.setModel(fileTreeModel);
        updateEnabledComponents();
        updateDetails();
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateComponents(selectedProject);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedProject != null) {
            ProjectCode newProjectCode = new ProjectCode(selectedProject.getId());
            EditProjectCodeDialog dialog = new EditProjectCodeDialog(application, "Add code", newProjectCode);
            dialog.showDialog();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedProjectCode != null) {
            int result = JOptionPane.showConfirmDialog(
                    ProjectCodePanel.this,
                    "Are you sure you want to delete " + selectedProjectCode.getName() + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                selectedProjectCode.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProjectCode != null) {
            EditProjectCodeDialog dialog = new EditProjectCodeDialog(application, "Edit " + selectedProjectCode.getName(), selectedProjectCode);
            if (dialog.showDialog() == IDialog.OK) {
                ProjectCode newProjectCode = dialog.getProjectCode();
                newProjectCode.save();
            }
        }
    }

    //
    // Project code changed
    //
    @Override
    public void onInserted(ProjectCode object) {
        selectedProject.updateProjectCodes();
        gridPanel.addTile(object);
        selectedProjectCode = object;
        updateEnabledComponents();
    }

    @Override
    public void onUpdated(ProjectCode object) {
        gridPanel.drawTiles(selectedProject.getProjectCodes());
        updateEnabledComponents();
    }

    @Override
    public void onDeleted(ProjectCode object) {
        selectedProject.updateProjectCodes();
        gridPanel.removeTile(object);
        selectedProjectCode = null;
        updateEnabledComponents();
    }

    @Override
    public void onCacheCleared() {

    }
}