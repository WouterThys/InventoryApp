package com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILocationMapPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

public class EditReceivedItemsLocationDialog extends EditReceivedItemsLocationDialogLayout {


    public EditReceivedItemsLocationDialog(Application application, String title, List<Item> itemsWithoutLocation) {
        super(application, title);

        this.itemsWithoutLocation = itemsWithoutLocation;

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    //
    // List selection
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedItem = itemList.getSelectedValue();

            if (selectedItem.getLocationId() <= DbObject.UNKNOWN_ID) {
                locationTypeCb.setSelectedItem(null);
            } else {
                locationTypeCb.setSelectedItem(selectedItem.getLocation());
            }
        }
    }

    //
    // Combo box value changed
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && !application.isUpdating()) {
            SwingUtilities.invokeLater(() -> {
                LocationType type = (LocationType) locationTypeCb.getSelectedItem();
                if (type != null) {
                    locationMapPanel.setLocations(type.getLocations());

                    if (selectedItem != null && selectedItem.getLocationId() > DbObject.UNKNOWN_ID) {
                        locationMapPanel.setHighlighted(selectedItem.getLocation(), ILocationMapPanel.GREEN);
                    }

                    pack();
                }
            });
        }
    }
}