package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.DbObject.DbObjectNameComparator;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.DistributorPartLink;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.managers.SearchManager.sm;

public class EditItemOrderPanel extends JPanel implements GuiInterface {

    private Item newItem;
    private DistributorPartLink distributorPartLink;

    private IComboBox<Distributor> distributorCb;
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
                    distributorPartLink = sm().findPartNumber(d.getId(), newItem.getId());
                    if (distributorPartLink == null) {
                        distributorPartLink = new DistributorPartLink();
                    }
                    distributorPartLink.setItemId(newItem.getId());
                    distributorPartLink.setDistributorId(d.getId());
                    distributorPartLink.setItemRef(ref);
                    distributorPartLink.save();
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
            DistributorPartLink dp = sm().findPartNumber(d.getId(), newItem.getId());
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
        distributorCb.updateList();
        distributorCb.setSelectedItem(newItem.getManufacturer());
    }

    @Override
    public void initializeComponents() {
        distributorCb = new IComboBox<>(db().getDistributors(), new DbObjectNameComparator<>(), true);
        distributorCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                distributorPartLink = sm().findPartNumber(((Distributor)e.getItem()).getId(), newItem.getId());
                if (distributorPartLink != null) {
                    itemRefField.setText(distributorPartLink.getItemRef());
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

        JPanel panel = new JPanel(new GridBagLayout());
        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(panel);
        gbc.addLineVertical("Distributor: ", PanelUtils.createComboBoxWithButton(distributorCb, createDistributorListener()));
        gbc.addLineVertical("Reference: ", itemRefField);

        // Add to panel
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(panel, BorderLayout.NORTH);
    }

    @Override
    public void updateComponents(Object... object) {
        if (distributorCb.getModel().getSize() > 0) {
            distributorCb.setSelectedIndex(0);
        }
    }

    public ITextField getItemRefField() {
        return itemRefField;
    }
}
