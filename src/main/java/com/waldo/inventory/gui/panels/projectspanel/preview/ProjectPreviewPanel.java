package com.waldo.inventory.gui.panels.projectspanel.preview;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.classes.dbclasses.ProjectObject;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.IRemarksPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextArea;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ProjectPreviewPanel<P extends ProjectObject> extends IPanel implements IdBToolBar.IdbToolBarListener {
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IImagePanel imagePanel;

    private ITextField nameTf;
    private ITextArea descriptionTa;
    private ITextField directoryTf;
    private IRemarksPanel remarksPnl;

    private IActions.DoItAction runIdeAction;
    private IActions.BrowseFileAction openProjectFolderAction;

    private IdBToolBar dbToolbar;
    private JToolBar actionToolBar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private P selectedProjectObject;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectPreviewPanel(Application application) {
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
    public void browseProjectObject(P projectObject) {
        if (projectObject != null) {
            File file = new File(projectObject.getDirectory());
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

    public void runIde(P projectObject) {
        if (projectObject != null) {
            if (projectObject.getProjectIDEId() > DbObject.UNKNOWN_ID) {
                ProjectIDE ide = projectObject.getProjectIDE();
                try {
                    ide.launch(new File(projectObject.getDirectory()));
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
                if (projectObject.isValid()) {
                    try {
                        ProjectIDE.tryLaunch(new File(projectObject.getDirectory()));
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

        remarksPnl.setEnabled(hasObject);
        runIdeAction.setEnabled(validObject);
        openProjectFolderAction.setEnabled(validObject);
    }

    private void updateHeader(P projectObject) {
        if (projectObject != null) {
            nameTf.setText(projectObject.toString());
            descriptionTa.setText(projectObject.getDescription());
        } else {
            nameTf.setText("");
            descriptionTa.setText("");
        }
        imagePanel.updateComponents(projectObject);
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
        if (projectObject != null) {
            remarksPnl.updateComponents(projectObject.getRemarksFile());
        }
    }

    private JPanel createToolBarPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(dbToolbar, BorderLayout.WEST);
        panel.add(actionToolBar, BorderLayout.EAST);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPnl = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(descriptionTa);
        scrollPane.setBorder(null);

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(headerPnl);
        // Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        headerPnl.add(imagePanel, gbc);

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
        gbc.addLine("Directory", imageResource.readIcon("Actions.BrowseFile"), directoryTf);

        // Specific code/pcb/other
        JPanel infoPnl = createInfoPanel();
        infoPnl.setBorder(BorderFactory.createEmptyBorder(8,1,1,1));

        dataPnl.add(commonPnl);
        dataPnl.add(infoPnl);

        return dataPnl;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Image
        imagePanel = new IImagePanel(null, Statics.ImageType.ProjectImage, selectedProjectObject, new Dimension(150,150));

        // Data
        nameTf = new ITextField(false);
        descriptionTa = new ITextArea(false);
        descriptionTa.setBorder(nameTf.getBorder());
        descriptionTa.setEnabled(false);
        descriptionTa.setLineWrap(true);
        descriptionTa.setWrapStyleWord(true);

        directoryTf = new ITextField(false);

        remarksPnl = new IRemarksPanel(application, newFile -> {
            selectedProjectObject.setRemarksFile(newFile);
            selectedProjectObject.save();
        });

        runIdeAction = new IActions.DoItAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runIde(selectedProjectObject);
            }
        };
        runIdeAction.putValue(Action.SMALL_ICON, imageResource.readIcon("Actions.M.Execute"));

        openProjectFolderAction = new IActions.BrowseFileAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseProjectObject(selectedProjectObject);
            }
        };
        openProjectFolderAction.putValue(Action.SMALL_ICON, imageResource.readIcon("Actions.M.BrowseFile"));

        dbToolbar = new IdBToolBar(this, false, false, true, true);
        actionToolBar = GuiUtils.createNewToolbar(openProjectFolderAction, runIdeAction);

        initializeInfoComponents();



    }

    @Override
    public void initializeLayouts() {
        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());

        JPanel toolbarsPanel = createToolBarPanel();
        JPanel headerPanel = createHeaderPanel();
        JPanel dataPanel = createDataPanel();
        //JPanel remarksPanel = createRemarksPanel();

        setLayout(new BorderLayout());

        panel1.add(headerPanel, BorderLayout.NORTH);
        panel1.add(dataPanel, BorderLayout.CENTER);

        panel2.add(toolbarsPanel, BorderLayout.PAGE_START);
        panel2.add(panel1, BorderLayout.CENTER);

        add(panel2, BorderLayout.NORTH);
        add(remarksPnl, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0 || args[0] == null) {
            setVisible(false);
            selectedProjectObject = null;
        } else {
            setVisible(true);
            if (args[0] instanceof Project) {
                selectedProjectObject = null;
            } else if (args[0] instanceof ProjectObject) {
                selectedProjectObject = (P) args[0];
            }

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
