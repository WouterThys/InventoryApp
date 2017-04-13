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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class ItemListPanel extends ItemListPanelLayout {

    private Application application;

    private DbObjectChangedListener<Item> itemsChanged;
    private DbObjectChangedListener<Category> categoriesChanged;
    private DbObjectChangedListener<Product> productsChanged;
    private DbObjectChangedListener<Type> typesChanged;

    public ItemListPanel(Application application) {
        URL url = ItemListPanel.class.getResource("/settings/Settings.properties");
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
                    try {
                        selectedItem = EditItemDialog.showDialog(application, selectedItem);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    if (selectedItem != null) {
                        selectedItem.save();
                    }
                }
                if (e.getClickCount() == 1) {
                    try {
                        dataSheetColumnClicked(table.columnAtPoint(e.getPoint()), table.rowAtPoint(e.getPoint()));
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void dataSheetColumnClicked(int col, int row) throws SQLException {
        if (col == 3) { // Data sheet column
            Item item = getItemAt(row);
            if (item != null) {
                String local = item.getLocalDataSheet();
                String online = item.getOnlineDataSheet();
                if (local != null && !local.isEmpty() && online != null && !online.isEmpty()) {
                    SelectDataSheetDialog.showDialog(application, online, local);
                } else if (local != null && !local.isEmpty()) {
                    try {
                        OpenUtils.openPdf(local);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(application,
                                "Error opening the file: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else if (online != null && !online.isEmpty()) {
                    try {
                        OpenUtils.browseLink(online);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(application,
                                "Error opening the file: " + e1.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    }
                }
            }
        }
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
        try {
            updateTable(lastSelectedDivision);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        lastSelectedDivision = node.getDbObject();
        try {
            application.clearSearch();
            updateTable(lastSelectedDivision);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    //
    // Table selction changed
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = itemTable.getSelectedRow();
            if (row >= 0) {
                selectedItem = getItemAt(itemTable.getSelectedRow());
            }
        }
    }
}
