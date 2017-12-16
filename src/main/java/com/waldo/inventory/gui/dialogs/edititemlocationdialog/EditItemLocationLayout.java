package com.waldo.inventory.gui.dialogs.edititemlocationdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;

import java.awt.*;
import java.awt.event.ItemListener;

import static com.waldo.inventory.managers.CacheManager.cache;

abstract class EditItemLocationLayout extends IDialog implements ItemListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILocationMapPanel locationMapPanel;
    IComboBox<LocationType> locationTypeCb;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Location location;
    Location originalLocation;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditItemLocationLayout(Application application, String title) {
        super(application, title);

        showTitlePanel(false);
    }



    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        setResizable(true);
        // Panel
        locationMapPanel = new ILocationMapPanel(application, null, (e, location) -> {
            this.location = location;
            locationMapPanel.clearHighlights();
            locationMapPanel.setHighlighted(location, ILocationMapPanel.GREEN);
        }, true);

        locationTypeCb = new IComboBox<>(cache().getLocationTypes(), new DbObjectNameComparator<>(), true);
        locationTypeCb.addItemListener(this);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        getContentPanel().add(locationTypeCb, BorderLayout.NORTH);
        getContentPanel().add(locationMapPanel);
        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        application.beginWait();
        try {
            if (object.length != 0 && object[0] != null) {
                location = (Location) object[0];
                originalLocation = location.createCopy();
                locationMapPanel.setLocations(location.getLocationType().getLocations());
                locationMapPanel.setHighlighted(location, ILocationMapPanel.GREEN);
                locationTypeCb.setSelectedItem(location.getLocationType());
            } else {
                location = null;
                locationMapPanel.setLocations(null);
                locationMapPanel.clearHighlights();
                locationTypeCb.setSelectedItem(null);
            }
        } finally {
            application.endWait();
        }
    }
}