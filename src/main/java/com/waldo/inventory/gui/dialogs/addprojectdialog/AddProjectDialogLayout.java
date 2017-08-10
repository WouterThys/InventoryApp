package com.waldo.inventory.gui.dialogs.addprojectdialog;

import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class AddProjectDialogLayout extends IDialog implements
        IdBToolBar.IdbToolBarListener,
        ListSelectionListener,
        IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameField;
    JList<ProjectDirectory> directoryList;
    DefaultListModel<ProjectDirectory> directoryModel;
    IdBToolBar directoryTb;
    ILabel directoryInfoLbl;


    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectDirectory selectedDirectory;
    Project project;

    /*
    *                  CONSTRUCTORS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public AddProjectDialogLayout(Application application, String title) {
        super(application, title);
        setResizable(true);
    }

    public AddProjectDialogLayout(Dialog dialog, String title) {
        super(dialog, title);
        setResizable(true);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        if (selectedDirectory != null && !selectedDirectory.isUnknown()) {
            directoryTb.setDeleteActionEnabled(true);
            directoryTb.setEditActionEnabled(true);
        } else {
            directoryTb.setDeleteActionEnabled(false);
            directoryTb.setEditActionEnabled(false);
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Content
        nameField = new ITextField("Name");
        nameField.addEditedListener(this, "name");
        directoryModel = new DefaultListModel<>();
        directoryList = new JList<>(directoryModel);
        directoryList.addListSelectionListener(this);
        directoryTb = new IdBToolBar(this, IdBToolBar.VERTICAL);
        directoryTb.setFloatable(false);
        directoryInfoLbl = new ILabel();
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Extra
        JPanel dirPanel = new JPanel(new BorderLayout());
        dirPanel.add(new JScrollPane(directoryList), BorderLayout.CENTER);
        dirPanel.add(directoryTb, BorderLayout.EAST);
        dirPanel.add(directoryInfoLbl, BorderLayout.PAGE_END);

        // Name
        ILabel nameLabel = new ILabel("Name: ");
        nameLabel.setHorizontalAlignment(JLabel.LEFT);
        nameLabel.setVerticalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(nameField, gbc);

        // Directories
        ILabel dirLabel = new ILabel("Directories: ");
        dirLabel.setHorizontalAlignment(JLabel.LEFT);
        dirLabel.setVerticalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(dirLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        getContentPanel().add(dirPanel, gbc);

        // Border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        directoryModel.removeAllElements();

        if (object != null) {
            project = (Project) object;
            nameField.setText(project.getName());
            setTitleName(project.getName());
            if (!project.getIconPath().isEmpty()) {
                try {
                    Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgProjectsPath(), project.getIconPath());
                    URL url = path.toUri().toURL();
                    getTitleIconLabel().setIcon(imageResource.readImage(url, 48, 48));
                } catch (Exception e) {
                    Status().setError("Error setting title icon");
                }
            }
            for (ProjectDirectory dir : project.getProjectDirectories()) {
                directoryModel.addElement(dir);
            }

        } else {
            nameField.setText("");
        }

        updateEnabledComponents();
    }
}
