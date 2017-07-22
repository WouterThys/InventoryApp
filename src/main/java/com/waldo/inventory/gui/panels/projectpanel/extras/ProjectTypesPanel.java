package com.waldo.inventory.gui.panels.projectpanel.extras;

import com.waldo.inventory.classes.ProjectType;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITileView;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ProjectTypesPanel extends JPanel implements GuiInterface, DbObjectChangedListener<ProjectType> {


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<ITileView> typeViews;

    /*
      *                  CONSTRUCTOR
      * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectTypesPanel() {

    }

    public ProjectTypesPanel(HashMap<ProjectType, File> projectTypeFileMap) {

    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectType get(int ndx) {
        return null;
    }

    public void add(ProjectType projectType, File file) {

    }

    public void remove (ProjectType projectType) {

    }

    public void setProjectTypeList(HashMap<ProjectType, File> projectTypeList) {

    }

    private String getFileName(String filePath) {
        if (filePath.contains("/")) {
            int ndx = filePath.lastIndexOf("/");
            return filePath.substring(ndx + 1, filePath.length() - 1);
        }
        return filePath;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object object) {

    }

    @Override
    public void onInserted(ProjectType object) {

    }

    @Override
    public void onUpdated(ProjectType newObject) {

    }

    @Override
    public void onDeleted(ProjectType object) {

    }
}
