package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.inventory.gui.dialogs.editprojectdialog.ProjectObjectPreview;
import com.waldo.utils.FileUtils;
import com.waldo.utils.icomponents.IAbstractTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class IProjectObjectPreviewTableModel extends IAbstractTableModel<ProjectObjectPreview> {

    private static final String[] COLUMN_NAMES = {"", "", "Name", "Path"};
    private static final Class[] COLUMN_CLASSES = {Boolean.class, ImageIcon.class, String.class, String.class};

    public IProjectObjectPreviewTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }


    public List<ProjectObject> getSelectedObjects() {
        List<ProjectObject> selectedList = new ArrayList<>();
        for (ProjectObjectPreview preview : getItemList()) {
            if (preview.isAddToProject()) {
                selectedList.add(preview.getProjectObject());
            }
        }
        return selectedList;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProjectObjectPreview projectObject = getItemAt(rowIndex);
        if (projectObject != null) {
            switch (columnIndex) {
                case -1:
                    return projectObject;
                case 0: // Add to project
                    return projectObject.isAddToProject();
                case 1: // Project icon
                    String ideIconPath = projectObject.getProjectObject().getProjectIDE().getIconPath();
                    return ImageResource.scaleImage(imageResource.readIdeIcon(ideIconPath), new Dimension(28, 28));
                case 2: // Name
                    return FileUtils.formatFileNameString(projectObject.getProjectObject().getName());
                case 3: // Full path
                    String dir = projectObject.getProjectObject().getDirectory();
                    Project project = projectObject.getProjectObject().getProject();
                    if (project != null) {
                        dir = dir.replace(project.getMainDirectory(), "...");
                    }
                    return dir;

            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            boolean check = (Boolean) aValue;
            ProjectObjectPreview projectObject = getItemAt(rowIndex);
            if (projectObject != null) {
                projectObject.setAddToProject(check);
            }
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
}