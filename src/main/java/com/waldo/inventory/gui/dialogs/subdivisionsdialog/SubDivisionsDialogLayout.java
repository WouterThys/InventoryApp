package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemListener;
import java.sql.SQLException;

import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.SOUTH;

public abstract class SubDivisionsDialogLayout extends IDialogPanel
        implements GuiInterface {

    private static final Logger LOG = LoggerFactory.getLogger(SubDivisionsDialogLayout.class);

    static final int CATEGORIES = 0;
    static final int PRODUCTS = 1;
    static final int TYPES = 2;

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<String> subDivisionList;

    private DefaultListModel<DbObject> detailListModel;
    JList<DbObject> detailList;

    private IdBToolBar toolBar;
    ITextField searchField;
    ITitledPanel detailsPanel;

    DefaultComboBoxModel<DbObject> selectionCbModel;
    JComboBox<DbObject> selectionComboBox;

    private JLabel selectionLabel;
    ILabel iconLabel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    int selectedSubType; // Selection between categories, products or types
    DbObject selectedObject;
    Action searchAction;

    ListSelectionListener subDivisionChangedAction;
    ListSelectionListener detailChangedAction;
    ItemListener selectionCbIndexChanged;
    DbObjectChangedListener<Category> categoriesChanged;
    DbObjectChangedListener<Product> productsChanged;
    DbObjectChangedListener<Type> typesChanged;

    public SubDivisionsDialogLayout(Application application, JDialog dialog) {
        super(application, dialog, true);
        setCategoriesChanged();
        setProductsChanged();
        setTypesChanged();
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel();
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
        subDivisionList.addListSelectionListener(subDivisionChangedAction);

        // Search field
        searchField = new ITextField("Search");
        searchField.setMaximumSize(new Dimension(100,30));
        searchField.addActionListener(searchAction);

        // Search button
        JButton searchButton = new JButton(resourceManager.readImage("Common.Search"));
        searchButton.addActionListener(searchAction);

        // Combo box
        selectionCbModel = new DefaultComboBoxModel<>();
        selectionComboBox = new JComboBox<>(selectionCbModel);
        selectionComboBox.addItemListener(selectionCbIndexChanged);

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
                        dialog = new DbObjectDialog<>(application, "New product", new Type());
                        if (dialog.showDialog() == DbObjectDialog.OK) {
                            Product p = (Product) selectionCbModel.getSelectedItem();
                            ((Type)dialog.getDbObject()).setProductId(p.getId());
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
        detailList.addListSelectionListener(detailChangedAction);

        selectionLabel = new JLabel("Categories");
        detailsPanel = new ITitledPanel("Categories",
                new JComponent[] {createDetailsPanel()});

        subDivisionList.setSelectedIndex(0);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(new ITitledPanel("Sub divisions",
                new JComponent[] {searchField, new JScrollPane(subDivisionList)}
        ), BorderLayout.WEST);

        getContentPanel().add(detailsPanel, BorderLayout.CENTER);
        setPositiveButton("Ok").addActionListener(e -> close());
    }

    @Override
    public void updateComponents(Object object) {
        switch (selectedSubType) {
            case CATEGORIES:
                selectionLabel.setVisible(false);
                selectionComboBox.setVisible(false);
                break;

            case PRODUCTS:
                selectionLabel.setText("Select a category");
                selectionLabel.setVisible(true);
                selectionComboBox.setVisible(true);
                break;

            case TYPES:
                selectionLabel.setText("Select a product");
                selectionLabel.setVisible(true);
                selectionComboBox.setVisible(true);
                break;
        }
    }

    private void setCategoriesChanged() {
        categoriesChanged = new DbObjectChangedListener<Category>() {
            @Override
            public void onAdded(Category object) {
                updateCategoryList();
            }

            @Override
            public void onUpdated(Category object) {
                updateCategoryList();
            }

            @Override
            public void onDeleted(Category object) {
                updateCategoryList();
            }
        };
    }

    /**
     * If the view is on types, the cb on top is showing products.
     * Get the id of which product is selected, than get all types for this product, and add them to the
     * Detail list
     */
    void updateTypeList() {
        if (selectedSubType == TYPES) {
            long productId;
            DbObject obj = ((DbObject)selectionCbModel.getSelectedItem());
            if (obj != null) {
                productId = obj.getId();
                if (productId < 0) {
                    productId = 1; // Unknown
                }
                detailListModel.removeAllElements();
                for (Type t : DbManager.dbInstance().getTypeListForProduct(productId)) {
                    detailListModel.addElement(t);
                }
                LOG.debug(obj.getName() + " updated in type list. Product id = " + productId + ". Element count: " + detailListModel.size());
            } else {
                LOG.debug("Type list not updated, object is null");
            }
        } else {
            LOG.error("Type notification for change, but selectedSubType is not TYPES: " + selectedSubType);
        }
    }

    private void setProductsChanged() {
        productsChanged = new DbObjectChangedListener<Product>() {
            @Override
            public void onAdded(Product object) {
                updateProductList();
            }

            @Override
            public void onUpdated(Product object) {
                updateProductList();
            }

            @Override
            public void onDeleted(Product object) {
                updateProductList();
            }
        };
    }

    void updateProductList() {
        if (selectedSubType == PRODUCTS) {
            long categoryId;
            DbObject obj = ((DbObject)selectionCbModel.getSelectedItem());
            if (obj != null) {
                categoryId = obj.getId();
                if (categoryId < 0) {
                    categoryId = 1; // Unknown
                }
                detailListModel.removeAllElements();
                for (Product p : DbManager.dbInstance().getProductListForCategory(categoryId)) {
                    detailListModel.addElement(p);
                }
                LOG.debug(obj.getName() + " updated in product list. Category id = " + categoryId + ". Element count: " + detailListModel.size());
            } else {
                LOG.debug("Category list not updated, object is null");
            }
        } else {
            LOG.error("Product notification for change, but selectedSubType is not PRODUCTS: " + selectedSubType);
        }
    }

    private void setTypesChanged() {
        typesChanged = new DbObjectChangedListener<Type>() {
            @Override
            public void onAdded(Type object) {
                updateTypeList();
            }

            @Override
            public void onUpdated(Type object) {
                updateTypeList();
            }

            @Override
            public void onDeleted(Type object) {
                updateTypeList();
            }
        };
    }

    void updateCategoryList() {
        if (selectedSubType == CATEGORIES) {
            detailListModel.removeAllElements();
            for (Category c : DbManager.dbInstance().getCategories()) {
                detailListModel.addElement(c);
            }
            LOG.debug("Category list updated" + ". Element count: " + detailListModel.size());
        } else {
            LOG.error("Category notification for change, but selectedSubType is not CATEGORIES: " + selectedSubType);
        }
    }
}
