package com.waldo.inventory.gui.dialogs.locationmapdialog;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;

import javax.swing.*;
import java.awt.*;

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
        // Dialog
        getButtonCancel().setVisible(false);
        getButtonOK().setVisible(false);

        // Panel
        locationMapPanel = new ILocationMapPanel(application, (row, column) -> {
            this.row = row;
            this.col = column;
            super.onOK();
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
            locationMapPanel.updateComponents(object);
        }
    }
}