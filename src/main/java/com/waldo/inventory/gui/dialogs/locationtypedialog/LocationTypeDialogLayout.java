package com.waldo.inventory.gui.dialogs.locationtypedialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.gui.Application.imageResource;
import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.WEST;

public abstract class LocationTypeDialogLayout extends IDialog implements
        ListSelectionListener,
        DbObjectChangedListener<LocationType>,
        IObjectSearchPanel.IObjectSearchListener,
        IObjectSearchPanel.IObjectSearchBtnListener,
        IdBToolBar.IdbToolBarListener,
        ActionListener,
        IEditedListener{

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<LocationType> locationTypeList;
    private DefaultListModel<LocationType> locationTypeModel;
    private IdBToolBar toolBar;
    private IObjectSearchPanel searchPanel;

    private JButton detailCustomBtn;
    ITextField detailName;

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

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        toolBar.setEditActionEnabled(selectedLocationType != null);
        toolBar.setDeleteActionEnabled(selectedLocationType != null);
    }

    private JPanel createWestPanel() {
        TitledBorder titledBorder = PanelUtils.createTitleBorder("Locations");

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
        TitledBorder titledBorder = PanelUtils.createTitleBorder("Info");
        JPanel panel = new JPanel(new BorderLayout(5,5));
        JPanel northPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());

        buttonPanel.add(detailCustomBtn, BorderLayout.EAST);
        PanelUtils.GridBagHelper gbh = new PanelUtils.GridBagHelper(northPanel);
        gbh.addLine("Name: ", detailName);
        gbh.add(buttonPanel, 1,1);

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
        setTitleIcon(imageResource.readImage("Locations.Title"));
        setTitleName(getTitle());
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Search
        searchPanel = new IObjectSearchPanel(false, DbObject.TYPE_LOCATION_TYPE);
        searchPanel.addSearchListener(this);
        searchPanel.addSearchBtnListener(this);

        // Location list
        locationTypeModel = new DefaultListModel<>();
        locationTypeList = new JList<>(locationTypeModel);
        locationTypeList.addListSelectionListener(this);

        // Tool bar
        toolBar = new IdBToolBar(this);

        // Details
        detailName = new ITextField("Name");
        detailName.setEnabled(false);
        detailCustomBtn = new JButton(imageResource.readImage("Locations.Edit"));
        detailCustomBtn.addActionListener(this);

        ILocationMapPanel = new ILocationMapPanel(application, null, true);
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
        if (application.isUpdating()) {
            return;
        }

        application.beginWait();
        try {
            // Get all
            locationTypeModel.removeAllElements();
            for (LocationType lt : DbManager.db().getLocationTypes()) {
                if (!lt.isUnknown()) {
                    locationTypeModel.addElement(lt);
                }
            }

            selectedLocationType = (LocationType) object[0];
            updateEnabledComponents();

            if (selectedLocationType != null) {
                originalLocationType = selectedLocationType.createCopy();
                locationTypeList.setSelectedValue(selectedLocationType, true);
            } else {
                originalLocationType = null;
            }

        } finally {
            application.endWait();
        }
    }
}