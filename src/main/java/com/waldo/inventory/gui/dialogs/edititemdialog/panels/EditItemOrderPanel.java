package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.PartNumber;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

import static com.waldo.inventory.database.DbManager.db;

public class EditItemOrderPanel extends JPanel implements GuiInterface {

    private Item newItem;
    private PartNumber partNumber;

    private JComboBox<Distributor> distributorCb;
    private DefaultComboBoxModel<Distributor> distributorCbModel;
    private ITextField itemRefField;
    private JButton saveButton;

    public EditItemOrderPanel(Item newItem) {
        this.newItem = newItem;
    }

    private void setPartNumber() {
        String ref = itemRefField.getText();
        if (ref != null && !ref.isEmpty()) {
            ref = ref.trim();
            try {
                Distributor d = (Distributor) distributorCb.getSelectedItem();
                if (newItem.getId() < 0) {
                    newItem.save();
                }
                partNumber = db().findPartNumber(d.getId(), newItem.getId());
                if (partNumber == null) {
                    partNumber = new PartNumber();
                }
                partNumber.setItemId(newItem.getId());
                partNumber.setDistributorId(d.getId());
                partNumber.setItemRef(ref);
                partNumber.save();
            } finally {

            }
        } else {
            itemRefField.setError("No reference");
        }
    }

    @Override
    public void initializeComponents() {
        distributorCbModel = new DefaultComboBoxModel<>();
        distributorCb = new JComboBox<>(distributorCbModel);
        distributorCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Distributor d = (Distributor) e.getItem();
                if (d != null) {
                    // Save item if not saved yet
                    if (newItem.getId() > 0) {
                        // Find ref
                        partNumber = db().findPartNumber(d.getId(), newItem.getId());
                        if (partNumber != null) {
                            itemRefField.setText(partNumber.getItemRef());
                        } else {
                            itemRefField.setText("");
                        }
                    }
                }
            }
        });

        itemRefField = new ITextField("Distributor Part No");
        saveButton = new JButton("Save");
        saveButton.setHorizontalAlignment(SwingConstants.RIGHT);
        saveButton.addActionListener(e -> setPartNumber());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        ILabel distributorLabel = new ILabel("Distributor: ", ILabel.LEFT);
        ILabel itemRefLabel = new ILabel("Reference: ", ILabel.LEFT);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Distributor
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(distributorLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(distributorCb, gbc);

        // Reference
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(itemRefLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(itemRefField, gbc);

        // Button
        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 4; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(saveButton, gbc);

        // Add to panel
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(panel, BorderLayout.NORTH);
    }

    @Override
    public void updateComponents(Object object) {
        distributorCbModel.removeAllElements();
        for (Distributor d : db().getDistributors()) {
            distributorCbModel.addElement(d);
        }
    }
}
