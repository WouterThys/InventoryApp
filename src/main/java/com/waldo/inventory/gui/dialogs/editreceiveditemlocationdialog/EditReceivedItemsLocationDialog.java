package com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILocationMapPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

public class EditReceivedItemsLocationDialog extends EditReceivedItemsLocationDialogLayout {


    private List<ItemLocationUpdate> itemLocationUpdates;

    public EditReceivedItemsLocationDialog(Application application, String title, List<Item> itemsWithoutLocation) {
        super(application, title);

        this.itemsWithoutLocation = itemsWithoutLocation;
        createItemLocationUpdateList(itemsWithoutLocation);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    private void createItemLocationUpdateList(List<Item> itemsWithoutLocation) {
        if (itemsWithoutLocation != null) {
            itemLocationUpdates = new ArrayList<>();
            for (Item item : itemsWithoutLocation) {
               ItemLocationUpdate ilu = new ItemLocationUpdate(item.getId());
               itemLocationUpdates.add(ilu);
            }
        }
    }

    private void updateLocation(long itemId, Location newLocation) {
        for (ItemLocationUpdate ilu : itemLocationUpdates) {
            if (ilu.getItemId() == itemId) {
                ilu.setNewLocation(newLocation);
                updateSaveButton();
                return;
            }
        }
    }

    private Location getOldLocation(long itemId) {
        for (ItemLocationUpdate ilu : itemLocationUpdates) {
            if (ilu.getItemId() == itemId) {
                return ilu.getOldLocation();
            }
        }
        return null;
    }

    private Location getNewLocation(long itemId) {
        for (ItemLocationUpdate ilu : itemLocationUpdates) {
            if (ilu.getItemId() == itemId) {
                return ilu.getNewLocation();
            }
        }
        return null;
    }

    private boolean locationsChanged() {
        for (ItemLocationUpdate ilu : itemLocationUpdates) {
            if (ilu.locationChanged()) {
                return true;
            }
        }
        return false;
    }

    private void updateSaveButton() {
        getButtonNeutral().setEnabled(locationsChanged());
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (locationsChanged()) {
            int res = JOptionPane.showConfirmDialog(
                    EditReceivedItemsLocationDialog.this,
                    "Locations are changed, do you want to save items?",
                    "Items changed",
                    JOptionPane.YES_NO_OPTION
            );
            if (res == JOptionPane.YES_OPTION) {
                onNeutral();
            }
        }
        super.onOK();
    }

    @Override
    protected void onNeutral() {
        if (locationsChanged()) {
            for (ItemLocationUpdate ilu : itemLocationUpdates) {
                if (ilu.locationChanged()) {
                    Item item = getItem(ilu.getItemId());
                    Location location = ilu.getNewLocation();
                    if (item != null && location != null) {
                        item.setLocationId(location.getId());
                        item.save();

                        ilu.setOldLocation(location);
                    }
                }
            }
        }
        updateSaveButton();
    }

    //
    // List selection
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedItem = itemList.getSelectedValue();

            Location location = getNewLocation(selectedItem.getId());
            if (location == null || location.isUnknown()) {
                locationTypeCb.setSelectedItem(null);
                locationMapPanel.clear();
            } else {
                locationTypeCb.setSelectedItem(location.getLocationType());
            }
        }
    }

    //
    // Location clicked
    //
    @Override
    public void onLocationClicked(ActionEvent e, Location location) {
        if (selectedItem != null) {
            locationMapPanel.clearHighlights();
            locationMapPanel.setHighlighted(location, ILocationMapPanel.GREEN);
            updateLocation(selectedItem.getId(), location);
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

                    if (selectedItem != null) {
                        Location location = getNewLocation(selectedItem.getId());
                        if (location != null) {
                            locationMapPanel.setHighlighted(location, ILocationMapPanel.GREEN);
                        }
                    }

                    pack();
                }
            });
        }
    }


    private static class ItemLocationUpdate {

        private final long itemId;
        private Location oldLocation;
        private Location newLocation;

        ItemLocationUpdate(long itemId) {
            this.itemId = itemId;
        }

        public long getItemId() {
            return itemId;
        }

        public Location getOldLocation() {
            return oldLocation;
        }

        public Location getNewLocation() {
            return newLocation;
        }

        public void setOldLocation(Location oldLocation) {
            if (oldLocation == null || !oldLocation.isUnknown()) {
                this.oldLocation = oldLocation;
            }
        }

        public void setNewLocation(Location newLocation) {
            if (newLocation == null || !newLocation.isUnknown()) {
                this.newLocation = newLocation;
            }
        }

        public boolean locationChanged() {
            return (oldLocation != null || newLocation != null) &&
                    (oldLocation != null && newLocation == null || oldLocation == null || oldLocation.getId() != newLocation.getId());
        }
    }
}