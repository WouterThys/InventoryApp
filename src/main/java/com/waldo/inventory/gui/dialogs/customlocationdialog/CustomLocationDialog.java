package com.waldo.inventory.gui.dialogs.customlocationdialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomLocationDialog extends CustomLocationDialogLayout {

    private boolean canClose = true;

    public CustomLocationDialog(Application application, String title, LocationType locationType) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(locationType);

    }

    private void showSaveDialog() {
        String msg = "Locations are edited, do you want to save?";
        if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            saveLocations();
        }

        canClose = true;
    }

    private void saveLocations() {
        // Delete
        List<Location> oldLocations = new ArrayList<>(locationType.getLocations());
        for (Location location : oldLocations) {
            if (!isInLocationList(location.getRow(), location.getCol())) {
                location.delete();
            }
        }

        // Insert or update
        for (Location newLocation : newLocationList) {
            Location oldLocation = SearchManager.sm().findLocation(locationType.getId(), newLocation.getRow(), newLocation.getCol());

            if (oldLocation != null) {
                oldLocation.setName(newLocation.getName());
                oldLocation.setAlias(newLocation.getAlias());
            } else {
                oldLocation = newLocation.createCopy();
                oldLocation.setCanBeSaved(true);
            }
            oldLocation.save();
        }
        locationType.updateLocations();
    }

    private List<Location> convertInput(String input) {
        List<Location> buttonList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String rows[] = input.split("\\r?\\n");

            int r = 0;
            int c = 0;

            for (String row : rows) {
                String cols[] = row.split(",");
                for (String col : cols) {
                    Location loc = createLocation(col, r, c);
                    buttonList.add(loc);
                    c++;
                }
                c = 0;
                r++;
            }
        }

        return buttonList;
    }

    private Location createLocation(String name, int r, int c) {
        Location location = SearchManager.sm().findLocation(locationType.getId(), r, c);
        if (location == null) {
            location = new Location();
        } else {
            location = location.createCopy();
        }
        if (name != null && !name.isEmpty()) {
            location.setName(name);
        } else {
            location.setName("(" + Statics.Alphabet[r] + "," + c + ")");
        }
        location.setCol(c);
        location.setRow(r);
        location.setLocationTypeId(locationType.getId());

        return location;
    }

    private boolean compareLocations() {
        List<Location> originalLocationList = locationType.getLocations();
        if (newLocationList.size() != originalLocationList.size()) {
            return false;
        } else {

            newLocationList.sort(new LocationSort());
            originalLocationList.sort(new LocationSort());

            for (int i = 0; i < newLocationList.size(); i++) {
                if (!newLocationList.get(i).equals(originalLocationList.get(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    private static class LocationSort implements Comparator<Location> {
        @Override
        public int compare(Location o1, Location o2) {
            if (o1 == null && o2 != null) return -1;
            if (o1 == null) return 0;
            if (o2 == null) return 1;

            if (o1.getRow() < o1.getRow()) return 1;
            if (o1.getRow() > o2.getRow()) return -1;
            if (o1.getRow() == o2.getRow()) {
                if (o1.getCol() < o1.getCol()) return 1;
                if (o1.getCol() > o2.getCol()) return -1;
                if (o1.getCol() == o2.getCol()) {
                    return 0;
                }
            }
            return 0;
        }
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (!compareLocations()) {
            canClose = false;
            showSaveDialog();
        }

        if (canClose) {
            super.onOK();
        }
    }

    @Override
    protected void onNeutral() {
        saveLocations();
        getButtonNeutral().setEnabled(false);
    }

    //
    // Location map button click
    //
    @Override
    public void onClick(ActionEvent e,Location location) {
        selectedLocationButton = locationMapPanel.findButton(location.getRow(), location.getCol());
        updateEnabledComponents();
        setButtonDetails(location);
    }

    //
    // Button click
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(convertBtn)) {
            String input = inputTa.getText();
            selectedLocationButton = null;
            newLocationList = convertInput(input);
            locationMapPanel.setLocations(newLocationList);
        } else if (e.getSource().equals(setNameBtn)) {
            if (selectedLocationButton != null) {
                selectedLocationButton.getTheLocation().setName(nameTf.getText());
                locationMapPanel.updateButtons();
            }
        } else if (e.getSource().equals(setAliasBtn)) {
            if (selectedLocationButton != null) {
                selectedLocationButton.getTheLocation().setAlias(aliasTf.getText());
                locationMapPanel.updateButtons();
            }
        }
        getButtonNeutral().setEnabled(!compareLocations());
        updateEnabledComponents();
    }
}