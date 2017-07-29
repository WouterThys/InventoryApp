package com.waldo.inventory.gui.dialogs.packagedialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.DimensionType;
import com.waldo.inventory.classes.PackageType;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IDimensionTypeTableModel;

import javax.swing.*;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.List;
import java.util.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static javax.swing.SpringLayout.*;

public abstract class PackageTypeDialogLayout extends IDialog implements
        IObjectSearchPanel.IObjectSearchListener,
        IObjectSearchPanel.IObjectSearchBtnListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        IEditedListener {


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<PackageType> packageList;
    DefaultListModel<PackageType> packageDefaultListModel;
    IdBToolBar listToolBar;
    private IObjectSearchPanel searchPanel;

    ITextField detailName;
    ITextArea detailDescription;

    private IDimensionTypeTableModel tableModel;
    private ITable detailTypeTable;
    IdBToolBar detailToolBar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    PackageType selectedPackageType;
    PackageType originalPackageType;

    DimensionType selectedDimensionType;
    DimensionType originalDimensionType;


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
        listToolBar.setDeleteActionEnabled(selectedPackageType != null && !selectedPackageType.isUnknown());
        listToolBar.setEditActionEnabled(selectedPackageType != null && !selectedPackageType.isUnknown());
        detailDescription.setEnabled(selectedPackageType != null && !selectedPackageType.isUnknown());

        detailToolBar.setDeleteActionEnabled(selectedDimensionType != null);
        detailToolBar.setEditActionEnabled(selectedDimensionType != null);

        detailTypeTable.setEnabled(selectedPackageType != null);
    }

    void dimensionTableUpdate() {
        if (selectedPackageType != null) {
            tableModel.setItemList(SearchManager.sm().findDimensionTypesForPackageType(selectedPackageType.getId()));
        } else {
            tableModel.clearItemList();
        }
    }

    void dimensionTableDelete(DimensionType typeToDelete) {
        java.util.List<DimensionType> tmp = new ArrayList<>();
        tmp.add(typeToDelete);
        tableModel.removeItems(tmp);
    }

    void dimensionTableDelete(java.util.List<DimensionType> typesToDelete) {
        tableModel.removeItems(typesToDelete);
    }

    void dimensionTableAdd(DimensionType typeToAdd) {
        java.util.List<DimensionType> tmp = new ArrayList<>();
        tmp.add(typeToAdd);
        tableModel.addItems(tmp);
    }

    DimensionType dimensionTableGetSelected() {
        int row = detailTypeTable.getSelectedRow();
        return (DimensionType) detailTypeTable.getValueAtRow(row);
    }

    java.util.List<DimensionType> getSelectedDimensionTypes() {
        java.util.List<DimensionType> setItems = new ArrayList<>();
        int[] selectedRows = detailTypeTable.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int row : selectedRows) {
                DimensionType si = (DimensionType) detailTypeTable.getValueAtRow(row);
                if (si != null) {
                    setItems.add(si);
                }
            }
        }
        return setItems;
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
        layout.putConstraint(SOUTH, list, -5, NORTH, listToolBar);
        layout.putConstraint(NORTH, list, 2, SOUTH, searchPanel);

        // Tool bar
        layout.putConstraint(EAST, listToolBar, -5, EAST, panel);
        layout.putConstraint(SOUTH, listToolBar, -5, SOUTH, panel);
        layout.putConstraint(WEST, listToolBar, 5, WEST, panel);

        // Add stuff
        panel.add(searchPanel);
        panel.add(list);
        panel.add(listToolBar);
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
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane pane = new JScrollPane(detailDescription);
        pane.setPreferredSize(new Dimension(100, 100));

        // Text fields
        JPanel textFieldPanel = new JPanel(new GridBagLayout());

        // - Name
        ILabel nameLabel = new ILabel("Name: ");
        nameLabel.setHorizontalAlignment(ILabel.RIGHT);
        nameLabel.setVerticalAlignment(ILabel.CENTER);

        // Description
        ILabel descriptionLabel = new ILabel("Description: ");
        descriptionLabel.setHorizontalAlignment(ILabel.RIGHT);
        descriptionLabel.setVerticalAlignment(ILabel.CENTER);

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
        textFieldPanel.add(descriptionLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 3;
        gbc.gridy = 2; gbc.weighty = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        textFieldPanel.add(pane, gbc);

        // TABLE
        tablePanel.add(new JScrollPane(detailTypeTable), BorderLayout.CENTER);
        tablePanel.add(detailToolBar, BorderLayout.EAST);

        // Add all
        panel.add(textFieldPanel, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.SOUTH);
        panel.setBorder(titledBorder);

        return panel;
    }

     /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title and neutral button
        setTitleIcon(imageResource.readImage("PackageDialog.TitleIcon"));
        setTitleName("Package Types");
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_PACKAGE, DbObject.TYPE_PACKAGE_TYPE);
        searchPanel.addSearchListener(this);
        searchPanel.addSearchBtnListener(this);

        // Packages list
        packageDefaultListModel = new DefaultListModel<>();
        packageList = new JList<>(packageDefaultListModel);
        packageList.addListSelectionListener(this);

        // Tool bar
        listToolBar = new IdBToolBar(this, IdBToolBar.HORIZONTAL);
        listToolBar.setFloatable(false);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailDescription = new ITextArea("Description", 20, 15);
        detailDescription.setLineWrap(true); // Go to next line when area is full
        detailDescription.setWrapStyleWord(true); // Don't cut words in two
        detailDescription.addEditedListener(this, "description");

        tableModel = new IDimensionTypeTableModel();
        detailTypeTable = new ITable(tableModel);
        detailTypeTable.getSelectionModel().addListSelectionListener(this);
        detailToolBar = new IdBToolBar(this, IdBToolBar.VERTICAL);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(createWestPanel(), BorderLayout.WEST);
        getContentPanel().add(createPackageDetailPanel(), BorderLayout.CENTER);

        pack();
    }
}
