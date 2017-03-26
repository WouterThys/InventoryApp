package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.database.CategoriesChangedListener;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.ProductsChangedListener;
import com.waldo.inventory.database.TypesChangedListener;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialogPanel;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.sql.SQLException;

import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.SOUTH;

public abstract class SubDivisionsDialogLayout extends IDialogPanel
        implements GuiInterface, CategoriesChangedListener, ProductsChangedListener, TypesChangedListener {

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

    private JToolBar toolBar;
    ITextField searchField;
    ITitledPanel detailsPanel;

    DefaultComboBoxModel<DbObject> selectionCbModel;
    JComboBox<DbObject> selectionComboBox;

    private JLabel selectionLabel;
    JLabel iconLabel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    int selectedSubType; // Selection between categories, products or types
    Action addAction;
    Action deleteAction;
    Action editAction;
    Action searchAction;

    ListSelectionListener subDivisionChangedAction;
    ListSelectionListener detailChangedAction;
    ItemListener selectionCbIndexChanged;

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
        iconLabel = new JLabel(resourceManager.readImage("SubDivisionDialog.EditIcon"));

        // Toolbar
        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.add(addAction);
        toolBar.add(deleteAction);
        toolBar.add(editAction);
        toolBar.setMaximumSize(new Dimension(toolBar.getPreferredSize()));

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

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        setPositiveButton("Ok");
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



    @Override
    public void onTypeAdded(Type type) {
        updateTypeList();
    }

    @Override
    public void onTypeUpdated(Type type) {
        updateTypeList();
    }

    @Override
    public void onTypeDeleted(Type type) {
        updateTypeList();
    }

    /**
     * If the view is on types, the cb on top is showing products.
     * Get the id of which product is selected, than get all types for this product, and add them to the
     * Detail list
     */
    void updateTypeList() {
        if (selectedSubType == TYPES) {
            long productId = -1;
            DbObject obj = ((DbObject)selectionCbModel.getSelectedItem());
            if (obj != null) {
                productId = obj.getId();
                if (productId < 0) {
                    productId = 1; // Unknown
                }
                detailListModel.removeAllElements();
                try {
                    for (Type t : DbManager.dbInstance().getTypeListForProduct(productId)) {
                        detailListModel.addElement(t);
                    }
                    LOG.debug(obj.getName() + " updated in type list. Product id = " + productId + ". Element count: " + detailListModel.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                LOG.debug("Type list not updated, object is null");
            }
        } else {
            LOG.error("Type notification for change, but selectedSubType is not TYPES: " + selectedSubType);
        }
    }

    @Override
    public void onProductAdded(Product product) {
        updateProductList();
    }

    @Override
    public void onProductUpdated(Product product) {
        updateProductList();
    }

    @Override
    public void onProductDeleted(Product product) {
        updateProductList();
    }

    void updateProductList() {
        if (selectedSubType == PRODUCTS) {
            long categoryId = -1;
            DbObject obj = ((DbObject)selectionCbModel.getSelectedItem());
            if (obj != null) {
                categoryId = obj.getId();
                if (categoryId < 0) {
                    categoryId = 1; // Unknown
                }
                detailListModel.removeAllElements();
                try {
                    for (Product p : DbManager.dbInstance().getProductListForCategory(categoryId)) {
                        detailListModel.addElement(p);
                    }
                    LOG.debug(obj.getName() + " updated in product list. Category id = " + categoryId + ". Element count: " + detailListModel.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                LOG.debug("Category list not updated, object is null");
            }
        } else {
            LOG.error("Product notification for change, but selectedSubType is not PRODUCTS: " + selectedSubType);
        }
    }

    @Override
    public void onCategoryAdded(Category category) {
        updateCategoryList();
    }

    @Override
    public void onCategoryUpdated(Category category) {
        updateCategoryList();
    }

    @Override
    public void onCategoryDeleted(Category category) {
        updateCategoryList();
    }

    void updateCategoryList() {
        if (selectedSubType == CATEGORIES) {
            detailListModel.removeAllElements();
            try {
                for (Category c : DbManager.dbInstance().getCategories()) {
                    detailListModel.addElement(c);
                }
                LOG.debug("Category list updated" + ". Element count: " + detailListModel.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            LOG.error("Category notification for change, but selectedSubType is not CATEGORIES: " + selectedSubType);
        }
    }
}
