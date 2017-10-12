package com.waldo.inventory.gui.dialogs.editprojectdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.gui.Application;

import java.awt.*;
import java.awt.event.ActionEvent;

public class EditProjectDialog extends EditProjectDialogLayout {


    public EditProjectDialog(Application application, String title, Project project) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(project);
    }


    public Project getProject() {
        return project;
    }

    //
    // Edited values
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        updateEnabledComponents();
    }

    @Override
    public DbObject getGuiObject() {
        return project;
    }

    //
    // Found projects in directory clicked
    //
    @Override
    public void onGridComponentClick(ProjectObject projectObject) {

    }

    //
    // Find projects button
    //
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}