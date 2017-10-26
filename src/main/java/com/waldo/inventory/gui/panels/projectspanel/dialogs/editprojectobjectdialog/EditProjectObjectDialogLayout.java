package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectobjectdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.SearchManager.sm;

public abstract class EditProjectObjectDialogLayout<P extends ProjectObject> extends IDialog implements IEditedListener, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField directoryTf;
    private JButton directoryBtn;
    private IComboBox<ProjectIDE> projectIdeCb;

    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    P projectObject;
    P originalObject;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditProjectObjectDialogLayout(Application application, String title) {
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
        setTitleIcon(imageResource.readImage("Projects.Pcb.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Directory
        directoryTf = new ITextField();
        directoryTf.addEditedListener(this, "directory");
        directoryBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        directoryBtn.addActionListener(this);

        // IDE
        projectIdeCb = new IComboBox<>(
                sm().findProjectIDEsByType(Statics.ProjectTypes.Pcb),
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

        //  -  IDE
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new ILabel("IDE: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(projectIdeCb, gbc);

        //  -  Directory
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(new ILabel("Directory: ", ILabel.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(PanelUtils.createFileOpenPanel(directoryTf, directoryBtn), gbc);

        // Add
        getContentPanel().add(fieldsPanel, BorderLayout.CENTER);

        // Border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        projectObject = (P) object[0];

        if (projectObject != null) {
            originalObject = (P) projectObject.createCopy();

            application.beginWait();
            try {
                directoryTf.setText(projectObject.getDirectory());
                projectIdeCb.setSelectedItem(projectObject.getProjectIDE());
            } finally {
                application.endWait();
            }
        }
    }
}