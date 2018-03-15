package com.waldo.inventory.gui.dialogs.editprojectcodedialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.CodeLanguages;
import com.waldo.inventory.classes.dbclasses.ProjectCode;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.gui.Application;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IEditedListener;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.SearchManager.sm;

abstract class EditProjectCodeDialogLayout extends IDialog implements IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<CodeLanguages> languageCb;
    GuiUtils.IBrowseFilePanel directoryPnl;
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
        setTitleIcon(imageResource.readImage("Projects.Code.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

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

        JPanel fieldsPanel = new JPanel(new GridBagLayout());

        // Fields
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(fieldsPanel);
        gbc.addLine("Language: ", languageCb);
        gbc.addLine("IDE: ", projectIdeCb);
        gbc.addLine("Directory: ", directoryPnl);

        // Add
        getContentPanel().add(fieldsPanel, BorderLayout.CENTER);

        // Border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        projectCode = (ProjectCode) object[0];

        if (projectCode != null) {
            originalProjectCode = projectCode.createCopy();

            beginWait();
            try {
                languageCb.setSelectedItem(projectCode.getLanguage());
                directoryPnl.setText(projectCode.getDirectory());
                projectIdeCb.setSelectedItem(projectCode.getProjectIDE());
                //remarksTa.setText(projectCode.getRemarksFile());
            } finally {
                endWait();
            }
        }
    }
}