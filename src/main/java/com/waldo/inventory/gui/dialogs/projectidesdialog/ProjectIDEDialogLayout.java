package com.waldo.inventory.gui.dialogs.projectidesdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.ProjectTypes;
import com.waldo.inventory.classes.dbclasses.Project;
import com.waldo.inventory.classes.dbclasses.ProjectIDE;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IImagePanel;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static javax.swing.SpringLayout.*;

abstract class ProjectIDEDialogLayout extends IDialog implements
        ListSelectionListener,
        IObjectSearchPanel.SearchListener<ProjectIDE>,
        IdBToolBar.IdbToolBarListener,
        IEditedListener,
        ActionListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<ProjectIDE> projectIdeList;
    DefaultListModel<ProjectIDE> projectIdeModel;
    private IdBToolBar toolBar;
    IObjectSearchPanel<ProjectIDE> searchPanel;

    ITextField detailName;
    IImagePanel detailLogo;

    private JList<Project> detailProjectList;
    DefaultListModel<Project> detailProjectModel;

    IComboBox<ProjectTypes> projectTypeCb;

    JButton detailLauncherBtn;
    JButton detailDetectionBtn;
    JButton detailParserBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectIDE selectedProjectIDE;
    ProjectIDE originalProjectIDE;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ProjectIDEDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean enabled = !(selectedProjectIDE == null || selectedProjectIDE.isUnknown());

        toolBar.setDeleteActionEnabled(enabled);
        toolBar.setEditActionEnabled(enabled);
        detailLauncherBtn.setEnabled(enabled);
        detailDetectionBtn.setEnabled(enabled);
        detailParserBtn.setEnabled(enabled);
        projectTypeCb.setEnabled(enabled);

    }

    void setProjectIdeList(List<ProjectIDE> ideList) {
        if (ideList != null) {
            ideList.sort(new ComparatorUtils.DbObjectNameComparator<>());
            projectIdeModel.removeAllElements();
            for (ProjectIDE p : ideList) {
                projectIdeModel.addElement(p);
            }
        }
    }

    private JPanel createWestPanel() {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Project IDEs");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        JPanel westPanel = new JPanel();
        JScrollPane list = new JScrollPane(projectIdeList);

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

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.add(detailLogo, BorderLayout.EAST);

        // - Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(detailParserBtn);
        buttonPanel.add(detailDetectionBtn);
        buttonPanel.add(detailLauncherBtn);

        // Text fields
        JPanel textFieldPanel = new JPanel(new GridBagLayout());
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(textFieldPanel);
        gbc.addLine("Name: ", detailName);
        gbc.addLine("Type", projectTypeCb);
        gbc.addLine("", buttonPanel);
        gbc.add(logoPanel, 1, 3, 1, 1);

        // Item list
        JPanel listPanel = new JPanel(new GridBagLayout());
        gbc = new GuiUtils.GridBagHelper(listPanel);
        gbc.addLineVertical("Projects: ", new JScrollPane(detailProjectList), GridBagConstraints.BOTH);

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
        setTitleIcon(imageResource.readIcon("Toolkit.L"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel<>(cache().getProjectIDES(), this);

        // Project type list
        projectIdeModel = new DefaultListModel<>();
        projectIdeList = new JList<>(projectIdeModel);
        projectIdeList.addListSelectionListener(this);

        toolBar = new IdBToolBar(this, IdBToolBar.HORIZONTAL);
        toolBar.setFloatable(false);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailLogo = new IImagePanel(this, Statics.ImageType.IdeImage, null, new Dimension(128, 128), this);

        List<ProjectTypes> types = Arrays.asList(ProjectTypes.values());
        projectTypeCb = new IComboBox<>(types, null, true);
        projectTypeCb.addEditedListener(this, "projectType", String.class);

        detailProjectModel = new DefaultListModel<>();
        detailProjectList = new JList<>(detailProjectModel);

        detailLauncherBtn = new JButton("Launcher");
        detailLauncherBtn.addActionListener(this);

        detailDetectionBtn = new JButton("Detection");
        detailDetectionBtn.addActionListener(this);

        detailParserBtn = new JButton("Parser");
        detailParserBtn.addActionListener(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(createWestPanel(), BorderLayout.WEST);

        getContentPanel().add(createProjectTypeDetailsPanel(), BorderLayout.CENTER);

        pack();
    }
}
