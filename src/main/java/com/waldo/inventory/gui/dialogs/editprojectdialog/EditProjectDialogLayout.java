package com.waldo.inventory.gui.dialogs.editprojectdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IProjectObjectPreviewTableModel;
import com.waldo.inventory.gui.panels.projectspanel.panels.ProjectGridPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class EditProjectDialogLayout extends IDialog implements
        IEditedListener, ProjectGridPanel.GridComponentClicked<ProjectObject>, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameTf;
    private PanelUtils.IBrowseImagePanel iconPnl;
    PanelUtils.IBrowseFilePanel directoryPnl;
    private JButton findProjectsBtn;
    IComboCheckBox<ProjectIDE> ideTypeCcb;

    private IProjectObjectPreviewTableModel tableModel;
    private ITable<ProjectObjectPreview> projectTable;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     Project originalProject;
     Project selectedProject;

     List<ProjectCode> newProjectCodes = new ArrayList<>();
     List<ProjectPcb> newProjectPcbs = new ArrayList<>();
     List<ProjectOther> newProjectOthers = new ArrayList<>();

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditProjectDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean isValid = selectedProject.isValidDirectory();
        findProjectsBtn.setEnabled(isValid);
        projectTable.setEnabled(isValid);
    }

    Project copyProject(Project from) {
        Project to = from.createCopy();
        newProjectPcbs.clear();
        newProjectCodes.clear();
        newProjectOthers.clear();
        for (ProjectCode code : from.getProjectCodes()) {
            ProjectCode cpy = code.createCopy();
            newProjectCodes.add(cpy);
        }
        for (ProjectPcb pcb : from.getProjectPcbs()) {
            ProjectPcb cpy = pcb.createCopy();
            newProjectPcbs.add(cpy);
        }
        for (ProjectOther other : from.getProjectOthers()) {
            ProjectOther cpy = other.createCopy();
            newProjectOthers.add(cpy);
        }
        return to;
    }

    void tableInit(List<ProjectCode> projectCodes, List<ProjectPcb> projectPcbs, List<ProjectOther> projectOthers) {
        List<ProjectObject> projectObjectList = new ArrayList<>();

        projectObjectList.addAll(projectCodes);
        projectObjectList.addAll(projectPcbs);
        projectObjectList.addAll(projectOthers);

        List<ProjectObjectPreview> previewList = new ArrayList<>(projectObjectList.size());
        for (ProjectObject projectObject : projectObjectList) {
            previewList.add(new ProjectObjectPreview(projectObject));
        }
        tableModel.setItemList(previewList);
    }

    List<ProjectCode> getSelectedProjectCodes() {
        List<ProjectCode> selected = new ArrayList<>();
        for (ProjectObject object : tableModel.getSelectedObjects()) {
            if (object instanceof ProjectCode) {
                selected.add((ProjectCode) object);
            }
        }
        return selected;
    }

    List<ProjectPcb> getSelectedProjectPcbs() {
        List<ProjectPcb> selected = new ArrayList<>();
        for (ProjectObject object : tableModel.getSelectedObjects()) {
            if (object instanceof ProjectPcb) {
                selected.add((ProjectPcb) object);
            }
        }
        return selected;
    }

    List<ProjectOther> getSelectedProjectOthers() {
        List<ProjectOther> selected = new ArrayList<>();
        for (ProjectObject object : tableModel.getSelectedObjects()) {
            if (object instanceof ProjectOther) {
                selected.add((ProjectOther) object);
            }
        }
        return selected;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleName(getTitle());
        setTitleIcon(imageResource.readImage("Projects.Edit.Title"));
        setResizable(true);

        // This
        nameTf = new ITextField();
        nameTf.addEditedListener(this, "name");

        iconPnl = new PanelUtils.IBrowseImagePanel(SettingsManager.settings().getFileSettings().getImgProjectsPath(), this, "iconPath");
        iconPnl.setEditable(false);

        directoryPnl = new PanelUtils.IBrowseFilePanel("", "home/", this, "mainDirectory");
        directoryPnl.setEditable(false);

        findProjectsBtn = new JButton("Find projects");
        findProjectsBtn.addActionListener(this);
        ideTypeCcb = new IComboCheckBox<>(cache().getProjectIDES(), true);

        tableModel = new IProjectObjectPreviewTableModel();

        projectTable = new ITable<>(tableModel);
        projectTable.setExactColumnWidth(0, 32);
        projectTable.setExactColumnWidth(1, 36);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel variablesPanel = new JPanel();
        JPanel gridPanel = new JPanel(new BorderLayout());

        // Variables
        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(variablesPanel);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Icon: ", iconPnl);
        gbc.addLine("Directory: ", directoryPnl);

        variablesPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        // Grid
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        buttonPanel.add(findProjectsBtn);
        buttonPanel.add(ideTypeCcb);
        JScrollPane pane = new JScrollPane(projectTable);
        pane.setPreferredSize(new Dimension(400, 200));

        gridPanel.add(buttonPanel, BorderLayout.NORTH);
        gridPanel.add(pane, BorderLayout.CENTER);

        getContentPanel().add(variablesPanel, BorderLayout.NORTH);
        getContentPanel().add(gridPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            selectedProject = (Project) object[0];
            originalProject = copyProject(selectedProject);

            nameTf.setText(selectedProject.getName());
            iconPnl.setText(selectedProject.getIconPath());
            directoryPnl.setText(selectedProject.getMainDirectory());

            tableInit(newProjectCodes, newProjectPcbs, newProjectOthers);
        } else {
            originalProject = null;
        }

        updateEnabledComponents();
        nameTf.requestFocus();
    }
}