package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.SOUTH;

public abstract class SubDivisionsDialogLayout extends IDialog
        implements GuiInterface, IObjectSearchPanel.IObjectSearchListener {

    private static final Logger LOG = LoggerFactory.getLogger(SubDivisionsDialogLayout.class);

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
    IObjectSearchPanel searchPanel;
    ITitledPanel detailsPanel;

    DefaultComboBoxModel<DbObject> selectionCbModel;
    JComboBox<DbObject> selectionComboBox;

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

    private JPanel createDetailsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
    JPanel northPanel = new JPanel(new GridBagLayout());
    JPanel centerPanel = new JPanel(new GridBagLayout());
    JScrollPane list = new JScrollPane(detailList);

    SpringLayout layout = new SpringLayout();

// Label
        layout.putConstraint(WEST, selectionLabel, 5, WEST, panel);
                layout.putConstraint(NORTH, selectionLabel, 5, NORTH, panel);

                // Combobox
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
                layout.putConstraint(EAST, iconLabel,-5, EAST, panel);
                layout.putConstraint(SOUTH, iconLabel, -5, SOUTH, panel);

                // Add stuff
                panel.add(selectionLabel);
                panel.add(selectionComboBox);
                panel.add(list);
                panel.add(toolBar);
                panel.add(iconLabel);
                panel.setPreferredSize(new Dimension(400,500));
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
        setTitleIcon(resourceManager.readImage("SubDivisionDialog.TitleIcon"));

        // Sub divisions list
        String[] subDivisions = new String[] {"Categories", "Products", "Types"};
        subDivisionList = new JList<>(subDivisions);

        // Search field
        searchPanel = new IObjectSearchPanel(false, this, DbObject.TYPE_CATEGORY, DbObject.TYPE_PRODUCT, DbObject.TYPE_TYPE);

        // Combo box
        selectedSubType = 0;
        selectionCbModel = new DefaultComboBoxModel<>();
        selectionComboBox = new JComboBox<>(selectionCbModel);

        // Icon
        iconLabel = new ILabel(resourceManager.readImage("SubDivisionDialog.EditIcon"));

        // Toolbar
        toolBar = new IdBToolBar(IdBToolBar.VERTICAL) {
            @Override
            protected void refresh() {
                updateComponents(null);
            }

            @Override
            protected void add() {
                DbObjectDialog dialog;
                switch (selectedSubType) {
                    case CATEGORIES:
                        dialog = new DbObjectDialog<>(application, "New category", new Category());
                        if (dialog.showDialog() == DbObjectDialog.OK) {
                            dialog.getDbObject().save();
                        }
                        break;
                    case PRODUCTS:
                        dialog = new DbObjectDialog<>(application, "New product", new Product());
                        if (dialog.showDialog() == DbObjectDialog.OK) {
                            Category c = (Category) selectionCbModel.getSelectedItem();
                            ((Product)dialog.getDbObject()).setCategoryId(c.getId());
                            dialog.getDbObject().save();
                        }
                        break;
                    case TYPES:
                        dialog = new DbObjectDialog<>(application, "New product", new com.waldo.inventory.classes.Type());
                        if (dialog.showDialog() == DbObjectDialog.OK) {
                            Product p = (Product) selectionCbModel.getSelectedItem();
                            ((com.waldo.inventory.classes.Type)dialog.getDbObject()).setProductId(p.getId());
                            dialog.getDbObject().save();
                        }
                        break;
                }
            }

            @Override
            protected void delete() {
                if (selectedObject != null) {
                    int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete \"" + selectedObject.getName() + "\"?");
                    if (res == JOptionPane.OK_OPTION) {
                        selectedObject.delete();
                        selectedObject = null;
                    }
                }
            }

            @Override
            protected void update() {
                if (selectedObject != null) {
                    DbObjectDialog dialog = new DbObjectDialog<>(application, "Update " + selectedObject.getName(), selectedObject);
                    if (dialog.showDialog() == DbObjectDialog.OK) {
                        selectedObject.save();
                    }
                }
            }
        };
        toolBar.setFloatable(false);

        // Details
        detailListModel = new DefaultListModel<>();
        detailList = new JList<>(detailListModel);

        selectionLabel = new JLabel("Categories");
        detailsPanel = new ITitledPanel("Categories",
                new JComponent[] {createDetailsPanel()});

        subDivisionList.setSelectedIndex(0);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        // Left part
        getContentPanel().add(createWestPanel(), BorderLayout.WEST);

        // Center
        getContentPanel().add(detailsPanel, BorderLayout.CENTER);
    }
}


