package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;

import javax.swing.*;
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

    private void initActions() {
        initMouseClicked();
        initItemSelectedListener();
        initCategorySelectedListener();
    }

    private void initItemSelectedListener() {
        itemTable.getSelectionModel().addListSelectionListener( e -> {
            if (!e.getValueIsAdjusting()) {

//                    int row = itemTable.getSelectedRow();
//                    if (row >= 0) {
//                        Item selected = null;
//                        try {
//                            selected = getItemAt(itemTable.getSelectedRow());
//                        } catch (SQLException e1) {
//                            e1.printStackTrace();
//                        }
//                        selectedItem = selected;
//                    }

            }
        });
    }

    private void initCategorySelectedListener() {
        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JList list  = (JList)e.getSource();
                selectedCategory = (Category) list.getSelectedValue();
                if (selectedCategory != null && selectedCategory.getId() != DbObject.UNKNOWN_ID) {
                    // set selected item
                } else {
                    // clear active item
                }
            }
        });
    }

    private void initMouseClicked() {
        mouseClicked = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                if (e.getClickCount() == 2) {
                    ActionListener a = application.updateItemAction;
                    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,  null));
                }
                if (e.getClickCount() == 1) {
                    try {
                        dataSheetColumnClicked(table.columnAtPoint(e.getPoint()), table.rowAtPoint(e.getPoint()));
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
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
                updateItems(item);
            }

            @Override
            public void onUpdated(Item item) {
                selectedItem = item;
                updateItems(item);
            }

            @Override
            public void onDeleted(Item item) {
                selectedItem = null;
                updateItems(item);
            }
        };
    }

    private void updateItems(Item item) {
        //tableModel.;
    }

    private void setCategoriesChangedListener() {
        categoriesChanged = new DbObjectChangedListener<Category>() {
            @Override
            public void onAdded(Category object) {
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
}
