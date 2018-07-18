package com.waldo.inventory.gui.dialogs.locationtypedialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.LocationLabel;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.ILocationLabelPreview;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.components.IResourceDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.customlocationdialog.CustomLocationDialog;
import com.waldo.inventory.gui.dialogs.editlocationlabeldialog.EditLocationLabelDialog;
import com.waldo.inventory.gui.dialogs.inventorydialog.InventoryCacheDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class LocationTypeDialog extends IResourceDialog<LocationType>  {

    private static final ImageIcon icon = imageResource.readIcon("Location.L");

    // Details
    private ITextField detailName;
    private IActions.EditAction editAction;
    private IActions.InventoryAction inventoryAction;
    private IActions.PrintAction printAction;
    private ILocationMapPanel locationMapPanel;

    // Labels
    private IComboBox<LocationLabel> locationLabelCb;
    private ILocationLabelPreview locationLabelPreview;
    private IActions.EditAction editLocationLabelAction;
    private IActions.AddAction addLocationLabelAction;
    private IActions.DeleteAction deleteLocationLabelAction;

    public LocationTypeDialog(Window window) {
        super(window, "Locations", LocationType.class);

        addCacheListener(LocationLabel.class, createLabelListener());
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

    private CacheChangedListener<LocationLabel> createLabelListener() {
        return new CacheChangedListener<LocationLabel>() {
            @Override
            public void onInserted(LocationLabel label) {
                locationLabelCb.updateList(cache().getLocationLabels());
                locationLabelCb.setSelectedItem(label);
                locationLabelPreview.updateComponents(label);
            }

            @Override
            public void onUpdated(LocationLabel label) {
                locationLabelCb.updateList(cache().getLocationLabels());
                locationLabelCb.setSelectedItem(label);
                locationLabelPreview.updateComponents(label);
            }

            @Override
            public void onDeleted(LocationLabel label) {
                locationLabelCb.updateList(cache().getLocationLabels());
                locationLabelPreview.updateComponents();
            }

            @Override
            public void onCacheCleared() {

            }
        };
    }

    private void editLocationLabel(final LocationType locationType) {
        if (locationType != null && locationType.getLocationLabel() != null) {
            EditLocationLabelDialog dialog = new EditLocationLabelDialog(LocationTypeDialog.this, locationType.getLocationLabel());
            dialog.showDialog();
        }
    }

    private void addLocationLabel(final LocationType locationType) {
        if (locationType != null) {
            String name = JOptionPane.showInputDialog(
                    LocationTypeDialog.this,
                    "Enter name: ",
                    "New Label",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null && !name.isEmpty()) {
                LocationLabel label = SearchManager.sm().findLocationLabelByName(name);
                if (label == null) {
                    label = new LocationLabel(name);
                    label.save();
                } else {
                    JOptionPane.showMessageDialog(
                            LocationTypeDialog.this,
                            name + " already used!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    SwingUtilities.invokeLater(() -> addLocationLabel(locationType));
                }
            }
        }
    }

    private void deleteLocationLabel(LocationType locationType) {
        if (locationType != null) {
            locationType.setLocationLabelId(0);
            locationLabelCb.setSelectedIndex(0);
            locationLabelPreview.updateComponents();
            onValueChanged(locationLabelCb, "locationLabelId", 1, 1);
        }
    }

    private JPanel makeDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new GridBagLayout());
        JToolBar toolBar = GuiUtils.createNewToolbar(inventoryAction, editAction, printAction);

        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(northPanel, 50);
        gbh.addLine("Name: ", detailName);
        gbh.add(toolBar, 1,1);

        // Add
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(locationMapPanel, BorderLayout.CENTER);

        panel.setBorder(GuiUtils.createInlineTitleBorder("Details"));

        return panel;
    }

    private JPanel makeLabelPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel cbPanel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(cbPanel, 50);
        gbc.addLine("Label: ", GuiUtils.createComponentWithActions(locationLabelCb, editLocationLabelAction, addLocationLabelAction, deleteLocationLabelAction));

        panel.add(cbPanel, BorderLayout.NORTH);
        panel.add(locationLabelPreview, BorderLayout.CENTER);

        panel.setBorder(GuiUtils.createInlineTitleBorder("Label"));

        return panel;
    }

    @Override
    protected void initializeDetailComponents() {
        setResizable(true);
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
        printAction = new IActions.PrintAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doPrint(getSelectedResource());
            }
        };
        locationMapPanel = new ILocationMapPanel(this, null, true);

        locationLabelCb = new IComboBox<>(cache().getLocationLabels(), new ComparatorUtils.DbObjectNameComparator<>(), true);
        locationLabelCb.addEditedListener(this, "locationLabelId");
        locationLabelPreview = new ILocationLabelPreview();

        editLocationLabelAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> editLocationLabel(getSelectedResource()));
            }
        };
        addLocationLabelAction = new IActions.AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> addLocationLabel(getSelectedResource()));
            }
        };
        deleteLocationLabelAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> deleteLocationLabel(getSelectedResource()));
            }
        };
    }

    @Override
    protected JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));

        Box box = Box.createVerticalBox();

        box.add(makeDetailPanel());
        box.add(makeLabelPanel());

        panel.add(box, BorderLayout.CENTER);

        return panel;
    }

    @Override
    protected void setDetails(LocationType locationType) {
        if (locationType != null) {
            detailName.setText(locationType.getName());
            locationMapPanel.setLocations(locationType.getLocations());
            locationMapPanel.setLocationsWithItemHighlighted(com.waldo.inventory.gui.components.ILocationMapPanel.GREEN);

            locationLabelCb.setSelectedItem(locationType.getLocationLabel());
            locationLabelPreview.updateComponents(locationType.getLocationLabel());
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
        locationLabelCb.setSelectedIndex(0);
        locationLabelPreview.updateComponents();
    }



    private void editLocationType(LocationType locationType) {
        if (locationType != null) {
            CustomLocationDialog dialog = new CustomLocationDialog(this, "Custom", locationType);
            if (dialog.showDialog() == ICacheDialog.OK) {
                locationType.updateLocations();
                setDetails(locationType);
            }
        }
    }

    private void doInventory(LocationType locationType) {
        if (locationType != null) {
            InventoryCacheDialog dialog = new InventoryCacheDialog(LocationTypeDialog.this, "Inventory", locationType);
            dialog.showDialog();
        }
    }

    private void doPrint(LocationType locationType) {
        if ( locationType != null) {

        }
    }
}