package com.waldo.inventory.gui.dialogs.customlocationdialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILocationButton;

import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

public class CustomLocationDialog extends CustomLocationDialogLayout {


    public CustomLocationDialog(Application application, String title, LocationType locationType) {
        super(application, title);

        this.locationType = locationType;

        initializeComponents();
        initializeLayouts();
        updateComponents(locationType);

    }

    private List<ILocationButton> convertInput(String input) {
        List<ILocationButton> buttonList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String rows[] = input.split("\\r?\\n");

            int r = 0;
            int c = 0;

            for (String row : rows) {
                String cols[] = row.split(",");
                for (String col : cols) {


                    ILocationButton btn = new ILocationButton(createLocation(col, r, c));
                    locationMapPanel.addButtonActionListener(btn, r, c);
                    buttonList.add(btn);
                    c++;
                }
                c = 0;
                r++;
            }
        }

        return buttonList;
    }

    private Location createLocation(String name, int r, int c) {
        Location location = new Location();
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
            locationMapPanel.drawButtons(convertInput(input));
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

        updateEnabledComponents();
    }

    //
    // Row and col spinners
    //
    @Override
    public void stateChanged(ChangeEvent e) {
        selectedLocationButton = null;

        updateEnabledComponents();
    }

    //
    // Custom check box
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED || e.getStateChange() == ItemEvent.DESELECTED) {
            locationType.setCustom(customTb.isSelected());
            setLocationDetails(locationType);
            selectedLocationButton = null;
            updateEnabledComponents();
        }
    }
}