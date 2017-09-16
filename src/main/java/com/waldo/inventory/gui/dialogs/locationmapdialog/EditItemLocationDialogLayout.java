package com.waldo.inventory.gui.dialogs.locationmapdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;

import java.awt.*;
import java.awt.event.ItemListener;

public abstract class EditItemLocationDialogLayout extends IDialog implements ItemListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILocationMapPanel locationMapPanel;
    IComboBox<LocationType> locationTypeCb;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Location location;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditItemLocationDialogLayout(Application application, String title) {
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
        locationMapPanel = new ILocationMapPanel(application, (e, location) -> {
            this.location = location;
            locationMapPanel.clearHighlights();
            locationMapPanel.setHighlighted(location, ILocationMapPanel.GREEN);
        });

        locationTypeCb = new IComboBox<>(DbManager.db().getLocationTypes(), new DbObject.DbObjectNameComparator<>(), true);
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
    public void updateComponents(Object object) {
        application.beginWait();
        try {
            if (object != null) {
                location = (Location) object;
                locationMapPanel.updateComponents(location.getLocationType());
                locationMapPanel.setHighlighted(location, ILocationMapPanel.GREEN);
                locationTypeCb.setSelectedItem(location.getLocationType());
            } else {
                location = null;
                locationMapPanel.updateComponents(null);
                locationMapPanel.clearHighlights();
                locationTypeCb.setSelectedItem(null);
            }
        } finally {
            application.endWait();
        }
    }
}