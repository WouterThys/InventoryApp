package com.waldo.inventory.gui;

import com.waldo.inventory.classes.Item;
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

import static com.waldo.inventory.database.DbManager.dbInstance;

public class Application extends JFrame {

    private JTable itemTable;
    private ItemListAdapter itemListAdapter;

    private Item selectedItem;

    public Application() {
        initComponents();
    }

    void refreshItemList() {
        itemListAdapter.fireTableDataChanged();
    }

    void createNewItem() throws SQLException {
        Item item = EditItemDialog.showDialog(this);
        if (item != null && item.getId() >= 0) {
            try {
                item.save(dbInstance());
                selectedItem = item;
                refreshItemList();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving Item", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void editItem() throws SQLException {
        if (selectedItem != null) {
            selectedItem = EditItemDialog.showDialog(this, selectedItem);
            if (selectedItem != null) {
                try {
                    selectedItem.save(dbInstance());
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
                selectedItem.save(dbInstance());
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
                    refreshItemList();
                } catch (final SQLException e) {
                    JOptionPane.showMessageDialog(this, "Failed to delete the selected contact", "Delete", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setSelectedItem(null);
                    refreshItemList();
                }
            }
        }
    }

    private void initComponents() {
        TopToolBar ttb = TopToolBar.getToolbar(this);
        ttb.init();
        add(ttb, BorderLayout.PAGE_START);
        add(createTablePane(), BorderLayout.CENTER);
        add(new QueryPanel(this), BorderLayout.SOUTH);
        setJMenuBar(new MenuBar(this));
    }

    private JComponent createTablePane() {
        List<Item> items = new ArrayList<>();
        try {
            items = dbInstance().getItems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        itemListAdapter = new ItemListAdapter();
        itemTable = new JTable(itemListAdapter);
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    itemListAdapter.tableClicked(Application.this, itemTable, e);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        itemTable.setColumnSelectionAllowed(false);
        itemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    int row = itemTable.getSelectedRow();
                    if (row >= 0) {
                        Item selected = null;
                        try {
                            selected = itemListAdapter.getItemAt(itemTable.getSelectedRow());
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                        setSelectedItem(selected);
                    }
                }
            }
        });

        return new JScrollPane(itemTable); // Put in ScrollPane!!
    }

    private void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

}
