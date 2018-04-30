package com.waldo.inventory.gui.dialogs.inventorydialog;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.classes.dbclasses.LocationType.LocationNeighbour;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.IInventoryTableModel;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITableEditors;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

abstract class InventoryCacheDialogLayout extends ICacheDialog
        implements ILocationMapPanel.LocationClickListener, IdBToolBar.IdbToolBarListener, ListSelectionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    // Location map
    private ILocationMapPanel locationMapPanel;

    // Current location
    private ILabel currentLocationLbl;
    private IdBToolBar itemToolBar;
    IInventoryTableModel tableModel;
    ITable<Item> itemTable;

    // Next location
    private ILabel nextLocationLbl;
    private IActions.NextAction nextAction;

    // Previous location
    private ILabel previousLocationLbl;
    private IActions.PreviousAction previousAction;


     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LocationType locationType;
    Location currentLocation;
    Item currentItem;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    InventoryCacheDialogLayout(Window window, String title, LocationType locationType) {
        super(window, title);
        this.locationType = locationType;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean enabled = currentItem != null;

        itemToolBar.setEditActionEnabled(enabled);
        itemToolBar.setDeleteActionEnabled(enabled);
    }

    void setCurrentLocation(Location location) {

        if (location != null) {
            currentLocation = location;

            currentLocationLbl.setText(currentLocation.toString());

            if (currentLocation.getItems().size() > 0) {
                tableModel.setItemList(currentLocation.getItems());
                itemTable.selectItem(currentLocation.getItems().get(0));
                itemTable.setRowSelectionInterval(0,0);
                itemTable.setColumnSelectionInterval(1, 1);
            } else {
                tableModel.clearItemList();
                currentItem = null;
            }

            Location next = locationType.getNeighbourOfLocation(currentLocation, LocationNeighbour.Right, true, true);
            Location prev = locationType.getNeighbourOfLocation(currentLocation, LocationNeighbour.Left, false, false);

            setNextLocation(next);
            setPreviousLocation(prev);

            locationMapPanel.clearHighlights();
            locationMapPanel.setHighlighted(currentLocation, ILocationMapPanel.BLUE);
        } else {
            currentLocation = null;
            currentItem = null;

            currentLocationLbl.setText("");
            previousLocationLbl.setText("");
            nextLocationLbl.setText("");

            setPreviousLocation(null);
            setNextLocation(null);
            locationMapPanel.clearHighlights();
        }

        updateEnabledComponents();
    }

    private void setPreviousLocation(Location location) {
        if (location != null) {
            previousAction.setEnabled(true);
            previousLocationLbl.setText(location.toString());
        } else {
            previousAction.setEnabled(false);
            previousLocationLbl.setText("");
        }
    }

    private void setNextLocation(Location location) {
        if (location != null) {
            nextAction.setEnabled(true);
            nextLocationLbl.setText(location.toString());
        } else {
            nextAction.setEnabled(false);
            nextLocationLbl.setText("");
        }
    }

    private JPanel createLocationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Center panel
        JPanel centerPnl = new JPanel(new BorderLayout());
        JPanel ctrTopPnl = new JPanel(new BorderLayout());
        JPanel ctrBtmPnl = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setPreferredSize(new Dimension(500, 100));

        ctrTopPnl.add(currentLocationLbl, BorderLayout.CENTER);
        ctrBtmPnl.add(itemToolBar, BorderLayout.EAST);

        centerPnl.add(ctrTopPnl, BorderLayout.NORTH);
        centerPnl.add(scrollPane, BorderLayout.CENTER);
        centerPnl.add(ctrBtmPnl, BorderLayout.SOUTH);

        // Previous panel
        JPanel previousPnl = new JPanel(new BorderLayout());
        previousPnl.add(previousLocationLbl, BorderLayout.NORTH);
        previousPnl.add(GuiUtils.createNewToolbar(previousAction), BorderLayout.CENTER);
        previousPnl.setBackground(Color.gray.brighter());

        // Next panel
        JPanel nextPnl = new JPanel(new BorderLayout());
        nextPnl.add(nextLocationLbl, BorderLayout.NORTH);
        nextPnl.add(GuiUtils.createNewToolbar(nextAction), BorderLayout.CENTER);
        nextPnl.setBackground(Color.gray.brighter());

        // Add
        panel.add(centerPnl, BorderLayout.CENTER);
        panel.add(previousPnl, BorderLayout.WEST);
        panel.add(nextPnl, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(2,2,2,2)
        ));

        return panel;
    }

    private JPanel createLocationMapPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(locationMapPanel, BorderLayout.CENTER);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setTitleIcon(imageResource.readIcon("Inventory.Title"));
        setTitleName(getTitle());
        setResizable(true);

        locationMapPanel = new ILocationMapPanel(getOwner(), this, false);

        currentLocationLbl = new ILabel("", SwingConstants.CENTER);
        currentLocationLbl.setFont(50, Font.BOLD);

        itemToolBar = new IdBToolBar(this, false, true, true, true);
        tableModel = new IInventoryTableModel();
        itemTable = new ITable<>(tableModel);
        itemTable.getSelectionModel().addListSelectionListener(this);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn tableColumn = itemTable.getColumnModel().getColumn(1);
        tableColumn.setCellEditor(new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int i) {
                if (currentItem != null) {
                    currentItem.setAmount(i);
                    currentItem.save();
                }
            }
        });

        nextLocationLbl = new ILabel("", SwingConstants.CENTER);
        nextLocationLbl.setFont(35, Font.BOLD);
        nextAction = new IActions.NextAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentLocation != null) {
                    SwingUtilities.invokeLater(() -> {
                        Location next = locationType.getNeighbourOfLocation(
                                currentLocation,
                                LocationNeighbour.Right,
                                true, true);
                        setCurrentLocation(next);
                    });
                }
            }
        };

        previousLocationLbl = new ILabel("", SwingConstants.CENTER);
        previousLocationLbl.setFont(35, Font.BOLD);
        previousAction = new IActions.PreviousAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentLocation != null) {
                    SwingUtilities.invokeLater(() -> {
                        Location prev = locationType.getNeighbourOfLocation(
                                currentLocation,
                                LocationNeighbour.Left,
                                false, false);
                        setCurrentLocation(prev);
                    });
                }
            }
        };
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(createLocationsPanel(), BorderLayout.CENTER);
        panel.add(createLocationMapPanel(), BorderLayout.SOUTH);

        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(panel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (locationType != null) {

            locationMapPanel.setLocations(locationType.getLocations());
            pack();

            if (currentLocation == null && locationType.getLocations().size() > 0) {
                setCurrentLocation(locationType.getLocations().get(0));
            }
        }
    }
}