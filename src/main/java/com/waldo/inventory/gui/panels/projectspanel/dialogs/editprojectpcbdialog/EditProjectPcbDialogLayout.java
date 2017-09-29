package com.waldo.inventory.gui.panels.projectspanel.dialogs.editprojectpcbdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.ProjectIDE;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditProjectPcbDialogLayout extends IDialog implements IEditedListener, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField directoryTf;
    private JButton directoryBtn;
    private IComboBox<ProjectIDE> projectIdeCb;

    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectPcb projectPcb;
    ProjectPcb originalProjectPcb;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditProjectPcbDialogLayout(Application application, String title) {
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
        setTitleIcon(imageResource.readImage("Common.Pcb", 48));
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
    public void updateComponents(Object object) {
        projectPcb = (ProjectPcb) object;

        if (projectPcb != null) {
            originalProjectPcb = projectPcb.createCopy();

            application.beginWait();
            try {
                directoryTf.setText(projectPcb.getDirectory());
                projectIdeCb.setSelectedItem(projectPcb.getProjectIDE());
            } finally {
                application.endWait();
            }
        }
    }
}