package com.waldo.inventory.gui;

import com.waldo.inventory.Utils.PanelUtils;
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

public class NewItemDialog extends JPanel {

    private static JDialog dialog;

    private JTextField idTextField;
    private JTextField nameTextField;
    private JTextArea descriptionTextArea;
    private JFormattedTextField priceTextField;
    private JButton cancelButton;
    private JButton createButton;

    public static void showDialog(JFrame parent) {
        dialog = new JDialog(parent, "Create new Item", true);
        dialog.getContentPane().add(new NewItemDialog());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(400,500);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private Item newItem;

    public NewItemDialog() {
        super(new GridBagLayout());
        newItem = new Item();
        initComponents();
        initLayouts();
    }

    private void initComponents() {
        idTextField = new JTextField(String.valueOf(newItem.getId()));
        idTextField.setEditable(false);

        nameTextField = PanelUtils.getHintTextField("Component name");
        nameTextField.setInputVerifier(new NotEmptyValidator(nameTextField));
        descriptionTextArea = PanelUtils.getHintTextArea("Component description");

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(Double.MIN_VALUE);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true); // Commit on every key press
        priceTextField = PanelUtils.getHintFormattedTextField("Price", formatter);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Do checks

                // Create item
                newItem.setName(nameTextField.getText());
                newItem.setDescription(descriptionTextArea.getText());
                newItem.setPrice(Double.valueOf(priceTextField.getText()));
                // Return item??
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

        // Buttons
        add(cancelButton, createButtonConstraints(0,4));
        add(createButton, createButtonConstraints(1,4));
    }
}
