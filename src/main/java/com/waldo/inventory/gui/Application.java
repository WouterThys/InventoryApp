package com.waldo.inventory.gui;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.database.ItemDbManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Application extends JFrame {

    private JTable itemTable;
    private ItemListAdapter itemListAdapter;
    private JTextField nameTextField;
    private JTextField idTextField;
    private JTextArea descriptionTextArea;
    private JTextField priceTextField;

    private Item selectedItem;

    public Application() {
        initComponents();
    }

    void refreshItemList() {
        // Do this asynchronous
        new AsyncRefreshList().execute();
    }

    void createNewItem() {
        Item item = NewItemDialog.showDialog(this);
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

    void saveItem() {
        if (selectedItem != null) {
            selectedItem.setName(nameTextField.getText());
            selectedItem.setDescription(descriptionTextArea.getText());
            //selectedItem.setPrice(Integer.valueOf(priceTextField.getText()));
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
        } else {
            idTextField.setText(String.valueOf(selectedItem.getId()));
            nameTextField.setText(selectedItem.getName());
            descriptionTextArea.setText(selectedItem.getDescription());
        }
    }

    private JComponent createEditor() {
        final JPanel panel = new JPanel(new GridBagLayout());

        // Id
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(2,2,2,2);
        panel.add(new JLabel("Id"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(2,2,2,2);
        constraints.fill = GridBagConstraints.BOTH;
        idTextField = new JTextField();
        idTextField.setEditable(false);
        panel.add(idTextField, constraints);

        // Name
        constraints = new GridBagConstraints();
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(2,2,2,2);
        panel.add(new JLabel("Name"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(2,2,2,2);
        constraints.fill = GridBagConstraints.BOTH;
        nameTextField = new JTextField();
        panel.add(nameTextField, constraints);

        // Contacts
        constraints = new GridBagConstraints();
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2,2,2,2);
        panel.add(new JLabel("Contacts"), constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(2,2,2,2);
        constraints.fill = GridBagConstraints.BOTH;
        descriptionTextArea = new JTextArea();
        panel.add(new JScrollPane(descriptionTextArea), constraints);

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
