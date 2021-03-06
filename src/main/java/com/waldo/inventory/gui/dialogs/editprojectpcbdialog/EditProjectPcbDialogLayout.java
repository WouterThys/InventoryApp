package com.waldo.inventory.gui.dialogs.editprojectpcbdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.SearchManager.sm;

abstract class EditProjectPcbDialogLayout extends IDialog implements IEditedListener, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField directoryTf;
    private JButton directoryBtn;
    private IComboBox<ProjectIDE> projectIdeCb;
    private ITextArea descriptionTa;

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
        setTitleIcon(imageResource.readIcon("Pcb.L"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Description
        descriptionTa = new ITextArea();
        descriptionTa.setLineWrap(true); // Go to next line when area is full
        descriptionTa.setWrapStyleWord(true); // Don't cut words in two
        descriptionTa.addEditedListener(this, "description");

        // Directory
        directoryTf = new ITextField();
        directoryTf.addEditedListener(this, "directory");
        directoryBtn = new JButton(imageResource.readIcon("Browse.Folder.SS"));
        directoryBtn.addActionListener(this);

        // IDE
        projectIdeCb = new IComboBox<>(
                sm().findProjectIDEsByType(Statics.ProjectTypes.Pcb),
                new DbObjectNameComparator<>(),
                true);
        projectIdeCb.addEditedListener(this, "projectIDEId");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridBagLayout());

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("Description: "), BorderLayout.PAGE_START);
        descPanel.add(new JScrollPane(descriptionTa), BorderLayout.CENTER);

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
        fieldsPanel.add(GuiUtils.createFileOpenPanel(directoryTf, directoryBtn), gbc);

        // Add
        getContentPanel().add(fieldsPanel, BorderLayout.NORTH);
        getContentPanel().add(descPanel, BorderLayout.CENTER);

        // Border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        projectPcb = (ProjectPcb) object[0];

        if (projectPcb != null) {
            originalProjectPcb = projectPcb.createCopy();

            beginWait();
            try {
                directoryTf.setText(projectPcb.getDirectory());
                projectIdeCb.setSelectedItem(projectPcb.getProjectIDE());
                descriptionTa.setText(projectPcb.getDescription());
            } finally {
                endWait();
            }
        }
    }
}