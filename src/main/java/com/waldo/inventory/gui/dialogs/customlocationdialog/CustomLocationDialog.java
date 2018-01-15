package com.waldo.inventory.gui.dialogs.customlocationdialog;

import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.allaliasesdialog.AllAliasesDialog;
import com.waldo.inventory.managers.SearchManager;

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
        List<Location> locationList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String rows[] = input.split("\\r?\\n");
            if (input.contains("(")) {
                locationType.setLayoutDefinition(input);
                locationList = createLocationsFromInput(rows);
            } else {
                locationList = smartCreateLocationFromInput(rows);
            }

        }

        return locationList;
    }

    private List<Location> smartCreateLocationFromInput(String[] rows) {
        List<Location> buttonList = new ArrayList<>();

        int r = 0;
        int c = 0;

        for (String row : rows) {
            String cols[] = row.split(",");
            for (String col : cols) {
                Location loc = Location.createLocation(col, r, c, locationType.getId());
                buttonList.add(loc);
                c++;
            }
            c = 0;
            r++;
        }
        return buttonList;
    }

    private List<Location> createLocationsFromInput(String[] rows) {
        List<Location> buttonList = new ArrayList<>();

        for (String row : rows) {
            try {
                row = row.replace(" ", "");
                int ndx = row.indexOf("(");
                String name = row.substring(0, ndx);

                String[] params = valueBetween(row, "(", ")").split(",");
                int c = Integer.valueOf(params[0]); // X = column
                int r = Integer.valueOf(params[1]); // Y = row

                Location loc = Location.createLocation(name, r, c, locationType.getId());
                buttonList.add(loc);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not create location for: " + row + ". Aborting...",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                buttonList.clear();
            }
        }

        return buttonList;
    }

    private String valueBetween(String s, String first, String last) {
        s = s.substring(s.indexOf(first) + 1);
        s = s.substring(0, s.indexOf(last));

        return s;
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

            if (o1.getRow() < o2.getRow()) return -1;
            if (o1.getRow() > o2.getRow()) return 1;
            if (o1.getRow() == o2.getRow()) {
                if (o1.getCol() < o2.getCol()) return -1;
                if (o1.getCol() > o2.getCol()) return 1;
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
    public void onLocationClicked(ActionEvent e, Location location) {
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
        } else if (e.getSource().equals(saveNameAction)) {
            if (selectedLocationButton != null) {
                selectedLocationButton.getTheLocation().setName(nameTf.getText());
                locationMapPanel.updateButtons();
            }
        } else if (e.getSource().equals(saveAliasAction)) {
            if (selectedLocationButton != null) {
                selectedLocationButton.getTheLocation().setAlias(aliasTf.getText());
                locationMapPanel.updateButtons();
            }
        } else if (e.getSource().equals(searchAliasAction)) {
            if (selectedLocationButton != null) {
                AllAliasesDialog dialog = new AllAliasesDialog(application, "Alias", aliasTf.getText());
                if (dialog.showDialog() == IDialog.OK) {
                    String selectedAlias = dialog.getSelectedAlias();
                    if (selectedAlias != null && !selectedAlias.isEmpty()) {
                        aliasTf.setText(selectedAlias);
                    }
                }
            }
        }
        getButtonNeutral().setEnabled(!compareLocations());
        updateEnabledComponents();
    }
}