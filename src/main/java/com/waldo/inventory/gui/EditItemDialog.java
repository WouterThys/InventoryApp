package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.statics.ItemCategories;
import com.waldo.inventory.Utils.validators.NotEmptyValidator;
import com.waldo.inventory.classes.Item;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import static com.waldo.inventory.Utils.PanelUtils.createButtonConstraints;
import static com.waldo.inventory.Utils.PanelUtils.createFieldConstraints;
import static com.waldo.inventory.Utils.PanelUtils.createLabelConstraints;

public class EditItemDialog extends JPanel {

    private static JDialog dialog;
    private static Item newItem;

    private JTextField idTextField;
    private JTextField nameTextField;
    private JTextArea descriptionTextArea;
    private JFormattedTextField priceTextField;
    private JComboBox<String> categoryComboBox;

    private JButton cancelButton;
    private JButton createButton;

    public static Item showDialog(JFrame parent) {
        dialog = new JDialog(parent, "Create new Item", true);
        dialog.getContentPane().add(new EditItemDialog());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(400,500);
        dialog.setResizable(false);
        dialog.setVisible(true);
        return newItem;
    }

    public static Item showDialog(JFrame parent, Item item) {
        dialog = new JDialog(parent, "Create new Item", true);
        dialog.getContentPane().add(new EditItemDialog(item));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(400,500);
        dialog.setResizable(false);
        dialog.setVisible(true);
        return newItem;
    }

    public EditItemDialog(Item item) {
        super(new GridBagLayout());
        newItem = item;
        initComponents();
        initLayouts();
        updateValues();
    }

    public EditItemDialog() {
        this(new Item());
    }

    private void updateValues() {
        idTextField.setText(String.valueOf(newItem.getId()));
        nameTextField.setText(newItem.getName());
        descriptionTextArea.setText(newItem.getDescription());
        priceTextField.setText(String.valueOf(newItem.getPrice()));
        categoryComboBox.setSelectedIndex(newItem.getCategory());
    }

    private void initComponents() {
        idTextField = new JTextField(String.valueOf(newItem.getId()));
        idTextField.setEditable(false);

        nameTextField = PanelUtils.getHintTextField("Component name");
        nameTextField.setInputVerifier(new NotEmptyValidator(nameTextField));
        descriptionTextArea = PanelUtils.getHintTextArea("Component description");
        descriptionTextArea.setInputVerifier(new NotEmptyValidator(descriptionTextArea));

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press
        priceTextField = PanelUtils.getHintFormattedTextField("Price", formatter);

        categoryComboBox = new JComboBox<>(ItemCategories.getItemCategoriesStringArray());
        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                if (newItem != null) {
                    newItem.setCategory(cb.getSelectedIndex());
                }
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newItem = null;
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create item
                newItem.setName(nameTextField.getText());
                newItem.setDescription(descriptionTextArea.getText());
                newItem.setPrice(Double.valueOf(priceTextField.getText()));
                newItem.setCategory(categoryComboBox.getSelectedIndex());

                // Close dialog
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
    }

    private void initLayouts() {
        // Id
        add(new JLabel("Database ID: "), createLabelConstraints(0,0));
        add(idTextField, createFieldConstraints(1,0));

        // Name
        add(new JLabel("Name: "), createLabelConstraints(0,1));
        add(nameTextField, createFieldConstraints(1,1));

        // Description
        add(new JLabel("Description: "), createLabelConstraints(0,2));
        GridBagConstraints constraints = createFieldConstraints(1,2);
        constraints.weighty = 1;
        add(descriptionTextArea, constraints);

        // Price
        add(new JLabel("Price: "), createLabelConstraints(0,3));
        add(priceTextField, createFieldConstraints(1,3));

        // Category
        add(new JLabel("Category: "), createLabelConstraints(0,4));
        add(categoryComboBox, createFieldConstraints(1,4));

        // Buttons
        add(cancelButton, createButtonConstraints(0,5));
        add(createButton, createButtonConstraints(1,5));
    }
}
