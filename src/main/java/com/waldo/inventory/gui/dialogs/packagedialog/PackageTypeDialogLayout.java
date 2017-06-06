package com.waldo.inventory.gui.dialogs.packagedialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.PackageType;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.WEST;

public abstract class PackageTypeDialogLayout extends IDialog implements
        GuiInterface,
        IObjectSearchPanel.IObjectSearchListener,
        DbObjectChangedListener<PackageType>,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener {


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<PackageType> packageList;
    DefaultListModel<PackageType> packageDefaultListModel;
    private IdBToolBar toolBar;
    private IObjectSearchPanel searchPanel;

    ITextField detailName;
//    JComboBox<PackageType> detailTypeCb;
//    DefaultComboBoxModel<PackageType> packageTypeCbModel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PackageType selectedPackageType;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PackageTypeDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        if (selectedPackageType == null || selectedPackageType.isUnknown()) {
            toolBar.setDeleteActionEnabled(false);
            toolBar.setEditActionEnabled(false);
        } else {
            toolBar.setDeleteActionEnabled(true);
            toolBar.setEditActionEnabled(true);
        }
    }

    private JPanel createWestPanel() {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Package Types");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        JPanel panel = new JPanel();
        JScrollPane list = new JScrollPane(packageList);

        SpringLayout layout = new SpringLayout();
        // Search panel
        layout.putConstraint(NORTH, searchPanel, 5, NORTH, panel);
        layout.putConstraint(EAST, searchPanel, -5, EAST, panel);
        layout.putConstraint(WEST, searchPanel, 5, WEST, panel);

        // Sub division list
        layout.putConstraint(EAST, list, -5, EAST, panel);
        layout.putConstraint(WEST, list, 5, WEST, panel);
        layout.putConstraint(SOUTH, list, -5, NORTH, toolBar);
        layout.putConstraint(NORTH, list, 2, SOUTH, searchPanel);

        // Tool bar
        layout.putConstraint(EAST, toolBar, -5, EAST, panel);
        layout.putConstraint(SOUTH, toolBar, -5, SOUTH, panel);
        layout.putConstraint(WEST, toolBar, 5, WEST, panel);

        // Add stuff
        panel.add(searchPanel);
        panel.add(list);
        panel.add(toolBar);
        panel.setLayout(layout);
        panel.setPreferredSize(new Dimension(300, 500));
        panel.setBorder(titledBorder);

        return panel;
    }

    private JPanel createPackageDetailPanel() {
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

        // Type
//        ILabel typeLabel = new ILabel("Type: ");
//        typeLabel.setHorizontalAlignment(ILabel.RIGHT);
//        typeLabel.setVerticalAlignment(ILabel.CENTER);

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

//        gbc.gridx = 0; gbc.weightx = 0;
//        gbc.gridy = 1; gbc.weighty = 0;
//        gbc.fill = GridBagConstraints.NONE;
//        textFieldPanel.add(typeLabel, gbc);
//
//        gbc.gridx = 1; gbc.weightx = 3;
//        gbc.gridy = 1; gbc.weighty = 0;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        textFieldPanel.add(detailTypeCb, gbc);

        // Add all
        panel.add(textFieldPanel, BorderLayout.CENTER);
        panel.setBorder(titledBorder);

        return panel;
    }

     /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(resourceManager.readImage("PackageDialog.TitleIcon"));
        setTitleName("Package Types");

        // Search
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_PACKAGE, DbObject.TYPE_PACKAGE_TYPE);
        searchPanel.addSearchListener(this);

        // Packages list
        packageDefaultListModel = new DefaultListModel<>();
        packageList = new JList<>(packageDefaultListModel);
        packageList.addListSelectionListener(this);

        // Tool bar
        toolBar = new IdBToolBar(this, IdBToolBar.HORIZONTAL);
        toolBar.setFloatable(false);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
//        packageTypeCbModel = new DefaultComboBoxModel<>();
//        detailTypeCb = new JComboBox<>(packageTypeCbModel);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(createWestPanel(), BorderLayout.WEST);
        getContentPanel().add(createPackageDetailPanel(), BorderLayout.CENTER);

        pack();
    }
}
