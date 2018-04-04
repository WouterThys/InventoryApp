package com.waldo.inventory.gui.dialogs.locationtypedialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static javax.swing.SpringLayout.*;

abstract class LocationTypeDialogLayout extends IDialog implements
        ListSelectionListener,
        IObjectSearchPanel.SearchListener<LocationType>,
        IdBToolBar.IdbToolBarListener,
        IEditedListener{

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<LocationType> locationTypeList;
    private DefaultListModel<LocationType> locationTypeModel;
    private IdBToolBar toolBar;
    IObjectSearchPanel<LocationType> searchPanel;

    ITextField detailName;

    private IActions.EditAction editAction;
    private IActions.InventoryAction inventoryAction;

    ILocationMapPanel ILocationMapPanel;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LocationType selectedLocationType;
    LocationType originalLocationType;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LocationTypeDialogLayout(Application application, String title) {
        super(application, title);

    }

    abstract void onEditLocation();
    abstract void onInventory();

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        toolBar.setEditActionEnabled(selectedLocationType != null);
        toolBar.setDeleteActionEnabled(selectedLocationType != null);
    }

    void setLocationTypeList(List<LocationType> locationTypeList) {
        if (locationTypeList != null) {
            locationTypeList.sort(new ComparatorUtils.DbObjectNameComparator<>());
            locationTypeModel.removeAllElements();
            for (LocationType d : locationTypeList) {
                locationTypeModel.addElement(d);
            }
        }
    }

    private JPanel createWestPanel() {
        TitledBorder titledBorder = GuiUtils.createTitleBorder("Locations");

        JPanel westPanel = new JPanel();
        JScrollPane list = new JScrollPane(locationTypeList);

        SpringLayout layout = new SpringLayout();
        // Search panel
        layout.putConstraint(NORTH, searchPanel, 5, NORTH, westPanel);
        layout.putConstraint(EAST, searchPanel, -5, EAST, westPanel);
        layout.putConstraint(WEST, searchPanel, 5, WEST, westPanel);

        // Location type list
        layout.putConstraint(EAST, list, -5, EAST, westPanel);
        layout.putConstraint(WEST, list, 5, WEST, westPanel);
        layout.putConstraint(SOUTH, list, -5, NORTH, toolBar);
        layout.putConstraint(NORTH, list, 2, SOUTH, searchPanel);

        // Tool bar
        layout.putConstraint(EAST, toolBar, -5, EAST, westPanel);
        layout.putConstraint(SOUTH, toolBar, -5, SOUTH, westPanel);
        layout.putConstraint(WEST, toolBar, 5, WEST, westPanel);

        // Add stuff
        westPanel.add(searchPanel);
        westPanel.add(list);
        westPanel.add(toolBar);
        westPanel.setLayout(layout);
        westPanel.setPreferredSize(new Dimension(300, 500));
        westPanel.setBorder(titledBorder);

        return westPanel;
    }

    private JPanel createLocationTypeDetailPanel() {
        TitledBorder titledBorder = GuiUtils.createTitleBorder("Info");
        JPanel panel = new JPanel(new BorderLayout(5,5));
        JPanel northPanel = new JPanel(new GridBagLayout());
        JToolBar toolBar = GuiUtils.createNewToolbar(inventoryAction, editAction);

        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(northPanel);
        gbh.addLine("Name: ", detailName);
        gbh.add(toolBar, 1,1);

        // Add
        panel.add(northPanel, BorderLayout.NORTH);
        panel.setBorder(titledBorder);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        setTitleIcon(imageResource.readIcon("Locations.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel<>(cache().getLocationTypes(), this);

        // Location list
        locationTypeModel = new DefaultListModel<>();
        locationTypeList = new JList<>(locationTypeModel);
        locationTypeList.addListSelectionListener(this);

        // Tool bar
        toolBar = new IdBToolBar(this);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);

        editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditLocation();
            }
        };

        inventoryAction = new IActions.InventoryAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onInventory();
            }
        };

        ILocationMapPanel = new ILocationMapPanel(this, null, true);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(createLocationTypeDetailPanel(), BorderLayout.NORTH);
        eastPanel.add(ILocationMapPanel, BorderLayout.CENTER);

        getContentPanel().add(createWestPanel(), BorderLayout.WEST);
        getContentPanel().add(eastPanel, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (isUpdating()) {
            return;
        }

        beginWait();
        try {
            // Get all
            setLocationTypeList(cache().getLocationTypes());

            selectedLocationType = (LocationType) object[0];
            updateEnabledComponents();

            if (selectedLocationType != null) {
                originalLocationType = selectedLocationType.createCopy();
                locationTypeList.setSelectedValue(selectedLocationType, true);
            } else {
                originalLocationType = null;
            }

        } finally {
            endWait();
        }
    }
}