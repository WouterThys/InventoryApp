package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class MainPanel extends MainPanelLayout {

    private Application application;

    private DbObjectChangedListener<Item> itemsChanged;
    private DbObjectChangedListener<Category> categoriesChanged;
    private DbObjectChangedListener<Product> productsChanged;
    private DbObjectChangedListener<Type> typesChanged;

    public MainPanel(Application application) {
        URL url = MainPanel.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
        this.application = application;

        initializeComponents();
        initializeLayouts();
        initActions();
        initializeListeners();

        dbInstance().addOnItemsChangedListener(itemsChanged);
        dbInstance().addOnCategoriesChangedListener(categoriesChanged);
        dbInstance().addOnProductsChangedListener(productsChanged);
        dbInstance().addOnTypesChangedListener(typesChanged);

        updateComponents(null);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public DbObject getLastSelectedDivision() {
        return lastSelectedDivision;
    }

    public ItemTableModel getTableModel() {
        return tableModel;
    }

    private void initActions() {
        initMouseClicked();
    }

    private void initMouseClicked() {
        itemTable.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                if (e.getClickCount() == 2) {
                    Item selectedItem = application.getSelectedItem();
                    EditItemDialog dialog = new EditItemDialog(application, "Item", selectedItem);
                    if (dialog.showDialog() == EditItemDialog.OK) {
                        dialog.getItem().save();
                    }
                }
            }
        });
    }

    private void initializeListeners() {
        setItemsChangedListener();
        setCategoriesChangedListener();
        setProductsChangedListener();
        setTypesChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new DbObjectChangedListener<Item>() {
            @Override
            public void onAdded(Item item) {
                selectedItem = item;
                updateItems();
            }

            @Override
            public void onUpdated(Item item) {
                selectedItem = item;
                updateItems();
            }

            @Override
            public void onDeleted(Item item) {
                selectedItem = null;
                updateItems();
            }
        };
    }

    private void updateItems() {
        updateComponents(null);
    }

    private void setCategoriesChangedListener() {
        categoriesChanged = new DbObjectChangedListener<Category>() {
            @Override
            public void onAdded(Category object) {
                //tableModel.fireTable
                updateComponents(null);
            }

            @Override
            public void onUpdated(Category object) {
                updateComponents(null);
            }

            @Override
            public void onDeleted(Category object) {
                updateComponents(null);
            }
        };
    }

    private void setProductsChangedListener() {
        productsChanged = new DbObjectChangedListener<Product>() {
            @Override
            public void onAdded(Product object) {

            }

            @Override
            public void onUpdated(Product object) {

            }

            @Override
            public void onDeleted(Product object) {

            }
        };
    }

    private void setTypesChangedListener() {
        typesChanged = new DbObjectChangedListener<Type>() {
            @Override
            public void onAdded(Type object) {

            }

            @Override
            public void onUpdated(Type object) {

            }

            @Override
            public void onDeleted(Type object) {

            }
        };
    }

    //
    // Tree model interface
    //

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        System.out.print(e);
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        System.out.print(e);
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        System.out.print(e);
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        System.out.print(e);
    }

    //
    // Tree selection interface
    //

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DivisionTreeModel.DbObjectNode node = (DivisionTreeModel.DbObjectNode) subDivisionTree.getLastSelectedPathComponent();

        if (node == null) {
            lastSelectedDivision = null;
            return; // Nothing selected
        }

        selectedItem = null;
        lastSelectedDivision = node.getDbObject();
        application.clearSearch();

        updateComponents(lastSelectedDivision);
    }

    //
    // Table selection changed
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = itemTable.getSelectedRow();
            if (row >= 0) {
                selectedItem = getItemAt(itemTable.getSelectedRow());
                updateComponents(null);
            }
        }
    }

//    @Override
//    public void focusGained(FocusEvent e) {
//        //
//    }
//
//    @Override
//    public void focusLost(FocusEvent e) {
//        selectedItem = null;
//        updateComponents(null);
//    }
}
