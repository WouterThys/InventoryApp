package com.waldo.inventory.gui.panels.projectspanel;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import static com.waldo.inventory.database.DbManager.db;

public class ProjectsPanel extends ProjectsPanelLayout {

    private DbObjectChangedListener<Project> projectChanged;

    public ProjectsPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initializeListeners();

        db().addOnProjectChangedListener(projectChanged);

        updateWithFirstProject();
    }

    private void updateWithFirstProject() {
        if (db().getProjects().size() > 0) {
            updateComponents(db().getProjects().get(0));
        } else {
            updateComponents(null);
        }
    }

    private void initializeListeners() {
        setProjectChangedListener();
    }

    private void setProjectChangedListener() {
        projectChanged = new DbObjectChangedListener<Project>() {
            @Override
            public void onInserted(Project object) {

            }

            @Override
            public void onUpdated(Project object) {

            }

            @Override
            public void onDeleted(Project object) {

            }

            @Override
            public void onCacheCleared() {

            }
        };
    }

    public TopToolBar getToolBar() {
        return topToolBar;
    }

    //
    // Tool bars
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        if (source.equals(projectsToolBar)) {
            updateComponents(selectedProject);
        }
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (source.equals(projectsToolBar)) {
//            AddProjectDialog dialog = new AddProjectDialog(application, "New Project");
//            if (dialog.showDialog() == IDialog.OK) {
//                // Add Project
//                Project p = dialog.getProject();
//                p.save();
//            }
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedProject != null) {
            int res = JOptionPane.showConfirmDialog(ProjectsPanel.this, "Are you sure you want to delete \"" + selectedProject.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                application.beginWait();
                try {
                    selectedProject.delete();
                } finally {
                    application.endWait();
                }
                selectedProject = null;
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProject != null) {
//            AddProjectDialog dialog = new AddProjectDialog(application, "New Project", selectedProject);
//            if (dialog.showDialog() == IDialog.OK) {
//                // Add order
//                Project p = dialog.getProject();
//                p.save();
//            }
        }
    }

    //
    // Tree value changed
    //
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!application.isUpdating()) {
            application.beginWait();
            try {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectsTree.getLastSelectedPathComponent();

                if (node == null) {
                    selectedProject = null;
                    return; // Nothing selected
                }
                DbObject object = (DbObject) node.getUserObject();
                if (object.isUnknown()) {
                    selectedProject = null;
                    return; // Not a selectable node
                }

                treeCollapseAll();
                int tabToSelect = 0;
                switch (DbObject.getType(object)) {
                    case DbObject.TYPE_PROJECT_CODE:
                        tabToSelect = TAB_CODE;
                        selectedProject = (Project) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
                        break;
                    case DbObject.TYPE_PROJECT_PCB:
                        tabToSelect = TAB_PCBS;
                        selectedProject = (Project) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
                        break;
                    case DbObject.TYPE_PROJECT_OTHER:
                        tabToSelect = TAB_OTHER;
                        selectedProject = (Project) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
                        break;
                    case DbObject.TYPE_PROJECT:
                        tabToSelect = TAB_CODE;
                        selectedProject = (Project) object;
                        break;
                }

                application.clearSearch();
                selectTreeTab(tabToSelect, selectedProject);
                selectTab(tabToSelect, selectedProject);

                updateToolBar();
                updateVisibleComponents();
                updateEnabledComponents();
            }
            finally {
                application.endWait();
            }
        }
    }

    //
    // Tab index changed
    //
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(tabbedPane)) {
            if (!application.isUpdating()) {
                application.beginWait();
                try {
                    if (selectedProject != null) {
                        int tab = tabbedPane.getSelectedIndex();
                        updatePanels(tab, selectedProject);
                        selectTreeTab(tab, selectedProject);
                    }
                } finally {
                    application.endWait();
                }
            }
        }
    }
}
