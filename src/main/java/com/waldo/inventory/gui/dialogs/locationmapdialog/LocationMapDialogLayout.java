package com.waldo.inventory.gui.dialogs.locationmapdialog;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class LocationMapDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILocationMapPanel locationMapPanel;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    int row;
    int col;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    LocationMapDialogLayout(Application application, String title) {
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
        // Panel
        locationMapPanel = new ILocationMapPanel(application, (e, items, row, column) -> {
            locationMapPanel.setHighlighted(this.row, this.col, null);
            this.row = row;
            this.col = column;
            locationMapPanel.setHighlighted(this.row, this.col, ILocationMapPanel.GREEN);
        });
    }

    @Override
    public void initializeLayouts() {
        add(locationMapPanel);
        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            LocationType type = (LocationType)object;
            locationMapPanel.updateComponents(object);
            locationMapPanel.setItems(SearchManager.sm().findItemsWithLocation(type.getId()));
            locationMapPanel.setHighlighted(row, col, ILocationMapPanel.GREEN);
        }
    }
}