package com.waldo.inventory.gui;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.database.TableChangedListener;
import com.waldo.inventory.gui.adapters.ItemListAdapter;
import com.waldo.inventory.gui.dialogs.EditItemDialog;
import com.waldo.inventory.gui.panels.QueryPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.Utils.PanelUtils.*;
import static com.waldo.inventory.database.DbManager.dbInstance;

public class Application extends JFrame implements TableChangedListener {

    private JTable itemTable;
    private ItemListAdapter itemListAdapter;

    // Cached objects from database
    private List<Category> categoryList;
    private List<Product> productList;
    private List<com.waldo.inventory.classes.Type> typeList;

    private Item selectedItem;

    public Application() {
        initObjectsFromDb();
        initComponents();
    }

    void refreshItemList() {
        dbInstance().getItemsAsync(itemListAdapter);
    }

    void createNewItem() {
        Item item = EditItemDialog.showDialog(this);
        if (item != null && item.getId() >= 0) {
            try {
                item.save();
                selectedItem = item;
                refreshItemList();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving Item", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void editItem() {
        if (selectedItem != null) {
            selectedItem = EditItemDialog.showDialog(this, selectedItem);
            if (selectedItem != null) {
                try {
                    selectedItem.save();
                    refreshItemList();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error saving Item: "+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    void saveItem() {
        if (selectedItem != null) {
            try {
                selectedItem.save();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                refreshItemList();
            }
        }
    }

    void deleteItem() {
        if (selectedItem != null) {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Delete " + selectedItem + "?", "Delete", JOptionPane.YES_NO_OPTION)) {
                try {
                    selectedItem.delete();
                } catch (final SQLException e) {
                    JOptionPane.showMessageDialog(this, "Failed to delete the selected contact", "Delete", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setSelectedItem(null);
                    refreshItemList();
                }
            }
        }
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public List<com.waldo.inventory.classes.Type> getTypeList() {
        return typeList;
    }

    private void initObjectsFromDb() {
        categoryList = new ArrayList<>();
        dbInstance().getCategoriesAsync(categoryList);

        productList = new ArrayList<>();
        dbInstance().getProductsAsync(productList);

        typeList = new ArrayList<>();
        dbInstance().getTypesAsync(typeList);
    }

    private void initComponents() {
        TopToolBar ttb = TopToolBar.getToolbar(this);
        ttb.init();
        add(ttb, BorderLayout.PAGE_START);
        add(createTablePane(), BorderLayout.CENTER);
        add(new QueryPanel(this), BorderLayout.SOUTH);
        //add(createEditor(), BorderLayout.CENTER);
    }

    private JComponent createTablePane() {
        List<Item> items = new ArrayList<>();
        try {
            items = dbInstance().getItems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        itemListAdapter = new ItemListAdapter(items);
        itemTable = new JTable(itemListAdapter);
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                itemListAdapter.tableClicked(Application.this, itemTable, e);
            }
        });

        itemTable.setColumnSelectionAllowed(false);
        itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    int row = itemTable.getSelectedRow();
                    if (row >= 0) {
                        Item selected = itemListAdapter.getItemAt(itemTable.getSelectedRow());
                        setSelectedItem(selected);
                    }
                }
            }
        });

        return new JScrollPane(itemTable); // Put in ScrollPane!!
    }

    /**
     * Select a contact
     * @param selectedItem: contact that was selected from the list.
     */
    private void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

    public Category findCategoryById(long id) {
        for (Category c : categoryList) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Product findProductById(long id) {
        for(Product p : productList) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public com.waldo.inventory.classes.Type findTypeById(long id) {
        for (com.waldo.inventory.classes.Type t : typeList) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void tableChangedListener(String tableName, long id) {
        switch (tableName) {
            case Item.TABLE_NAME:
                break;

            case Category.TABLE_NAME:
                dbInstance().getCategoriesAsync(categoryList);
                break;

            case Product.TABLE_NAME:
                dbInstance().getProductsAsync(productList);
                break;

            case com.waldo.inventory.classes.Type.TABLE_NAME:
                dbInstance().getTypesAsync(typeList);
                break;

            default:
                break;
        }
    }
}
