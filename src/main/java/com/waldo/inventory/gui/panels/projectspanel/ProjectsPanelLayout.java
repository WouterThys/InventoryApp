package com.waldo.inventory.gui.panels.projectspanel;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.trees.IProjectTree;
import com.waldo.inventory.gui.panels.projectspanel.panels.code.ProjectCodePanel;
import com.waldo.inventory.gui.panels.projectspanel.panels.ProjectObjectPanel;
import com.waldo.inventory.gui.panels.projectspanel.panels.other.ProjectOtherPanel;
import com.waldo.inventory.gui.panels.projectspanel.panels.pcbs.ProjectPcbPanel;
import com.waldo.inventory.gui.panels.projectspanel.projectdetailpanel.ProjectDetailsPanel;
import com.waldo.inventory.managers.CacheManager;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ProjectsPanelLayout extends JPanel implements
        GuiUtils.GuiInterface,
        TreeSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ChangeListener,
        ProjectObjectPanel.ProjectObjectListener {

    static final int TAB_CODE = 0;
    static final int TAB_PCBS = 1;
    static final int TAB_OTHER = 2;


     /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    IProjectTree projectsTree;

    TopToolBar topToolBar;
    IdBToolBar projectsToolBar;
    private ProjectDetailsPanel detailsPanel;

    JTabbedPane tabbedPane;
    private ProjectCodePanel projectCodePanel;
    private ProjectPcbPanel projectPcbPanel;
    private ProjectOtherPanel projectOtherPanel;

    private ILabel tbProjectNameLbl;
    private ILabel tbProjectDirLbl;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;
    Project selectedProject;
    private Project rootProject;


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
        detailsPanel.updateComponents(getSelectedProjectObject());
    }

    void updateToolBar() {
        if (selectedProject != null) {
            tbProjectNameLbl.setText(selectedProject.getName());
            tbProjectDirLbl.setText(selectedProject.getMainDirectory());
        } else  {
            tbProjectNameLbl.setText("");
            tbProjectDirLbl.setText("");
        }
    }

    void selectTab(int tab, Project project) {
        updatePanels(tab, project);
        tabbedPane.setSelectedIndex(tab);
    }

    void updatePanels(int tab, Project project) {
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
    }

    void selectTreeTab(int tab, Project project) {
        DefaultMutableTreeNode projectNode = projectsTree.findNode(project);
        if (projectNode != null) {
            TreeNode node = projectNode.getChildAt(tab);
            projectsTree.setSelectedNode(node);
        }
    }

    void treeCollapseAll() {
        projectsTree.collapseAll();
    }

    int getSelectedTab() {
        return tabbedPane.getSelectedIndex();
    }

    void treeDeleteProject(Project project) {
        projectsTree.removeProject(project);
    }

    long treeUpdate() {
        long projectId = -1;
        if (selectedProject != null) {
            projectId = selectedProject.getId();
        }
        projectsTree.updateTree();
        //projectsTree.expandAll();
        return projectId;
    }

    void clearSelectedProjectObject() {
        projectCodePanel.setSelectedProjectObject(null);
        projectPcbPanel.setSelectedProjectObject(null);
        projectOtherPanel.setSelectedProjectObject(null);
    }

    private ProjectObject getSelectedProjectObject() {
        ProjectObject selected = null;
        switch (getSelectedTab()) {
            case TAB_CODE:
                selected = projectCodePanel.getSelectedProjectObject();
                break;
            case TAB_PCBS:
                selected = projectPcbPanel.getSelectedProjectObject();
                break;
            case TAB_OTHER:
                selected = projectOtherPanel.getSelectedProjectObject();
                break;
        }
        return selected;
    }

    void treeSelectProject(Project project) {
        projectsTree.setSelectedItem(project);
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Project tree
        rootProject = new Project("All");
        rootProject.setCanBeSaved(false);

        projectsTree = new IProjectTree(rootProject, false, false);
        projectsTree.addTreeSelectionListener(this);
        projectsTree.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                // TODO
            }
        });

        // Details
        detailsPanel = new ProjectDetailsPanel(application);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);

        projectCodePanel = new ProjectCodePanel(application, this);
        projectPcbPanel = new ProjectPcbPanel(application, this);
        projectOtherPanel = new ProjectOtherPanel(application, this);

        tabbedPane.addTab("Code ", imageResource.readIcon("Projects.Tab.Code"), projectCodePanel);
        tabbedPane.addTab("Pcbs ", imageResource.readIcon("Projects.Tab.Pcb"), projectPcbPanel);
        tabbedPane.addTab("Other ", imageResource.readIcon("Projects.Tab.Other"), projectOtherPanel);

        // Tree toolbar
        projectsToolBar = new IdBToolBar(this);

        // Top tool bar
        topToolBar = new TopToolBar(application, null);
        topToolBar.setDbToolbarVisible(false);

        tbProjectDirLbl = new ILabel("", ILabel.CENTER);
        tbProjectNameLbl = new ILabel("", ILabel.CENTER);
        Font f = tbProjectNameLbl.getFont();
        tbProjectNameLbl.setFont(new Font(f.getName(), Font.BOLD, 20));

    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Tool bar
        JPanel tbPanel = new JPanel(new BorderLayout());
        tbPanel.add(tbProjectNameLbl, BorderLayout.CENTER);
        tbPanel.add(tbProjectDirLbl, BorderLayout.SOUTH);
        topToolBar.getContentPane().setLayout(new BorderLayout());
        topToolBar.getContentPane().add(tbPanel);

        // Panels
        JPanel westPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 3, 2, 3),
                BorderFactory.createLineBorder(Color.GRAY, 1)
        ));

        projectsTree.setPreferredSize(new Dimension(300,200));
        JScrollPane pane = new JScrollPane(projectsTree);
        westPanel.add(pane, BorderLayout.CENTER);
        westPanel.add(projectsToolBar, BorderLayout.PAGE_END);
        westPanel.setMinimumSize(new Dimension(200, 200));

        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        centerPanel.add(topToolBar, BorderLayout.PAGE_START);
        //centerPanel.add(detailsPanel, BorderLayout.SOUTH);

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPanel, centerPanel);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length == 0) {
            return;
        }
        if (Application.isUpdating(ProjectsPanelLayout.this)) {
            return;
        }
        Application.beginWait(ProjectsPanelLayout.this);
        try {
            selectedProject = (Project) object[0];
            if (selectedProject == null) {
                selectedProject = CacheManager.cache().getProjects().get(0);
            }
            selectTreeTab(TAB_CODE, selectedProject);
            projectCodePanel.updateComponents(selectedProject);

            updateToolBar();

            updateEnabledComponents();
            updateVisibleComponents();
        } finally {
            Application.endWait(ProjectsPanelLayout.this);
        }
    }

    @Override
    public void onSelected(ProjectObject selectedObject) {
        detailsPanel.updateComponents(selectedObject);
    }
}
