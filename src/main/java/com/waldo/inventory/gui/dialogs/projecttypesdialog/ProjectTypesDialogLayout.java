package com.waldo.inventory.gui.dialogs.projecttypesdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Project;
import com.waldo.inventory.classes.ProjectType;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.filechooserdialog.CsvFileChooser;
import com.waldo.inventory.gui.dialogs.filechooserdialog.ShellFileChooser;
import com.waldo.inventory.gui.dialogs.importfromcsvdialog.ReadCsvDialogLayout;

import javax.swing.*;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static javax.swing.SpringLayout.*;

public abstract class ProjectTypesDialogLayout extends IDialog implements
        GuiInterface,
        ListSelectionListener,
        DbObjectChangedListener<ProjectType>,
        IObjectSearchPanel.IObjectSearchListener,
        IObjectSearchPanel.IObjectSearchBtnListener,
        IdBToolBar.IdbToolBarListener,
        IEditedListener {

    /*
 *                  COMPONENTS
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<ProjectType> projectTypeList;
    DefaultListModel<ProjectType> projectTypeModel;
    private IdBToolBar toolBar;
    private IObjectSearchPanel searchPanel;

    ITextField detailName;
    ITextField detailExtension;
    ILabel detailLogo;
    ICheckBox detailOpenAsFolder;
    ICheckBox detailUseDefaultLauncher;
    ITextField detailLauncherPath;
    JButton launcherFileBtn;

    private JList<Project> detailProjectList;
    DefaultListModel<Project> detailProjectModel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectType selectedProjectType;
    ProjectType originalProjectType;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectTypesDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        if (selectedProjectType == null || selectedProjectType.isUnknown()) {
            toolBar.setDeleteActionEnabled(false);
            toolBar.setEditActionEnabled(false);
        } else {
            toolBar.setDeleteActionEnabled(true);
            toolBar.setEditActionEnabled(true);
        }
    }


    private JPanel createWestPanel() {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Project types");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        JPanel westPanel = new JPanel();
        JScrollPane list = new JScrollPane(projectTypeList);

        SpringLayout layout = new SpringLayout();
        // Search panel
        layout.putConstraint(NORTH, searchPanel, 5, NORTH, westPanel);
        layout.putConstraint(EAST, searchPanel, -5, EAST, westPanel);
        layout.putConstraint(WEST, searchPanel, 5, WEST, westPanel);

        // Sub division list
        layout.putConstraint(EAST, list, -5, EAST, westPanel);
        layout.putConstraint(WEST, list, 5, WEST, westPanel);
        layout.putConstraint(SOUTH, list, -5, NORTH, toolBar);
        layout.putConstraint(NORTH, list, 2, SOUTH, searchPanel);

        // Tool bar
        layout.putConstraint(EAST, toolBar, -5, EAST, westPanel);
        layout.putConstraint(SOUTH, toolBar, -5, SOUTH, westPanel);
        layout.putConstraint(WEST, toolBar, 5, WEST, westPanel);

        // Add stuff
        westPanel.add(searchPanel);
        westPanel.add(list);
        westPanel.add(toolBar);
        westPanel.setLayout(layout);
        westPanel.setPreferredSize(new Dimension(300, 500));
        westPanel.setBorder(titledBorder);

        return westPanel;
    }

    private JPanel createProjectTypeDetailsPanel() {

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Info");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        JPanel panel = new JPanel(new BorderLayout(5,5));

        // Text fields
        JPanel textFieldPanel = new JPanel(new GridBagLayout());

        // - Name
        ILabel nameLabel = new ILabel("Name: ");
        nameLabel.setHorizontalAlignment(ILabel.RIGHT);
        nameLabel.setVerticalAlignment(ILabel.CENTER);

        // - Extension
        ILabel extensionLabel = new ILabel("Extension: ");
        extensionLabel.setHorizontalAlignment(ILabel.RIGHT);
        extensionLabel.setVerticalAlignment(ILabel.CENTER);

        // - Launcher path
        ILabel launcherLabel = new ILabel("Launcher: ");
        launcherLabel.setHorizontalAlignment(ILabel.RIGHT);
        launcherLabel.setVerticalAlignment(ILabel.CENTER);

        // - Browse panel
        JPanel launcherPanel = PanelUtils.createFileOpenPanel(detailLauncherPath, launcherFileBtn);

        // - Add to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        textFieldPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 3;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        textFieldPanel.add(detailName, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        textFieldPanel.add(extensionLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 3;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textFieldPanel.add(detailExtension, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        textFieldPanel.add(detailOpenAsFolder, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        textFieldPanel.add(detailUseDefaultLauncher, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        textFieldPanel.add(launcherLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
//        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        textFieldPanel.add(launcherPanel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 4; gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        textFieldPanel.add(detailLogo, gbc);

        // Item list
        JPanel listPanel = new JPanel(new GridBagLayout());

        JLabel projectsLabel = new JLabel("Projects: ");

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        listPanel.add(projectsLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        listPanel.add(new JScrollPane(detailProjectList), gbc);

        // Add all
        panel.add(textFieldPanel, BorderLayout.NORTH);
        panel.add(listPanel, BorderLayout.CENTER);
        panel.setBorder(titledBorder);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(imageResource.readImage("ProjectTypeDialog.TitleIcon"));
        setTitleName("Project types");
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_PROJECT_TYPE);
        searchPanel.addSearchListener(this);
        searchPanel.addSearchBtnListener(this);

        // Project type list
        projectTypeModel = new DefaultListModel<>();
        projectTypeList = new JList<>(projectTypeModel);
        projectTypeList.addListSelectionListener(this);

        toolBar = new IdBToolBar(this, IdBToolBar.HORIZONTAL);
        toolBar.setFloatable(false);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailExtension = new ITextField("Extension");
        detailExtension.addEditedListener(this, "extension");
        detailLogo = new ILabel();
        detailLogo.setHorizontalAlignment(SwingConstants.RIGHT);

        detailProjectModel = new DefaultListModel<>();
        detailProjectList = new JList<>(detailProjectModel);

        detailOpenAsFolder = new ICheckBox("Open as folder");
        detailOpenAsFolder.addEditedListener(this, "openAsFolder");

        detailUseDefaultLauncher = new ICheckBox("Use default launcher", true);
        detailUseDefaultLauncher.addEditedListener(this, "useDefaultLauncher");

        detailLauncherPath = new ITextField("Launcher");
        detailLauncherPath.setEnabled(false);
        detailLauncherPath.addEditedListener(this, "launcherPath");

        launcherFileBtn = new JButton(imageResource.readImage("Common.BrowseIcon"));
        launcherFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = ShellFileChooser.getFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showDialog(ProjectTypesDialogLayout.this, "Open") == JFileChooser.APPROVE_OPTION) {
                detailLauncherPath.setText(fileChooser.getSelectedFile().getPath());
                detailLauncherPath.fireValueChanged();
            }
        });
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(createWestPanel(), BorderLayout.WEST);

        getContentPanel().add(createProjectTypeDetailsPanel(), BorderLayout.CENTER);

        pack();
    }
}
