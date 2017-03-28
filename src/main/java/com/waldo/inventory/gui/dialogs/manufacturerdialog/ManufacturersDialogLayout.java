package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.database.interfaces.ManufacturersChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialogPanel;

import javax.swing.*;

public abstract class ManufacturersDialogLayout extends IDialogPanel
        implements GuiInterface, ManufacturersChangedListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    ManufacturersDialogLayout(Application application, JDialog dialog) {
        super(application, dialog, true);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
    *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object object) {

    }


    @Override
    public void onManufacturerAdded(Manufacturer manufacturer) {

    }

    @Override
    public void onManufacturerUpdated(Manufacturer manufacturer) {

    }

    @Override
    public void onManufacturerDeleted(Manufacturer manufacturer) {

    }
}
