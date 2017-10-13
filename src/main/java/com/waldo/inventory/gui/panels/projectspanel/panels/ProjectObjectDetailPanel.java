package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;

public class ProjectObjectDetailPanel extends JPanel implements GuiInterface {

    /*
         *                  COMPONENTS
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField objectNameTf;
    private ITextField objectMainDirectoryTf;
    private ITextField objectIdeTf;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectObjectDetailPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateDetails(ProjectObject object) {
        if (object != null) {
            objectNameTf.setText(object.getName());
            objectMainDirectoryTf.setText(object.getDirectory());
            if (object.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                objectIdeTf.setText(object.getProjectIDE().getName());
            } else {
                objectIdeTf.setText("");
            }
        }
    }

    private JPanel createDetailsPanel() {
        JPanel details = new JPanel();

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(details);
        gbc.addLine("Name: ", objectNameTf);
        gbc.addLine("Directory: ", objectMainDirectoryTf);
        gbc.addLine("IDE: ", objectIdeTf);

        return details;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        objectNameTf = new ITextField(false);
        objectMainDirectoryTf = new ITextField(false);
        objectIdeTf = new ITextField(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        add(createDetailsPanel(), BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object object) {
        if (object == null) {
            setVisible(false);
        } else {
            setVisible(true);
            ProjectObject selectedProjectObject = (ProjectObject) object;
            updateDetails(selectedProjectObject);
        }
    }
}