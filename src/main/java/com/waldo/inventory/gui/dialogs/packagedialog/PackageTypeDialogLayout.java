package com.waldo.inventory.gui.dialogs.packagedialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Package;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.tablemodels.IPackageTypeTableModel;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static javax.swing.SpringLayout.*;

abstract class PackageTypeDialogLayout extends IDialog implements
        IObjectSearchPanel.IObjectSearchListener,
        IObjectSearchPanel.IObjectSearchBtnListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        IEditedListener {


    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<Package> packageList;
    DefaultListModel<Package> packageModel;
    IdBToolBar listToolBar;
    private IObjectSearchPanel searchPanel;

    ITextField detailNameTf;
    ITextArea detailDescriptionTa;

    private IPackageTypeTableModel detailTableModel;
    private ITable<PackageType> detailTypeTable;
    private IdBToolBar detailToolBar;


    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Package selectedPackage;
    Package originalPackage;
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
        boolean packageSelected = selectedPackage != null && !selectedPackage.isUnknown();
        listToolBar.setDeleteActionEnabled(packageSelected);
        listToolBar.setEditActionEnabled(packageSelected);
        detailDescriptionTa.setEnabled(packageSelected);
        detailTypeTable.setEnabled(packageSelected);

        boolean typeSelected = packageSelected && selectedPackageType != null && !selectedPackageType.isUnknown();
        detailToolBar.setDeleteActionEnabled(typeSelected);
        detailToolBar.setEditActionEnabled(typeSelected);


    }

    void dimensionTableAddMouseAdapter(MouseAdapter mouseAdapter) {
        detailTypeTable.addMouseListener(mouseAdapter);
    }

    void typeTableInitialize(Package pack) {
        if (pack != null) {
            detailTableModel.setItemList(SearchManager.sm().findPackageTypesByPackageId(pack.getId()));
        } else {
            detailTableModel.clearItemList();
        }
    }

    void typeTableUpdate() {
        detailTableModel.updateTable();
    }

    void typeTableAdd(PackageType type) {
        List<PackageType> typeList = new ArrayList<>();
        typeList.add(type);
        detailTableModel.addItems(typeList);
    }

    void typeTableDelete(PackageType type) {
        List<PackageType> typeList = new ArrayList<>();
        typeList.add(type);
        detailTableModel.removeItems(typeList);
    }

    void typeTableDelete(List<PackageType> typeList) {
        detailTableModel.removeItems(typeList);
    }

    PackageType typeTableGetSelected() {
        int row = detailTypeTable.getSelectedRow();
        return (PackageType) detailTypeTable.getValueAtRow(row);
    }

    List<PackageType> typeTableGetAllSelected() {
        List<PackageType> typeList = new ArrayList<>();
        int[] selectedRows = detailTypeTable.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int row : selectedRows) {
                PackageType type = (PackageType) detailTypeTable.getValueAtRow(row);
                if (type != null) {
                    typeList.add(type);
                }
            }
        }
        return typeList;
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
        JScrollPane pane = new JScrollPane(detailDescriptionTa);

        // Text fields
        JPanel textFieldPanel = new JPanel(new GridBagLayout());

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(textFieldPanel);
        gbc.addLine("Name: ", detailNameTf);
        gbc.addLine("Description: ", null);
        gbc.gridwidth = 2;
        gbc.add(pane, 0, 2, 1, 1);

        // TABLE
        tablePanel.add(new JScrollPane(detailTypeTable), BorderLayout.CENTER);
        tablePanel.add(detailToolBar, BorderLayout.EAST);

        // Add all
        panel.add(textFieldPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.setBorder(titledBorder);

        return panel;
    }

     /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title and neutral button
        setTitleIcon(imageResource.readImage("Packages.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_PACKAGE, DbObject.TYPE_PACKAGE_TYPE);
        searchPanel.addSearchListener(this);
        searchPanel.addSearchBtnListener(this);

        // Packages list
        packageModel = new DefaultListModel<>();
        packageList = new JList<>(packageModel);
        packageList.addListSelectionListener(this);

        // Tool bar
        listToolBar = new IdBToolBar(this, IdBToolBar.HORIZONTAL);
        listToolBar.setFloatable(false);

        // Details
        detailNameTf = new ITextField("Name");
        detailNameTf.setEnabled(false);
        detailDescriptionTa = new ITextArea("Description", 10, 15);
        detailDescriptionTa.setLineWrap(true); // Go to next line when area is full
        detailDescriptionTa.setWrapStyleWord(true); // Don't cut words in two
        detailDescriptionTa.addEditedListener(this, "description");

        detailTableModel = new IPackageTypeTableModel();
        detailTypeTable = new ITable<>(detailTableModel);
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
