package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.dialogs.edititemlocationdialog.EditItemLocation;
import com.waldo.inventory.managers.CacheManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

class WizardLocationsPanel extends JPanel implements GuiInterface, ILocationMapPanel.LocationClickListener {


    private enum LocationComputeType {
        None("None"),
        Set("Inherit from set"),
        Custom("Custom");

        private final String name;

        LocationComputeType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private JComboBox<LocationComputeType> locationComputeTypeCb;

    private ILocationMapPanel startLocationPnl;
    private IComboBox<LocationType> locationTypeCb;

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

    void updateSettings(WizardSettings settings) {
        if (settings != null) {
            if (startLocation != null && !startLocation.isUnknown()) {
                computeLocations(
                        settings,
                        getComputeType(),
                        getLeftToRight(),
                        getUpDown(),
                        getOverWrite(),
                        getNumberPerLocation(),
                        startLocation);
            }
        }
    }

    private void updateEnabledComponents() {
        LocationComputeType computeType = getComputeType();
        boolean enabled = computeType != null && computeType == LocationComputeType.Custom;

        startLocationPnl.setVisible(enabled);
        locationTypeCb.setEnabled(enabled);
        leftRightCb.setEnabled(enabled);
        upDownCb.setEnabled(enabled);
        overWriteCb.setEnabled(enabled);
        numberPerLocationSp.setEnabled(enabled);
    }

    private void updateLocationType(LocationType locationType, Location startLocation) {
        if (locationType != null && !locationType.isUnknown()) {
            this.locationType = locationType;
            this.startLocation = startLocation;

            locationTypeCb.setSelectedItem(locationType);
            startLocationPnl.setLocations(locationType.getLocations());
            startLocationPnl.setHighlighted(startLocation, ILocationMapPanel.GREEN);
            parent.pack();
        }
    }

    private LocationComputeType getComputeType() {
        return (LocationComputeType) locationComputeTypeCb.getSelectedItem();
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
            LocationComputeType computeType,
            boolean leftRight,
            boolean upDown,
            boolean overWrite,
            int numberPerLocation,
            Location startLocation) {

        switch (computeType) {
            case None:
                noneLocationCompute(settings);
                break;
            case Set:
                setLocationCompute(settings);
                break;
            case Custom:
                customLocationCompute(settings, leftRight, upDown, overWrite, numberPerLocation, startLocation);
                break;
        }
    }

    private void noneLocationCompute(WizardSettings settings) {
        for (Value value : settings.getValues()) {
            settings.setLocation(value, Location.unknownLocation());
        }
        settings.setOverWriteLocations(false);
        settings.setNumberOfLocations(0);
    }

    private void setLocationCompute(WizardSettings settings) {
        Location setLocation = settings.getSelectedSet().getLocation();
        if (setLocation == null) {
            setLocation = Location.unknownLocation();
        }
        for (Value value : settings.getValues()) {
            settings.setLocation(value, setLocation);
        }
        settings.setOverWriteLocations(false);
        settings.setNumberOfLocations(1);
    }

    private void customLocationCompute(WizardSettings settings, boolean leftRight, boolean upDown, boolean overWrite, int numberPerLocation, Location startLocation) {
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
            settings.setOverWriteLocations(overWrite);
            settings.setNumberOfLocations(total);
        }
    }


    @Override
    public void initializeComponents() {
        locationComputeTypeCb = new IComboBox<>();
        locationComputeTypeCb.setModel(new DefaultComboBoxModel<>(LocationComputeType.values()));
        locationComputeTypeCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateEnabledComponents();
            }
        });

        locationTypeCb = new IComboBox<>(CacheManager.cache().getLocationTypes(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        locationTypeCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                LocationType locationType = (LocationType) locationTypeCb.getSelectedItem();
                Location startLocation = null;
                if (locationType != null && locationType.getLocations().size() > 0) {
                    startLocation = locationType.getLocations().get(0);
                }
                updateLocationType(locationType, startLocation);
                updateEnabledComponents();
            }
        });
        startLocationPnl = new ILocationMapPanel(application, this, true);

        leftRightCb = new JCheckBox("", true);
        upDownCb = new JCheckBox("", true);
        overWriteCb = new JCheckBox("", true);

        SpinnerNumberModel numberModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        numberPerLocationSp = new JSpinner(numberModel);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel typePanel = new JPanel(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel customPanel = new JPanel(new BorderLayout());

        typePanel.add(new ILabel("Compute type: "), BorderLayout.WEST);
        typePanel.add(locationComputeTypeCb, BorderLayout.CENTER);

        JPanel settingPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingPnl);

        gbc.addLine("Left -> Right ", leftRightCb);
        gbc.addLine("Up -> Down ", upDownCb);
        gbc.addLine("Over-write known locations ", overWriteCb);
        gbc.addLine("# per location", numberPerLocationSp);
        gbc.addLine("Start location: ", locationTypeCb);

        customPanel.add(settingPnl, BorderLayout.NORTH);
        customPanel.add(startLocationPnl, BorderLayout.CENTER);
        customPanel.setBorder(GuiUtils.createTitleBorder("Custom"));

        mainPanel.add(typePanel, BorderLayout.NORTH);
        mainPanel.add(customPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
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
        locationComputeTypeCb.setSelectedItem(LocationComputeType.Custom);
        updateLocationType(locationType, startLocation);
    }

    @Override
    public void onLocationClicked(ActionEvent e, Location location) {
        startLocation = location;
        startLocationPnl.clearHighlights();
        startLocationPnl.setHighlighted(location, ILocationMapPanel.GREEN);
    }
}
