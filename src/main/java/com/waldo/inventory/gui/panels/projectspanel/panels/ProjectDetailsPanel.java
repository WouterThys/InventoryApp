package com.waldo.inventory.gui.panels.projectspanel.panels;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.*;
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
import static com.waldo.inventory.gui.Application.imageResource;

public class ProjectDetailsPanel extends JPanel implements GuiInterface {    

    private final static String PNL_CODE = "CODE";
    private final static String PNL_PCB = "PCB";
    private final static String PNL_OTHER = "OTHER";

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel iconLbl;

    private ITextField nameTf;
    private ITextField directoryTf;

    // Code
    private ITextField codeIdeTf;
    private ITextField languageTf;
    // Pcb
    private ITextField pcbIdeTf;
    private ITextField itemsFromTf;
    private ITextField lastParsedTf;
    private ITextField numberOfItemsTf;
    // Other
    //..

    private JPanel cardPanel;

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
    private void updateIcon(ProjectObject project) {
        try {
            Project p = project.getProject();
            Path path = Paths.get(settings().getFileSettings().getImgProjectsPath(), p.getIconPath());
            iconLbl.setIcon(path.toString(), 120, 120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDetails(ProjectObject project) {
        if (project != null) {
            nameTf.setText(project.getName());
            directoryTf.setText(project.getDirectory());

            CardLayout layout = (CardLayout) cardPanel.getLayout();
            if (project instanceof ProjectCode) {
                if (project.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                    codeIdeTf.setText(project.getProjectIDE().getName());
                } else {
                    codeIdeTf.setText("");
                }
                languageTf.setText(((ProjectCode)project).getLanguage());
                layout.show(cardPanel, PNL_CODE);
            } else if (project instanceof ProjectPcb) {
                ProjectPcb pcb = (ProjectPcb) project;
                if (pcb.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                     pcbIdeTf.setText(pcb.getProjectIDE().getName());
                } else {
                     pcbIdeTf.setText("");
                }
                itemsFromTf.setText(pcb.hasParsed() ? "file" : "database");
                lastParsedTf.setText(DateUtils.formatDateTime(pcb.getLastParsedDate()));
                numberOfItemsTf.setText(String.valueOf(pcb.numberOfComponents()));
                layout.show(cardPanel, PNL_PCB);
            } else if (project instanceof ProjectOther) {
                layout.show(cardPanel, PNL_OTHER);
            }
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
        JPanel detailPanel = new JPanel(new BorderLayout());

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(mainPanel);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Directory: ", directoryTf);

        detailPanel.add(createCardPanel(), BorderLayout.NORTH);

        panel.add(mainPanel, BorderLayout.NORTH);
        panel.add(detailPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCardPanel() {
        PanelUtils.GridBagHelper gbc;
        JPanel codePanel = new JPanel();
        JPanel pcbPanel = new JPanel();
        JPanel otherPanel = new JPanel();

        codePanel.setBorder(PanelUtils.createTitleBorder(imageResource.readImage("Projects.Details.Code")));
        pcbPanel.setBorder(PanelUtils.createTitleBorder(imageResource.readImage("Projects.Details.Pcb")));
        otherPanel.setBorder(PanelUtils.createTitleBorder(imageResource.readImage("Projects.Details.Other")));

        JPanel westPanel;
        JPanel eastPanel;

        // Code
        westPanel = new JPanel();
        eastPanel = new JPanel();
        gbc = new PanelUtils.GridBagHelper(westPanel);
        gbc.addLine("IDE: ", codeIdeTf);
        gbc.addLine(" ", null);
        gbc = new PanelUtils.GridBagHelper(eastPanel);
        gbc.addLine("Language: ", languageTf);
        gbc.addLine(" ", null);
        codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.X_AXIS));
        codePanel.add(westPanel);
        codePanel.add(eastPanel);

        // Pcb
        westPanel = new JPanel();
        eastPanel = new JPanel();
        gbc = new PanelUtils.GridBagHelper(westPanel);
        gbc.addLine("IDE: ", pcbIdeTf);
        gbc.addLine("Components from ", itemsFromTf);
        gbc = new PanelUtils.GridBagHelper(eastPanel);
        gbc.addLine("# components: ", numberOfItemsTf);
        gbc.addLine("Last parsed: ", lastParsedTf);
        pcbPanel.setLayout(new BoxLayout(pcbPanel, BoxLayout.X_AXIS));
        pcbPanel.add(westPanel);
        pcbPanel.add(eastPanel);

        // Other
        //..

        cardPanel.add(PNL_CODE, codePanel);
        cardPanel.add(PNL_PCB, pcbPanel);
        cardPanel.add(PNL_OTHER, otherPanel);

        return cardPanel;
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
        directoryTf = new ITextField(false);

        codeIdeTf = new ITextField(false);
        pcbIdeTf = new ITextField(false);

        languageTf = new ITextField(false);
        itemsFromTf = new ITextField(false);
        lastParsedTf = new ITextField(false);
        numberOfItemsTf = new ITextField(false);

        cardPanel = new JPanel(new CardLayout());

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
    public void updateComponents(Object... object) {
        if (object.length == 0 || object[0] == null) {
            setVisible(false);
        } else {
            setVisible(true);
            ProjectObject selectedProject = (ProjectObject) object[0];
            updateIcon(selectedProject);
            updateDetails(selectedProject);
        }
    }
}