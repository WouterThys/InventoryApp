package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.DistributorPart;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.database.SearchManager.sm;

public class EditItemOrderPanel extends JPanel implements GuiInterface {

    private Item newItem;
    private DistributorPart distributorPart;

    private IComboBox<Distributor> distributorCb;
    private DefaultComboBoxModel<Distributor> distributorCbModel;
    private ITextField itemRefField;

    // Listener
    private Application application;
    private IEditedListener editedListener;

    public EditItemOrderPanel(Application application, Item newItem, IEditedListener listener) {
        this.application = application;
        this.newItem = newItem;
        this.editedListener = listener;
    }

    public void setPartNumber() {
        String ref = itemRefField.getText();
        if (ref != null && !ref.isEmpty()) {
            ref = ref.trim();
            try {
                Distributor d = (Distributor) distributorCb.getSelectedItem();
                if (newItem.getId() < 0) {
                    JOptionPane.showMessageDialog(
                            EditItemOrderPanel.this,
                            "Save item first..",
                            "Error saving",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    distributorPart = sm().findPartNumber(d.getId(), newItem.getId());
                    if (distributorPart == null) {
                        distributorPart = new DistributorPart();
                    }
                    distributorPart.setItemId(newItem.getId());
                    distributorPart.setDistributorId(d.getId());
                    distributorPart.setItemRef(ref);
                    distributorPart.save();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            itemRefField.setError("No reference");
        }
    }

    public boolean checkChange() {
        boolean result = false;
        String ref = itemRefField.getText();
        Distributor d = (Distributor) distributorCb.getSelectedItem();
        if (d != null && !d.isUnknown() && newItem.getId() > DbObject.UNKNOWN_ID) {
            DistributorPart dp = sm().findPartNumber(d.getId(), newItem.getId());
            result = !ref.isEmpty() && ((dp == null) || (!dp.getItemRef().equals(ref)));
        }

        return result;
    }

    private ActionListener createDistributorListener() {
        return e -> {
            ManufacturersDialog manufacturersDialog = new ManufacturersDialog(application, "Manufacturers");
            if (manufacturersDialog.showDialog() == IDialog.OK) {
                updateManufacturerCombobox();
            }
        };
    }

    private void updateManufacturerCombobox() {
        distributorCbModel.removeAllElements();
        for (Distributor d : db().getDistributors()) {
            distributorCbModel.addElement(d);
        }

        distributorCb.setSelectedItem(newItem.getManufacturer());
    }

    @Override
    public void initializeComponents() {
        distributorCbModel = new DefaultComboBoxModel<>();
        distributorCb = new IComboBox<>(distributorCbModel);
        distributorCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                distributorPart = sm().findPartNumber(((Distributor)e.getItem()).getId(), newItem.getId());
                if (distributorPart != null) {
                    itemRefField.setText(distributorPart.getItemRef());
                } else {
                    itemRefField.setText("");
                }
            }
        });

        itemRefField = new ITextField("Distributor reference");
        itemRefField.addEditedListener(editedListener, "");
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
        panel.add(PanelUtils.createComboBoxWithButton(distributorCb, createDistributorListener()), gbc);

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
        if (distributorCbModel.getSize() > 0) {
            distributorCb.setSelectedIndex(0);
        }
    }

    public ITextField getItemRefField() {
        return itemRefField;
    }
}
