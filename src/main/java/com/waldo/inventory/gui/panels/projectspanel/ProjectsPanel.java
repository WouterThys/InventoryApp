package com.waldo.inventory.gui.panels.projectspanel;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.editprojectdialog.EditProjectCacheDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class ProjectsPanel extends ProjectsPanelLayout implements CacheChangedListener<Project> {


    public ProjectsPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();

        cache().addListener(Project.class, this);

        updateWithFirstProject();
    }

    private void updateWithFirstProject() {
        if (cache().getProjects().size() > 0) {
            updateComponents(cache().getProjects().get(0));
        } else {
            updateComponents();
        }
    }

    private void updateProjectObjects(Project project) {
        project.updateProjectCodes();
        project.updateProjectPcbs();
        project.updateProjectOthers();
    }


    //
    // Cache listener
    @Override
    public void onInserted(Project project) {
        selectedProject = project;
        updateProjectObjects(project);

        updatePanels(TAB_CODE, project);
        projectsTree.addProject(project);
        final long projectId = project.getId();

        SwingUtilities.invokeLater(() -> {
            selectedProject = sm().findProjectById(projectId);
            treeSelectProject(selectedProject);

            updateVisibleComponents();
            updateEnabledComponents();
        });

    }

    @Override
    public void onUpdated(Project project) {
        selectedProject = project;
        updateProjectObjects(project);

        updatePanels(getSelectedTab(), project);
        final long projectId = treeUpdate();

        SwingUtilities.invokeLater(() -> {
            selectedProject = sm().findProjectById(projectId);
            treeSelectProject(selectedProject);

            updateVisibleComponents();
            updateEnabledComponents();
        });
    }

    @Override
    public void onDeleted(Project project) {
        selectedProject = null;

        treeDeleteProject(project);
        updateVisibleComponents();
        updateEnabledComponents();
    }

    @Override
    public void onCacheCleared() {

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
            EditProjectCacheDialog dialog = new EditProjectCacheDialog(application, "New Project", new Project());
            dialog.showDialog();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedProject != null) {
            int res = JOptionPane.showConfirmDialog(ProjectsPanel.this, "Are you sure you want to delete \"" + selectedProject.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedProject.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedProject != null) {
            EditProjectCacheDialog dialog = new EditProjectCacheDialog(application, "Edit Project", selectedProject);
            dialog.showDialog();
        }
    }

    //
    // Tree value changed
    //
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!Application.isUpdating(ProjectsPanel.this)) {
            Application.beginWait(ProjectsPanel.this);
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

                //treeCollapseAll();
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
                        clearSelectedProjectObject();
                        break;
                }

                selectTreeTab(tabToSelect, selectedProject);
                selectTab(tabToSelect, selectedProject);

                updateToolBar();
                updateVisibleComponents();
                updateEnabledComponents();
            } finally {
                Application.endWait(ProjectsPanel.this);
            }
        }
    }

    //
    // Tab index changed
    //
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(tabbedPane)) {
            if (!Application.isUpdating(ProjectsPanel.this)) {
                Application.beginWait(ProjectsPanel.this);
                try {
                    if (selectedProject != null) {
                        int tab = tabbedPane.getSelectedIndex();
                        updatePanels(tab, selectedProject);
                        selectTreeTab(tab, selectedProject);

                        updateVisibleComponents();
                    }
                } finally {
                    Application.endWait(ProjectsPanel.this);
                }
            }
        }
    }
}
