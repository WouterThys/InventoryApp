package com.waldo.inventory.gui.dialogs.edititemlocationdialog;

import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class EditItemLocation extends EditItemLocationLayout {


    public EditItemLocation(Window parent, String title, Location location) {
        super(parent, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(location);

    }

    public Location getItemLocation() {
        return location;
    }

    @Override
    protected void onCancel() {
        if (originalLocation != null) {
            location = originalLocation.createCopy();
            location.setCanBeSaved(true);
        }
        super.onCancel();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && !isUpdating()) {
            SwingUtilities.invokeLater(() -> {
                LocationType type = (LocationType) locationTypeCb.getSelectedItem();
                if (type != null && (location == null || location.getLocationTypeId() != type.getId())) {
                    location = null;
                    locationMapPanel.setLocations(type.getLocations());
                    pack();
                }
            });
        }
    }
}