package com.waldo.inventory.gui.panels.projectspanel;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.ITree;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.treemodels.IDbObjectTreeModel;
import com.waldo.inventory.gui.panels.projectspanel.panels.ProjectCodePanel;
import com.waldo.inventory.gui.panels.projectspanel.panels.ProjectOtherPanel;
import com.waldo.inventory.gui.panels.projectspanel.panels.ProjectPcbPanel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class ProjectsPanelLayout extends JPanel implements
        GuiInterface,
        TreeSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ChangeListener {

    static final int TAB_CODE = 0;
    static final int TAB_PCBS = 1;
    static final int TAB_OTHER = 2;


     /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITree projectsTree;
    IDbObjectTreeModel<Project> treeModel;

    TopToolBar topToolBar;
    IdBToolBar projectsToolBar;

    JTabbedPane tabbedPane;
    ProjectCodePanel projectCodePanel;
    ProjectPcbPanel projectPcbPanel;
    ProjectOtherPanel projectOtherPanel;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    Project selectedProject;


    /*
    *                  CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectsPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean enabled =  !(selectedProject == null || selectedProject.isUnknown() || !selectedProject.canBeSaved());
        projectsToolBar.setEditActionEnabled(enabled);
        projectsToolBar.setDeleteActionEnabled(enabled);
    }

    void updateVisibleComponents() {

    }

    void selectTab(int tab, Project project) {
        switch (tab) {
            case TAB_CODE:
                projectCodePanel.updateComponents(project);
                break;
            case TAB_PCBS:
                projectPcbPanel.updateComponents(project);
                break;
            case TAB_OTHER:
                projectOtherPanel.updateComponents(project);
                break;
            default:
                break;
        }
        tabbedPane.setSelectedIndex(tab);
    }

    void selectTreeTab(int tab, Project project) {
        DefaultMutableTreeNode projectNode = treeModel.findNode(project);
        if (projectNode != null) {
            TreeNode node = projectNode.getChildAt(tab);
            treeModel.setSelectedNode(node);
        }
    }

    ProjectPcb treeGetPcbTab(Project project) {
        TreeNode node = treeModel.findNode(project);
        if (node != null) {
            return (ProjectPcb) ((DefaultMutableTreeNode) node.getChildAt(TAB_PCBS)).getUserObject();
        }
        return null;
    }

    private void treeInitializeTree(DefaultMutableTreeNode rootNode) {
        for (Project p : db().getProjects()) {
            if (!p.isUnknown()) {
                ProjectCode code = new ProjectCode("Code");
                ProjectPcb pcb = new ProjectPcb("Pcbs");
                ProjectOther other = new ProjectOther("Other");

                code.setCanBeSaved(false);
                pcb.setCanBeSaved(false);
                other.setCanBeSaved(false);

                DefaultMutableTreeNode codeNode = new DefaultMutableTreeNode(code, false);
                DefaultMutableTreeNode pcbNode = new DefaultMutableTreeNode(pcb, false);
                DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode(other, false);
                DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(p);

                projectNode.add(codeNode);
                projectNode.add(pcbNode);
                projectNode.add(otherNode);

                rootNode.add(projectNode);
            }
        }
    }

    void treeRecreateNodes() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        treeInitializeTree(rootNode);
    }

    void treeDeleteProject(Project project) {
        try {
            treeModel.removeObject(project);
        } catch (Exception e) {
            Status().setError("Failed to remove project " + project.getName() + " from tree", e);
        }
    }

    long treeUpdate() {
        long projectId = -1;
        if (selectedProject != null) {
            projectId = selectedProject.getId();
        }
        treeModel.reload();
        treeModel.expandNodes();
        return projectId;
    }

    void treeSelectProject(Project project) {
        treeModel.setSelectedObject(project);
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Project tree
        Project virtualRootProject = new Project("All");
        virtualRootProject.setCanBeSaved(false);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(virtualRootProject, true);
        treeInitializeTree(rootNode);
        treeModel = new IDbObjectTreeModel<> (rootNode, (rootNode1, child) -> {
            switch (DbObject.getType(child)) {
                case DbObject.TYPE_PROJECT:
                    break;
                case DbObject.TYPE_PROJECT_CODE:
                case DbObject.TYPE_PROJECT_PCB:
                case DbObject.TYPE_PROJECT_OTHER:
                    DefaultMutableTreeNode node = treeModel.findNode(child);
                    if (node != null) {
                        return (DefaultMutableTreeNode) node.getParent();
                    }
                    break;
                default:
                    break;
            }
            return null;
        });

        projectsTree = new ITree(treeModel);
        projectsTree.addTreeSelectionListener(this);
        projectsTree.setCellRenderer(ITree.getProjectsRenderer());
        treeModel.setTree(projectsTree);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);

        projectCodePanel = new ProjectCodePanel(application);
        projectPcbPanel = new ProjectPcbPanel(application);
        projectOtherPanel = new ProjectOtherPanel(application);

        tabbedPane.addTab("Code", imageResource.readImage("Common.Code", 16), projectCodePanel);
        tabbedPane.addTab("Pcbs", imageResource.readImage("Common.Pcb", 16), projectPcbPanel);
        tabbedPane.addTab("Other", imageResource.readImage("Common.Other", 16), projectOtherPanel);

        // Tree toolbar
        projectsToolBar = new IdBToolBar(this);

        // Top tool bar
        topToolBar = new TopToolBar(application, null);
        topToolBar.setDbToolbarVisible(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panels
        JPanel westPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());

        projectsTree.setPreferredSize(new Dimension(300,200));
        JScrollPane pane = new JScrollPane(projectsTree);
        westPanel.add(pane, BorderLayout.CENTER);
        westPanel.add(projectsToolBar, BorderLayout.PAGE_END);

        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        centerPanel.add(topToolBar, BorderLayout.PAGE_START);
        // centerPanel.add(details?? .. )

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPanel, centerPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        if (application.isUpdating()) {
            return;
        }
        application.beginWait();
        try {
            if (object != null) {
                selectedProject = (Project) object;
            } else {
                selectedProject = null;
            }

            updateEnabledComponents();
            updateVisibleComponents();

            // Details?

        } finally {
            application.endWait();
        }

    }
}
