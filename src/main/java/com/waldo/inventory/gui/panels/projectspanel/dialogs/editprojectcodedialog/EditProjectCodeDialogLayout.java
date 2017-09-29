package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectcodedialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.ProjectCode;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditProjectCodeDialogLayout extends IDialog implements IEditedListener, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<String> languageCb;
    ITextField directoryTf;
    private JButton directoryBtn;
    private IComboBox<ProjectIDE> projectIdeCb;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectCode projectCode;
    ProjectCode originalProjectCode;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditProjectCodeDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Common.Code", 48));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Language
        DefaultComboBoxModel<String> languageModel = new DefaultComboBoxModel<>(Statics.CodeLanguage.All);
        languageCb = new IComboBox<>(languageModel);
        languageCb.addEditedListener(this, "language", String.class);

        // Directory
        directoryTf = new ITextField();
        directoryTf.addEditedListener(this, "directory");
        directoryBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        directoryBtn.addActionListener(this);

        // IDE
        projectIdeCb = new IComboBox<>(
                sm().findProjectIDEsByType(Statics.ProjectTypes.Code),
                new DbObject.DbObjectNameComparator<>(),
                true);
        projectIdeCb.addEditedListener(this, "projectIDEId");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridBagLayout());

        // Fields
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        //  -  Language
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new ILabel("Language: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(languageCb, gbc);

        //  -  IDE
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new ILabel("IDE: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(projectIdeCb, gbc);

        //  -  Directory
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new ILabel("Directory: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(PanelUtils.createFileOpenPanel(directoryTf, directoryBtn), gbc);

        // Add
        getContentPanel().add(fieldsPanel, BorderLayout.CENTER);

        // Border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        projectCode = (ProjectCode) object;

        if (projectCode != null) {
            originalProjectCode = projectCode.createCopy();

            application.beginWait();
            try {
                languageCb.setSelectedItem(projectCode.getLanguage());
                directoryTf.setText(projectCode.getDirectory());
                projectIdeCb.setSelectedItem(projectCode.getProjectIDE());
                //remarksTa.setText(projectCode.getRemarks());
            } finally {
                application.endWait();
            }
        }
    }
}