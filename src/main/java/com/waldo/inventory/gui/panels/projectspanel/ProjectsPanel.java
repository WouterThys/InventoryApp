package com.waldo.inventory.gui.panels.projectspanel;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.editprojectdialog.EditProjectDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class ProjectsPanel extends ProjectsPanelLayout {

    private CacheChangedListener<Project> projectChanged;

    public ProjectsPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initializeListeners();

        cache().addOnProjectChangedListener(projectChanged);

        updateWithFirstProject();
    }

    private void updateWithFirstProject() {
        if (cache().getProjects().size() > 0) {
            updateComponents(cache().getProjects().get(0));
        } else {
            updateComponents();
        }
    }

    private void initializeListeners() {
        setProjectChangedListener();
    }

    private void updateProjectObjects(Project project) {
        updateProjectCodes(project);
        updateProjectPcbs(project);
        updateProjectOthers(project);
    }

    private void updateProjectCodes(Project project) {
        List<ProjectCode> knownCodes = new ArrayList<>(sm().findProjectCodesByProjectId(project.getId()));

        for (ProjectCode code : project.getProjectCodes()) {
            code.setProjectId(project.getId());
            int ndx = knownCodes.indexOf(code);

            if (ndx >= 0) {
                code.setId(knownCodes.get(ndx).getId());
                code.setRemarksFile(knownCodes.get(ndx).getRemarksFile());
                knownCodes.remove(ndx);
            }
            code.save();
        }

        for (ProjectCode code : knownCodes) {
            code.delete();
        }
    }

    private void updateProjectPcbs(Project project) {
        List<ProjectPcb> knownPcbs = new ArrayList<>(sm().findProjectPcbsByProjectId(project.getId()));

        for (ProjectPcb pcb : project.getProjectPcbs()) {
            pcb.setProjectId(project.getId());
            int ndx = knownPcbs.indexOf(pcb);

            if (ndx >= 0) {
                pcb.setId(knownPcbs.get(ndx).getId());
                pcb.setRemarksFile(knownPcbs.get(ndx).getRemarksFile());
                knownPcbs.remove(ndx);
            }
            pcb.save();
        }

        for (ProjectPcb pcb : knownPcbs) {
            pcb.delete();
        }
    }

    private void updateProjectOthers(Project project) {
        List<ProjectOther> knownOthers = new ArrayList<>(sm().findProjectOthersByProjectId(project.getId()));

        for (ProjectOther other : project.getProjectOthers()) {
            other.setProjectId(project.getId());
            int ndx = knownOthers.indexOf(other);

            if (ndx >= 0) {
                other.setId(knownOthers.get(ndx).getId());
                other.setRemarksFile(knownOthers.get(ndx).getRemarksFile());
                knownOthers.remove(ndx);
            }
            other.save();
        }

        for (ProjectOther other : knownOthers) {
            other.delete();
        }
    }

    private void setProjectChangedListener() {
        projectChanged = new CacheChangedListener<Project>() {
            @Override
            public void onInserted(Project project) {
                selectedProject = project;
                updateProjectObjects(project);

                updatePanels(TAB_CODE, project);
                treeRecreateNodes();
                final long projectId = treeUpdate();

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
                treeRecreateNodes();
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
            EditProjectDialog dialog = new EditProjectDialog(application, "New Project");
            if (dialog.showDialog() == IDialog.OK) {
                // Add Project
                Project p = dialog.getProject();
                p.save();
            }
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
            EditProjectDialog dialog = new EditProjectDialog(application, "New Project", selectedProject);
            if (dialog.showDialog() == IDialog.OK) {
                // Edit project
                Project p = dialog.getProject();
                p.save();
            }
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
                        clearSelectedProjectObject();
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

                        updateVisibleComponents();
                    }
                } finally {
                    application.endWait();
                }
            }
        }
    }
}
