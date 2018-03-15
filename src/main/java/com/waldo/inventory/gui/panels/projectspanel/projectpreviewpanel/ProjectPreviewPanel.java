package com.waldo.inventory.gui.panels.projectspanel.projectpreviewpanel;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.editremarksdialog.EditRemarksDialog;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ProjectPreviewPanel<P extends ProjectObject> extends IPanel implements IdBToolBar.IdbToolBarListener {
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel iconLbl;

    private ITextField nameTf;
    private ITextArea descriptionTa;
    private ITextField directoryTf;
    private ITextPane remarksTp;

    private AbstractAction editRemarksAa;
    private IActions.DoItAction runIdeAction;
    private IActions.BrowseFileAction openProjectFolderAction;

    private IdBToolBar dbToolbar;
    protected JToolBar actionToolBar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;
    protected P selectedProjectObject;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected ProjectPreviewPanel(Application application) {
        super();
        this.application = application;
        initializeComponents();
        initializeLayouts();
    }

    abstract void initializeInfoComponents();
    abstract JPanel createInfoPanel();
    abstract void updateInfoPanel(P projectObject);

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void browseProjectObject() {
        if (selectedProjectObject != null) {
            File file = new File(selectedProjectObject.getDirectory());
            if (file.exists()) {
                if (file.isFile()) {
                    file = file.getParentFile();
                }
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            application,
                            "Could not open folder..",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(
                        application,
                        "Could not folder because it does not exist..",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void runIde() {
        if (selectedProjectObject != null) {
            if (selectedProjectObject.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                ProjectIDE ide = selectedProjectObject.getProjectIDE();
                try {
                    ide.launch(new File(selectedProjectObject.getDirectory()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to open IDE",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                if (selectedProjectObject.isValid()) {
                    try {
                        ProjectIDE.tryLaunch(new File(selectedProjectObject.getDirectory()));
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        JOptionPane.showMessageDialog(
                                this,
                                "Failed to open IDE",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        }
    }

    private void updateToolbar() {
        boolean hasObject = selectedProjectObject != null;
        boolean validObject = hasObject && selectedProjectObject.isValid();

        editRemarksAa.setEnabled(hasObject);
        runIdeAction.setEnabled(validObject);
        openProjectFolderAction.setEnabled(validObject);
    }

    private void updateHeader(P projectObject) {
        ImageIcon icon;
        try {
            Project project = projectObject.getProject();
            Path path = Paths.get(SettingsManager.settings().getFileSettings().getImgProjectsPath(), project.getIconPath());
            URL url = path.toUri().toURL();
            icon = imageResource.readImage(url, 150, 150);
        } catch (Exception e) {
            icon = imageResource.readImage("Projects.Icon");
        }
        iconLbl.setIcon(icon);
        nameTf.setText(projectObject.toString());
        descriptionTa.setText(projectObject.getDescription());
    }

    private void updateData(P projectObject) {
        if (projectObject != null) {
            String objectDir = projectObject.getDirectory();
            if (projectObject.getProjectId() > DbObject.UNKNOWN_ID) {
                String mainDir = projectObject.getProject().getMainDirectory();
                String showDir = objectDir.replace(mainDir, "..");
                directoryTf.setText(showDir);
            } else {
                directoryTf.setText(objectDir);
            }
        } else {
            directoryTf.setText("");
        }
        updateInfoPanel(projectObject);
    }

    private void updateRemarks(P projectObject) {
        remarksTp.setFile(projectObject.getRemarksFile());
    }

    private JPanel createToolBarPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(dbToolbar, BorderLayout.WEST);
        panel.add(actionToolBar, BorderLayout.EAST);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPnl = new JPanel(new BorderLayout());

        iconLbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(2,2,2,2)
        ));

        JScrollPane scrollPane = new JScrollPane(descriptionTa);
        scrollPane.setBorder(null);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(headerPnl);
        // Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(iconLbl, gbc);

        // Name
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPnl.add(nameTf, gbc);

        // Name
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(scrollPane, gbc);

        return headerPnl;
    }

    private JPanel createDataPanel() {
        JPanel dataPnl = new JPanel();
        dataPnl.setLayout(new BoxLayout(dataPnl, BoxLayout.Y_AXIS));

        GuiUtils.GridBagHelper gbc;

        // Common
        JPanel commonPnl = new JPanel();
        commonPnl.setBorder(BorderFactory.createEmptyBorder(1,1,8,1));
        gbc = new GuiUtils.GridBagHelper(commonPnl, 0);
        gbc.addLine("Directory", imageResource.readImage(""), directoryTf);

        // Specific code/pcb/other
        JPanel infoPnl = createInfoPanel();
        infoPnl.setBorder(BorderFactory.createEmptyBorder(8,1,1,1));

        dataPnl.add(commonPnl);
        dataPnl.add(infoPnl);

        return dataPnl;
    }

    private JPanel createRemarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JToolBar toolBar = GuiUtils.createNewToolbar();
        JButton b = toolBar.add(editRemarksAa);
        b.setText("Edit remarks ");
        b.setVerticalTextPosition(SwingConstants.CENTER);
        b.setHorizontalTextPosition(SwingConstants.LEFT);

        JScrollPane scrollPane = new JScrollPane(remarksTp);
        panel.add(toolBar, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Label
        iconLbl = new ILabel();
        iconLbl.setBackground(Color.WHITE);
        iconLbl.setOpaque(true);
        iconLbl.setHorizontalAlignment(ILabel.CENTER);
        iconLbl.setVerticalAlignment(ILabel.CENTER);
        iconLbl.setPreferredSize(new Dimension(150,150));
        iconLbl.setMaximumSize(new Dimension(150,150));
        iconLbl.setMinimumSize(new Dimension(150,150));

        // Data
        nameTf = new ITextField(false);
        descriptionTa = new ITextArea(false);
        descriptionTa.setBorder(nameTf.getBorder());
        descriptionTa.setEnabled(false);
        descriptionTa.setLineWrap(true);
        descriptionTa.setWrapStyleWord(true);

        directoryTf = new ITextField(false);

        remarksTp = new ITextPane();
        remarksTp.setEditable(false);
        remarksTp.setEnabled(false);

        dbToolbar = new IdBToolBar(this, false, false, true, true);
        actionToolBar = GuiUtils.createNewToolbar(openProjectFolderAction, runIdeAction);

        initializeInfoComponents();

        // Actions
        editRemarksAa = new AbstractAction("Edit remarks", imageResource.readImage("Actions.EditRemark")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditRemarksDialog dialog = new EditRemarksDialog(application, "Edit project remarks", selectedProjectObject.getRemarksFile());
                if (dialog.showDialog() == IDialog.OK) {
                    selectedProjectObject.setRemarksFile(dialog.getFile());
                    selectedProjectObject.save();
                }
            }
        };
        editRemarksAa.putValue(AbstractAction.LONG_DESCRIPTION, "Edit remarks");
        editRemarksAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Edit remarks");

        runIdeAction = new IActions.DoItAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runIde();
            }
        };
        runIdeAction.putValue(Action.SMALL_ICON, imageResource.readImage("Actions.Execute"));

        openProjectFolderAction = new IActions.BrowseFileAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseProjectObject();
            }
        };
        openProjectFolderAction.putValue(Action.SMALL_ICON, imageResource.readImage("Actions.M.BrowseFile"));

    }

    @Override
    public void initializeLayouts() {
        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());

        JPanel toolbarsPanel = createToolBarPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel dataPanel = createDataPanel();
        JPanel remarksPanel = createRemarksPanel();

        setLayout(new BorderLayout());

        panel1.add(headerPanel, BorderLayout.NORTH);
        panel1.add(dataPanel, BorderLayout.CENTER);

        panel2.add(toolbarsPanel, BorderLayout.PAGE_START);
        panel2.add(panel1, BorderLayout.CENTER);

        add(panel2, BorderLayout.NORTH);
        add(remarksPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            setVisible(false);
            selectedProjectObject = null;
        } else {
            setVisible(true);
            selectedProjectObject = (P) args[0];

            updateToolbar();
            updateHeader(selectedProjectObject);
            updateData(selectedProjectObject);
            updateRemarks(selectedProjectObject);
        }
    }

    //
    // Toolbar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {

    }
}
