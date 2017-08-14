package com.waldo.inventory.gui.panels.projectpanel;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ProjectPanel extends ProjectPanelLayout {

    private static final LogManager LOG = LogManager.LOG(ProjectPanel.class);

    private DbObjectChangedListener<Project> projectsListener;
    private DbObjectChangedListener<ProjectDirectory> projectDirectoryListener;

    public ProjectPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();

        initActions();
        initListeners();

        DbManager.db().addOnProjectChangedListener(projectsListener);
        DbManager.db().addOnProjectDirectoryChangedListener(projectDirectoryListener);

        updateComponents(null);
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public TopToolBar getToolBar() {
        return topToolBar;
    }

    private void showTreeRightClickPopup(TreePath path, int x, int y) {
        projectTree.setSelectionPath(path);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectTree.getLastSelectedPathComponent();

        if (node != null) {
            DbObject obj = (DbObject)node.getUserObject();
            if (DbObject.getType(obj) == DbObject.TYPE_PROJECT_DIRECTORY) {

                ProjectDirectory directory = (ProjectDirectory) obj;

                JPopupMenu menu = new JPopupMenu ();
                AbstractAction openAction = new AbstractAction("Open in files", imageResource.readImage("MenuBar.OpenIcon")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        File file = new File(directory.getDirectory());
                        if (file.exists()) {
                            Desktop desktop;
                            if (file.isDirectory()) {
                                try {
                                    if (Desktop.isDesktopSupported()) {
                                        desktop = Desktop.getDesktop();
                                        desktop.open(file);
                                    }
                                } catch (IOException ex){
                                    LOG.error("Could not open file : " + file, ex);
                                }
                            } else {
                                try {
                                    if (Desktop.isDesktopSupported()) {
                                        desktop = Desktop.getDesktop();
                                        desktop.open(file.getParentFile());
                                    }
                                } catch (IOException ex){
                                    LOG.error("Could not open file : " + file, ex);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(ProjectPanel.this,
                                    "Path " + directory.getDirectory() + " does not exist..",
                                    "Unknown directory",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                menu.add(openAction);
                menu.show(projectTree, x, y);

            }
        }
    }

    private void initActions() {
        projectTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = projectTree.getPathForLocation ( e.getX (), e.getY () );
                    Rectangle pathBounds = projectTree.getUI ().getPathBounds ( projectTree, path );
                    if ( pathBounds != null && pathBounds.contains ( e.getX (), e.getY () ) ) {
                        showTreeRightClickPopup(path, pathBounds.x, pathBounds.y + pathBounds.height );
                    }
                }
            }
        });
    }

    private void initListeners() {
        setProjectListener();
        setProjectDirectoryListener();
    }

    private void setProjectListener() {
        projectsListener = new DbObjectChangedListener<Project>() {
            @Override
            public void onInserted(Project project) {
                treeModel.addObject(project);
                updateComponents(project);
            }

            @Override
            public void onUpdated(Project newProject) {
                treeModel.updateObject(newProject);
                updateComponents(newProject);
            }

            @Override
            public void onDeleted(Project project) {
                treeModel.removeObject(project);
                selectedProject = null;
                selectedDirectory = null;
                updateComponents(null);
            }

            @Override
            public void onCacheCleared() {
                recreateNodes();
                updateComponents(selectedProject);
            }
        };
    }

    private void setProjectDirectoryListener() {
        projectDirectoryListener = new DbObjectChangedListener<ProjectDirectory>() {
            @Override
            public void onInserted(ProjectDirectory directory) {}

            @Override
            public void onUpdated(ProjectDirectory newDirectory) {}

            @Override
            public void onDeleted(ProjectDirectory directory) {}

            @Override
            public void onCacheCleared() {}
        };
    }

    private void validateProject(Project project) {
        if (project != null) {
            java.util.List<ProjectValidationError> errorList = project.validate();
            if (errorList.size() > 0) {
                StringBuilder errors = new StringBuilder();
                for (ProjectValidationError error : errorList) {
                    errors.append(error.toString()).append("\n");
                }

                int result = JOptionPane.showConfirmDialog(ProjectPanel.this,
                        "Project " + selectedProject.getName() + " encountered errors, do you want to reload? \n" + errors,
                        "Validation error",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    project.recreateProjectStructure();
                }
            }
        }
    }

    //
    // Top toolbar listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        if (selectedProject != null) {
            selectedProject.recreateProjectStructure();
        }
        updateComponents(selectedProject);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {

    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {

    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {

    }

    //
    // Tree value changed
    //
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!application.isUpdating()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectTree.getLastSelectedPathComponent();

            if (node == null) {
                selectedProject = null;
                selectedDirectory = null;
                return; // Nothing selected
            } else {
                DbObject obj = (DbObject)node.getUserObject();
                switch (DbObject.getType(obj)) {
                    case DbObject.TYPE_PROJECT:
                        selectedProject = (Project) obj;
                        selectedDirectory = null;

                        if (!selectedProject.isValidated()) {
                            validateProject(selectedProject);
                        }

                        break;
                    case DbObject.TYPE_PROJECT_DIRECTORY:
                        selectedDirectory = (ProjectDirectory) obj;
                        selectedProject = selectedDirectory.getProject();
                        break;
                }
            }

            application.clearSearch();

            updateComponents(selectedProject);
        }
    }


    //
    // Launch clicked in details panel
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (lastProjectFile != null && selectedProjectType != null) {
            try {
                Status().setMessage("Opening file: " + lastProjectFile.getAbsolutePath());
                selectedProjectType.launch(lastProjectFile);
            } catch (IOException e1) {
                LOG.error("Error opening project.", e1);
            }
        }
    }

    //
    // Project tile clicked
    //
    @Override
    public void onGridComponentClick(String name, ProjectType type, File file) {
        selectedProjectType = type;
        lastProjectFile = file;
        detailsPanel.updateComponents(selectedProject);
        projectTypeDetails.setProjectName(name);
        projectTypeDetails.updateComponents(selectedProjectType);

        if (type.hasParser()) {
            kiCadItemPanel.updateComponents(file);
        } else {
            kiCadItemPanel.updateComponents(null);
        }
    }
}
