package com.waldo.inventory.gui.dialogs.locationtypedialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.components.IResourceDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.customlocationdialog.CustomLocationDialog;
import com.waldo.inventory.gui.dialogs.inventorydialog.InventoryDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class LocationTypeDialog extends IResourceDialog<LocationType> {

    private static final ImageIcon icon = imageResource.readImage("Locations.Title");

    private ITextField detailName;
    private IActions.EditAction editAction;
    private IActions.InventoryAction inventoryAction;
    private ILocationMapPanel locationMapPanel;

    public LocationTypeDialog(Window window) {
        super(window, "Locations", LocationType.class);
    }

    @Override
    protected List<LocationType> getAllResources() {
        return cache().getLocationTypes();
    }

    @Override
    protected LocationType getNewResource() {
        return new LocationType();
    }

    @Override
    protected void updateEnabledComponents() {
        super.updateEnabledComponents();
    }

    @Override
    protected void initializeDetailComponents() {
        setTitleIcon(icon);
        detailName = new ITextField("Name");
        detailName.setEnabled(false);

        editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editLocationType(getSelectedResource());
            }
        };

        inventoryAction = new IActions.InventoryAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doInventory(getSelectedResource());
            }
        };

        locationMapPanel = new ILocationMapPanel(this, null, true);
    }

    @Override
    protected JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        JPanel northPanel = new JPanel(new GridBagLayout());
        JToolBar toolBar = GuiUtils.createNewToolbar(inventoryAction, editAction);

        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(northPanel);
        gbh.addLine("Name: ", detailName);
        gbh.add(toolBar, 1,1);

        // Add
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(locationMapPanel, BorderLayout.CENTER);

        return panel;
    }

    @Override
    protected void setDetails(LocationType locationType) {
        if (locationType != null) {
            detailName.setText(locationType.getName());
            locationMapPanel.setLocations(locationType.getLocations());
            locationMapPanel.setLocationsWithItemHighlighted(com.waldo.inventory.gui.components.ILocationMapPanel.GREEN);
        } else {
            clearDetails();
        }
    }

    @Override
    public VerifyState verify(LocationType toVerify) {
        VerifyState ok = VerifyState.Ok;
        if (detailName.getText().isEmpty()) {
            detailName.setError("Name can't be empty");
            ok = VerifyState.Error;
        } else {
            if (toVerify.getId() < DbObject.UNKNOWN_ID) {
                if (SearchManager.sm().findLocationTypeByName(detailName.getText()) != null) {
                    detailName.setError("Name already exists..");
                    ok = VerifyState.Error;
                }
            }
        }

        return ok;
    }

    @Override
    public void clearDetails() {
        detailName.setText("");
        locationMapPanel.clear();
    }



    private void editLocationType(LocationType locationType) {
        if (locationType != null) {
            CustomLocationDialog dialog = new CustomLocationDialog(this, "Custom", locationType);
            if (dialog.showDialog() == IDialog.OK) {
                locationType.updateLocations();
                setDetails(locationType);
            }
        }
    }

    private void doInventory(LocationType locationType) {
        if (locationType != null) {
            InventoryDialog dialog = new InventoryDialog(LocationTypeDialog.this, "Inventory", locationType);
            dialog.showDialog();
        }
    }
}