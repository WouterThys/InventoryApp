package com.waldo.inventory.gui.panels.projectspanel.projectdetailpanel;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.dialogs.editremarksdialog.EditRemarksDialog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ITextField;
import com.waldo.utils.icomponents.ITextPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public class ProjectDetailsPanel extends JPanel implements GuiUtils.GuiInterface {

    private final static String PNL_CODE = "CODE";
    private final static String PNL_PCB = "PCB";
    private final static String PNL_OTHER = "OTHER";

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IImagePanel imagePanel;

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

    private AbstractAction editRemarksAa;
    private ITextPane remarksTp;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private ProjectObject selectedProject;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ProjectDetailsPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateIcon(ProjectObject projectObject) {
        imagePanel.updateComponents(projectObject);
    }

    private void updateDetails(ProjectObject project) {
        if (project != null) {
            nameTf.setText(project.getName());
            directoryTf.setText(project.getDirectory());
            remarksTp.setFile(project.getRemarksFile());

            CardLayout layout = (CardLayout) cardPanel.getLayout();
            if (project instanceof ProjectCode) {
                if (project.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                    codeIdeTf.setText(project.getProjectIDE().getName());
                } else {
                    codeIdeTf.setText("");
                }
                languageTf.setText(((ProjectCode)project).getLanguage().toString());
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
        panel.add(imagePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel mainPanel = new JPanel();
        JPanel detailPanel = new JPanel(new BorderLayout());

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(mainPanel);
        gbc.addLine("Name: ", nameTf);
        gbc.addLine("Directory: ", directoryTf);

        detailPanel.add(createCardPanel(), BorderLayout.NORTH);

        panel.add(mainPanel, BorderLayout.NORTH);
        panel.add(detailPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCardPanel() {
        GuiUtils.GridBagHelper gbc;
        JPanel codePanel = new JPanel();
        JPanel pcbPanel = new JPanel();
        JPanel otherPanel = new JPanel();

        codePanel.setBorder(GuiUtils.createTitleBorder(imageResource.readIcon("Projects.Details.Code")));
        pcbPanel.setBorder(GuiUtils.createTitleBorder(imageResource.readIcon("Projects.Details.Pcb")));
        otherPanel.setBorder(GuiUtils.createTitleBorder(imageResource.readIcon("Projects.Details.Other")));

        JPanel westPanel;
        JPanel eastPanel;

        // Code
        westPanel = new JPanel();
        eastPanel = new JPanel();
        gbc = new GuiUtils.GridBagHelper(westPanel);
        gbc.addLine("IDE: ", codeIdeTf);
        gbc.addLine(" ", null);
        gbc = new GuiUtils.GridBagHelper(eastPanel);
        gbc.addLine("Language: ", languageTf);
        gbc.addLine(" ", null);
        codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.X_AXIS));
        codePanel.add(westPanel);
        codePanel.add(eastPanel);

        // Pcb
        westPanel = new JPanel();
        eastPanel = new JPanel();
        gbc = new GuiUtils.GridBagHelper(westPanel);
        gbc.addLine("IDE: ", pcbIdeTf);
        gbc.addLine("Components from ", itemsFromTf);
        gbc = new GuiUtils.GridBagHelper(eastPanel);
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

    private JPanel createRemarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JToolBar toolBar = GuiUtils.createNewToolbar();
        JButton b = toolBar.add(editRemarksAa);
        b.setText("Edit remarks ");
        b.setVerticalTextPosition(SwingConstants.CENTER);
        b.setHorizontalTextPosition(SwingConstants.LEFT);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolBar, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.PAGE_START);
        panel.add(new JScrollPane(remarksTp), BorderLayout.CENTER);

        return panel;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        imagePanel = new IImagePanel(null, Statics.ImageType.ProjectImage, null, new Dimension(150,150));

        nameTf = new ITextField(false);
        directoryTf = new ITextField(false);

        codeIdeTf = new ITextField(false);
        pcbIdeTf = new ITextField(false);

        languageTf = new ITextField(false);
        itemsFromTf = new ITextField(false);
        lastParsedTf = new ITextField(false);
        numberOfItemsTf = new ITextField(false);

        cardPanel = new JPanel(new CardLayout());

        remarksTp = new ITextPane();
        remarksTp.setPreferredSize(new Dimension(300, 50));
        remarksTp.setEditable(false);
        editRemarksAa = new AbstractAction("Edit remarks", imageResource.readIcon("Actions.EditRemark")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditRemarksDialog dialog = new EditRemarksDialog(application, "Edit project remarks", selectedProject.getRemarksFile());
                if (dialog.showDialog() == IDialog.OK) {
                    selectedProject.setRemarksFile(dialog.getFile());
                    selectedProject.save();
                }
            }
        };
        editRemarksAa.putValue(AbstractAction.LONG_DESCRIPTION, "Edit remarks");
        editRemarksAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Edit remarks");
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        add(createIconPanel(), BorderLayout.WEST);
        add(createDetailsPanel(), BorderLayout.CENTER);
        add(createRemarksPanel(), BorderLayout.EAST);
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length == 0 || object[0] == null) {
            setVisible(false);
        } else {
            setVisible(true);
            selectedProject = (ProjectObject) object[0];
            updateIcon(selectedProject);
            updateDetails(selectedProject);
        }
    }
}