package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static javax.swing.SpringLayout.*;

public abstract class SubDivisionsDialogLayout extends IDialog implements
        GuiInterface,
        IObjectSearchPanel.IObjectSearchListener,
        IObjectSearchPanel.IObjectSearchBtnListener,
        IdBToolBar.IdbToolBarListener {

    static final int CATEGORIES = 0;
    static final int PRODUCTS = 1;
    static final int TYPES = 2;

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<String> subDivisionList;

    DefaultListModel<DbObject> detailListModel;
    JList<DbObject> detailList;

    private IdBToolBar toolBar;
    private IObjectSearchPanel searchPanel;
    ITitledPanel detailsPanel;

    DefaultComboBoxModel<DbObject> selectionCbModel;
    IComboBox<DbObject> selectionComboBox;

    JLabel selectionLabel;
    ILabel iconLabel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    int selectedSubType; // Selection between categories, products or types
    DbObject selectedObject;

    DbObjectChangedListener<Category> categoriesChanged;
    DbObjectChangedListener<Product> productsChanged;
    DbObjectChangedListener<com.waldo.inventory.classes.Type> typesChanged;

    public SubDivisionsDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void updateEnabledComponents() {
        if (selectedObject == null) {
            toolBar.setDeleteActionEnabled(false);
            toolBar.setEditActionEnabled(false);
        } else {
            toolBar.setDeleteActionEnabled(true);
            toolBar.setEditActionEnabled(true);
        }
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane list = new JScrollPane(detailList);

        SpringLayout layout = new SpringLayout();

        // Label
        layout.putConstraint(WEST, selectionLabel, 5, WEST, panel);
        layout.putConstraint(NORTH, selectionLabel, 5, NORTH, panel);

        // Combo box
        layout.putConstraint(WEST, selectionComboBox, 5, WEST, panel);
        layout.putConstraint(NORTH, selectionComboBox, 2, SOUTH, selectionLabel);
        layout.putConstraint(EAST, selectionComboBox, -5, EAST, panel);

        // List
        layout.putConstraint(WEST, list, 5, WEST, panel);
        layout.putConstraint(NORTH, list, 5, SOUTH, selectionComboBox);
        layout.putConstraint(SOUTH, list, -5, SOUTH, panel);
        layout.putConstraint(EAST, list, 0, WEST, toolBar);

        // Toolbar
        layout.putConstraint(EAST, toolBar, -5, EAST, panel);
        layout.putConstraint(NORTH, toolBar, 5, SOUTH, selectionComboBox);

        // Icon
        layout.putConstraint(EAST, iconLabel, -5, EAST, panel);
        layout.putConstraint(SOUTH, iconLabel, -5, SOUTH, panel);

        // Add stuff
        panel.add(selectionLabel);
        panel.add(selectionComboBox);
        panel.add(list);
        panel.add(toolBar);
        panel.add(iconLabel);
        panel.setPreferredSize(new Dimension(400, 500));
        panel.setLayout(layout);

        return panel;
    }

    private JPanel createWestPanel() {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Sub divisions");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        JPanel westPanel = new JPanel();
        JScrollPane list = new JScrollPane(subDivisionList);

        SpringLayout layout = new SpringLayout();
        // Search panel
        layout.putConstraint(NORTH, searchPanel, 5, NORTH, westPanel);
        layout.putConstraint(EAST, searchPanel, -5, EAST, westPanel);
        layout.putConstraint(WEST, searchPanel, 5, WEST, westPanel);

        // Sub division list
        layout.putConstraint(EAST, list, -5, EAST, westPanel);
        layout.putConstraint(WEST, list, 5, WEST, westPanel);
        layout.putConstraint(SOUTH, list, 5, SOUTH, westPanel);
        layout.putConstraint(NORTH, list, 2, SOUTH, searchPanel);

        // Add stuff
        westPanel.add(searchPanel);
        westPanel.add(list);
        westPanel.setLayout(layout);
        westPanel.setPreferredSize(new Dimension(300, 500));
        westPanel.setBorder(titledBorder);

        return westPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        // Title
        setTitleName("Sub Divisions");
        setTitleIcon(imageResource.readImage("SubDivisions.Title"));

        // Sub divisions list
        String[] subDivisions = new String[]{"Categories", "Products", "Types"};
        subDivisionList = new JList<>(subDivisions);

        // Search field
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_CATEGORY, DbObject.TYPE_PRODUCT, DbObject.TYPE_TYPE);
        searchPanel.addSearchListener(this);
        searchPanel.addSearchBtnListener(this);

        // Combo box
        selectedSubType = 0;
        selectionCbModel = new DefaultComboBoxModel<>();
        selectionComboBox = new IComboBox<>(selectionCbModel);

        // Icon
        iconLabel = new ILabel(imageResource.readImage("SubDivisions.Edit"));

        // Toolbar
        toolBar = new IdBToolBar(this, IdBToolBar.VERTICAL);
        toolBar.setFloatable(false);

        // Details
        detailListModel = new DefaultListModel<>();
        detailList = new JList<>(detailListModel);

        selectionLabel = new JLabel("Categories");
        detailsPanel = new ITitledPanel("Categories",
                new JComponent[]{createDetailsPanel()});

        subDivisionList.setSelectedIndex(0);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        // Left part
        getContentPanel().add(createWestPanel(), BorderLayout.WEST);

        // Center
        getContentPanel().add(detailsPanel, BorderLayout.CENTER);

        // Pack
        pack();
    }
}


