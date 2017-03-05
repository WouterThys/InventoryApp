package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.statics.ItemCategories;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.database.ItemDbManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.Utils.PanelUtils.*;

public class Application extends JFrame {

    private JTable itemTable;
    private ItemListAdapter itemListAdapter;
    private JTextField nameTextField;
    private JTextField idTextField;
    private JTextArea descriptionTextArea;
    private JTextField priceTextField;
    private JTextField categoryTextField;
    private JTextField productTextField;
    private JTextField typeTextField;

    private Item selectedItem;

    public Application() {
        initComponents();
    }

    void refreshItemList() {
        // Do this asynchronous
        new AsyncRefreshList().execute();
    }

    void createNewItem() {
        Item item = EditItemDialog.showDialog(this);
        if (item != null) {
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
                    JOptionPane.showMessageDialog(this, "Error saving Item", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    void saveItem() {
        if (selectedItem != null) {
            selectedItem.setName(nameTextField.getText());
            selectedItem.setDescription(descriptionTextArea.getText());
            selectedItem.setPrice(Integer.valueOf(priceTextField.getText()));
            //selectedItem.setCategory();
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

    private void initComponents() {
        TopToolBar ttb = TopToolBar.getToolbar(this);
        ttb.init();
        add(ttb, BorderLayout.PAGE_START);
        add(createTablePane(), BorderLayout.WEST);
        add(createEditor(), BorderLayout.CENTER);
    }

    private JComponent createTablePane() {
        List<Item> items = new ArrayList<>();
        try {
            items = ItemDbManager.getInstance().getItems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        itemListAdapter = new ItemListAdapter(items);
        itemTable = new JTable(itemListAdapter);

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
        if (selectedItem == null) {
            idTextField.setText("");
            nameTextField.setText("");
            descriptionTextArea.setText("");
            priceTextField.setText("");
            categoryTextField.setText("");
            productTextField.setText("");
            typeTextField.setText("");
        } else {
            idTextField.setText(String.valueOf(selectedItem.getId()));
            nameTextField.setText(selectedItem.getName());
            descriptionTextArea.setText(selectedItem.getDescription());
            priceTextField.setText(String.valueOf(selectedItem.getPrice()));
            categoryTextField.setText(ItemCategories.getItemCategoryAsString(selectedItem.getCategory()));
            productTextField.setText(String.valueOf(selectedItem.getProduct()));
            typeTextField.setText(String.valueOf(selectedItem.getProduct()));
        }
    }

    private JComponent createEditor() {
        final JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints;

        // Id
        panel.add(new JLabel("Id"), createLabelConstraints(0,0));
        idTextField = new JTextField();
        idTextField.setEditable(false);
        panel.add(idTextField, createFieldConstraints(1,0));

        // Name
        panel.add(new JLabel("Name"), createLabelConstraints(0,1));
        nameTextField = new JTextField();
        panel.add(nameTextField, createFieldConstraints(1,1));

        // Description
        panel.add(new JLabel("Contacts"), createLabelConstraints(0,2));
        constraints = createFieldConstraints(1,2);
        constraints.weighty = 1;
        descriptionTextArea = new JTextArea();
        panel.add(new JScrollPane(descriptionTextArea), constraints);

        // Price
        panel.add(new JLabel("Price"), createLabelConstraints(0,3));
        priceTextField = new JTextField();
        panel.add(priceTextField, createFieldConstraints(1,3));

        // Category
        panel.add(new JLabel("Category"), createLabelConstraints(0,4));
        categoryTextField = new JTextField();
        panel.add(categoryTextField, createFieldConstraints(1,4));

        // Product
        panel.add(new JLabel("Product"), createLabelConstraints(0,5));
        productTextField = new JTextField();
        panel.add(productTextField, createFieldConstraints(1,5));

        // Type
        panel.add(new JLabel("Type"), createLabelConstraints(0,6));
        typeTextField = new JTextField();
        panel.add(typeTextField, createFieldConstraints(1,6));

        return panel;
    }

    private class AsyncRefreshList extends SwingWorker<Void, Item> {

        AsyncRefreshList() {
            itemListAdapter.removeAllItems();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        @Override
        protected void process(List<Item> chunks) {
            for(Item i : chunks) {
                itemListAdapter.add(i);
            }
        }

        @Override
        protected void done() {
            setCursor(Cursor.getDefaultCursor());
        }

        @Override
        protected Void doInBackground() throws Exception {
            try {
                List<Item> items = ItemDbManager.getInstance().getItems();
                for (Item i : items) {
                    publish(i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
