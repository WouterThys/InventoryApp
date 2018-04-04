package com.waldo.inventory.gui.dialogs.editprojectcodedialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.CodeLanguages;
import com.waldo.inventory.classes.dbclasses.ProjectCode;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ITextArea;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.SearchManager.sm;

abstract class EditProjectCodeDialogLayout extends IObjectDialog<ProjectCode> {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<CodeLanguages> languageCb;
    GuiUtils.IBrowseFilePanel directoryPnl;
    private IComboBox<ProjectIDE> projectIdeCb;
    private ITextArea descriptionTa;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditProjectCodeDialogLayout(Window window, ProjectCode projectCode) {
        super(window, "Project code", projectCode, ProjectCode.class);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectCode getProjectCode() {
        return getObject();
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readIcon("Projects.Code.Title"));

        // Description
        descriptionTa = new ITextArea();
        descriptionTa.setLineWrap(true); // Go to next line when area is full
        descriptionTa.setWrapStyleWord(true); // Don't cut words in two
        descriptionTa.addEditedListener(this, "description");

        // Language
        languageCb = new IComboBox<>(CodeLanguages.values());
        languageCb.addEditedListener(this, "language", String.class);

        // Directory
        directoryPnl = new GuiUtils.IBrowseFilePanel("", "home/",this, "directory");

        // IDE
        projectIdeCb = new IComboBox<>(
                sm().findProjectIDEsByType(Statics.ProjectTypes.Code),
                new DbObjectNameComparator<>(),
                true);
        projectIdeCb.addEditedListener(this, "projectIDEId");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("Description: "), BorderLayout.PAGE_START);
        descPanel.add(new JScrollPane(descriptionTa), BorderLayout.CENTER);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());

        // Fields
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(fieldsPanel);
        gbc.addLine("Language: ", languageCb);
        gbc.addLine("IDE: ", projectIdeCb);
        gbc.addLine("Directory: ", directoryPnl);

        // Add
        getContentPanel().add(fieldsPanel, BorderLayout.NORTH);
        getContentPanel().add(descPanel, BorderLayout.CENTER);

        // Border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (getObject() != null) {
            beginWait();
            try {
                languageCb.setSelectedItem(getProjectCode().getLanguage());
                directoryPnl.setText(getProjectCode().getDirectory());
                projectIdeCb.setSelectedItem(getProjectCode().getProjectIDE());
                descriptionTa.setText(getProjectCode().getDescription());
            } finally {
                endWait();
            }
        }
    }
}