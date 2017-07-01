package com.waldo.inventory.gui.panels.projectpanel.projectdetails;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;

public abstract class ProjectDetailsPanelLayout extends JPanel implements GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILabel iconLabel;

    ITextField nameTextField;
    DefaultListModel<ProjectDirectory> listModel;
    JList<ProjectDirectory> directoryList;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;
    Project selectedProject;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectDetailsPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JPanel createIconPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(iconLabel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private JPanel createProjectInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());

        ILabel nameLabel = new ILabel("Name: ", ILabel.LEFT);
        nameLabel.setStatusInfo("Item name");
        ILabel directoriesLabel = new ILabel("Directories: ", ILabel.LEFT);
        directoriesLabel.setStatusInfo("Directories");

        directoryList.setPreferredSize(new Dimension(300, 60));
        directoryList.setMaximumSize(directoryList.getPreferredSize());
        JScrollPane pane = new JScrollPane(directoryList);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        //  - Name
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(nameTextField, gbc);

        //  - Directories
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(directoriesLabel, gbc);

//        gbc.gridx = 0; gbc.weightx = 1;
//        gbc.gridy = 2; gbc.weighty = 1;
//        gbc.gridwidth = 2;
//        gbc.fill = GridBagConstraints.BOTH;
//        infoPanel.add(pane, gbc);

        return infoPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        iconLabel = new ILabel();
        iconLabel.setHorizontalAlignment(ILabel.CENTER);
        iconLabel.setVerticalAlignment(ILabel.CENTER);
        iconLabel.setPreferredSize(new Dimension(150,150));

        nameTextField = new ITextField();
        nameTextField.setEnabled(false);

        listModel = new DefaultListModel<>();
        directoryList = new JList<>(listModel);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        add(createIconPanel(), BorderLayout.WEST);
        add(createProjectInfoPanel(), BorderLayout.CENTER);
    }
}
