package com.waldo.inventory.gui.dialogs.edititemlocationdialog;

import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class EditItemLocationDialog extends EditItemLocationDialogLayout {


    public EditItemLocationDialog(Application application, String title, Location location) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(location);

    }

    public Location getItemLocation() {
        return location;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && !application.isUpdating()) {
            SwingUtilities.invokeLater(() -> {
                LocationType type = (LocationType) locationTypeCb.getSelectedItem();
                if (type != null && (location == null || location.getLocationTypeId() != type.getId())) {
                    location = null;
                    locationMapPanel.updateComponents(type);
                    pack();
                }
            });
        }
    }
}