package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.dialogs.edititemlocationdialog.EditItemLocation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class WizardLocationsPanel extends JPanel implements GuiInterface, ILocationMapPanel.LocationClickListener {

    private ILocationMapPanel startLocationPnl;

    private JCheckBox leftRightCb;
    private JCheckBox upDownCb;
    private JCheckBox overWriteCb;

    private JSpinner numberPerLocationSp;

    private final Application application;
    private final IDialog parent;
    private LocationType locationType;
    private Location startLocation;

    WizardLocationsPanel(Application application, IDialog parent, Location location) {
        super();

        this.application = application;
        this.parent = parent;

        if (location != null) {
            this.locationType = location.getLocationType();
            this.startLocation = location;
        }

        initializeComponents();
        initializeLayouts();
    }

    public void updateSettings(WizardSettings settings) {
        if (settings != null) {
            if (startLocation != null && !startLocation.isUnknown()) {
                computeLocations(settings, getLeftToRight(), getUpDown(), getOverWrite(), getNumberPerLocation(), startLocation);
            }
        }
    }

    private boolean getLeftToRight() {
        return leftRightCb.isSelected();
    }

    private boolean getUpDown() {
        return upDownCb.isSelected();
    }

    private boolean getOverWrite() {
        return overWriteCb.isSelected();
    }

    private int getNumberPerLocation() {
        return ((SpinnerNumberModel) numberPerLocationSp.getModel()).getNumber().intValue();
    }

    private void computeLocations(
            WizardSettings settings,
            boolean leftRight,
            boolean upDown,
            boolean overWrite,
            int numberPerLocation,
            Location startLocation) {

        LocationType locationType = startLocation.getLocationType();
        LocationType.LocationNeighbour direction = leftRight ? LocationType.LocationNeighbour.Right : LocationType.LocationNeighbour.Left;

        Location newLocation = startLocation;
        int count = 0;
        int total = 0;
        for (Value value : settings.getValues()) {
            //if (setItem.getLocationId() <= DbObject.UNKNOWN_ID || overWrite) {
                // Set location
               settings.setLocation(value, newLocation);
                count++;

                if (count >= numberPerLocation) {
                    count = 0;
                    total++;
                    // Find new location
                    newLocation = locationType.getNeighbourOfLocation(newLocation, direction, leftRight, upDown);
                    if (newLocation == null) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Location error",
                                "Could not find a valid next location..",
                                JOptionPane.ERROR_MESSAGE
                        );
                        break;
                    }
                }
            //}
            settings.setNumberOfLocations(total);
        }
    }


    @Override
    public void initializeComponents() {
        startLocationPnl = new ILocationMapPanel(application, this, true);

        leftRightCb = new JCheckBox("", true);
        upDownCb = new JCheckBox("", true);
        overWriteCb = new JCheckBox("", true);

        SpinnerNumberModel numberModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        numberPerLocationSp = new JSpinner(numberModel);
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel settingPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingPnl);
        gbc.addLine("Left -> Right ", leftRightCb);
        gbc.addLine("Up -> Down ", upDownCb);
        gbc.addLine("Over-write known locations ", overWriteCb);
        gbc.addLine("# location", numberPerLocationSp);
        gbc.addLine("Start location: ", new ILabel());

        panel.add(settingPnl, BorderLayout.NORTH);
        panel.add(startLocationPnl, BorderLayout.CENTER);

        add(panel);
        setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }

    @Override
    public void updateComponents(Object... object) {
        if (locationType == null || locationType.isUnknown()) {
            EditItemLocation dialog = new EditItemLocation(application,
                    "Select new location",
                    startLocation);

                if (dialog.showDialog() == IDialog.OK) {
                    Location newLocation = dialog.getItemLocation();
                    if (newLocation != null) {
                        startLocation = newLocation;
                        locationType = newLocation.getLocationType();
                    }
                }
            }
        if (locationType != null && !locationType.isUnknown()) {
            startLocationPnl.setLocations(locationType.getLocations());
            startLocationPnl.setHighlighted(startLocation, ILocationMapPanel.GREEN);
            parent.pack();
        }
    }

    @Override
    public void onLocationClicked(ActionEvent e, Location location) {
        startLocation = location;
        startLocationPnl.clearHighlights();
        startLocationPnl.setHighlighted(location, ILocationMapPanel.GREEN);
    }
}
