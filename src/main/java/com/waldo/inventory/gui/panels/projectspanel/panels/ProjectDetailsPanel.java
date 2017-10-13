package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextEditor;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ProjectDetailsPanel extends JPanel implements GuiInterface {    
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel iconLbl;

    private ITextField nameTf;
    private ITextField mainDirectoryTf;
    private ITextField numCodesTf;
    private ITextField numPcbsTf;
    private ITextField numOthersTf;

    private ProjectObjectDetailPanel projectObjectDetailPanel;

    private ITextEditor remarksTe;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectDetailsPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void updateObjectDetails(ProjectObject projectObject) {
        projectObjectDetailPanel.updateComponents(projectObject);
    }

    private void updateIcon(Project project) {
        try {
            Path path = Paths.get(settings().getFileSettings().getImgProjectsPath(), project.getIconPath());
            iconLbl.setIcon(path.toString(), 120, 120);
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }
    }

    private void updateDetails(Project project) {
        if (project != null) {
            nameTf.setText(project.getName());
            mainDirectoryTf.setText(project.getMainDirectory());
            numCodesTf.setText(String.valueOf(project.getProjectCodes().size()));
            numPcbsTf.setText(String.valueOf(project.getProjectPcbs().size()));
            numOthersTf.setText(String.valueOf(project.getProjectOthers().size()));
        }
    }


    private JPanel createIconPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(iconLbl, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel mainPanel = new JPanel();
        JPanel numbersPanel = new JPanel();

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(mainPanel);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Main directory: ", mainDirectoryTf);

        gbc = new PanelUtils.GridBagHelper(numbersPanel);
        gbc.addLine("# Codes: ", numCodesTf);
        gbc.addLine("# Pcbs: ", numPcbsTf);
        gbc.addLine("# Others: ", numOthersTf);

        panel.add(mainPanel, BorderLayout.NORTH);
        panel.add(numbersPanel, BorderLayout.WEST);
        panel.add(projectObjectDetailPanel, BorderLayout.CENTER);

        return panel;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        iconLbl = new ILabel();
        iconLbl.setHorizontalAlignment(ILabel.CENTER);
        iconLbl.setVerticalAlignment(ILabel.CENTER);
        iconLbl.setPreferredSize(new Dimension(150,150));

        nameTf = new ITextField(false);
        mainDirectoryTf = new ITextField(false);
        numCodesTf = new ITextField(false);
        numPcbsTf = new ITextField(false);
        numOthersTf = new ITextField(false);

        projectObjectDetailPanel = new ProjectObjectDetailPanel(application);
        remarksTe = new ITextEditor();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        add(createIconPanel(), BorderLayout.WEST);
        add(createDetailsPanel(), BorderLayout.CENTER);
        add(remarksTe, BorderLayout.EAST);
    }

    @Override
    public void updateComponents(Object object) {
        if (object == null) {
            setVisible(false);
        } else {
            setVisible(true);
            Project selectedProject = (Project) object;
            updateIcon(selectedProject);
            updateDetails(selectedProject);
            projectObjectDetailPanel.updateComponents(null);
        }
    }
}