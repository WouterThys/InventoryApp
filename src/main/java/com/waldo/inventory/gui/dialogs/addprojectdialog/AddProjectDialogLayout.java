package com.waldo.inventory.gui.dialogs.addprojectdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectDirectory;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public abstract class AddProjectDialogLayout extends IDialog implements
        GuiInterface,
        IdBToolBar.IdbToolBarListener,
        ListSelectionListener {

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
        showTitlePanel(false);
        setResizable(true);
    }

    public AddProjectDialogLayout(Dialog dialog, String title) {
        super(dialog, title);
        showTitlePanel(false);
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
        nameField = new ITextField("Name");
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
            for (ProjectDirectory dir : project.getProjectDirectories()) {
                directoryModel.addElement(dir);
            }

        } else {
            nameField.setText("");
        }

        updateEnabledComponents();
    }
}
