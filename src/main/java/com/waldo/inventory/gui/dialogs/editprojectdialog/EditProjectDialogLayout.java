package com.waldo.inventory.gui.dialogs.editprojectdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.panels.projectspanel.panels.ProjectGridPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditProjectDialogLayout extends IDialog implements
        IEditedListener, ProjectGridPanel.GridComponentClicked<ProjectObject>, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField nameTf;
    private PanelUtils.IBrowseImagePanel iconPnl;
    private PanelUtils.IBrowseFilePanel directoryPnl;
    private JButton findProjectsBtn;
    private ProjectGridPanel<ProjectObject> projectGridPanel;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Project project;

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
        boolean isValid = project.isValidDirectory();
        findProjectsBtn.setEnabled(isValid);
        projectGridPanel.setEnabled(isValid);
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

        projectGridPanel = new ProjectGridPanel<>(this, 3,2);

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

        // Grid
        JPanel buttonPanel = new JPanel(new BorderLayout());

        buttonPanel.add(findProjectsBtn, BorderLayout.EAST);
        JScrollPane pane = new JScrollPane(projectGridPanel);
        pane.setPreferredSize(new Dimension(400, 200));

        gridPanel.add(buttonPanel, BorderLayout.NORTH);
        gridPanel.add(pane, BorderLayout.CENTER);

        getContentPanel().add(variablesPanel, BorderLayout.NORTH);
        getContentPanel().add(gridPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            project = (Project) object;

            nameTf.setText(project.getName());
            iconPnl.setText(project.getIconPath());
            directoryPnl.setText(project.getMainDirectory());

            List<ProjectObject> projectObjectList = new ArrayList<>();

            projectObjectList.addAll(project.getProjectCodes());
            projectObjectList.addAll(project.getProjectPcbs());
            projectObjectList.addAll(project.getProjectOthers());

            projectGridPanel.drawTiles(projectObjectList);
            projectGridPanel.updateTiles();
        }

        updateEnabledComponents();
        nameTf.requestFocus();
    }
}