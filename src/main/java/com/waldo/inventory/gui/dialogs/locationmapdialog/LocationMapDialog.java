package com.waldo.inventory.gui.dialogs.locationmapdialog;

import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.Application;

public class LocationMapDialog extends LocationMapDialogLayout {


    public LocationMapDialog(Application application, String title, LocationType locationType) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(locationType);

    }

    public int getRow(){
        return row;
    }

    public int getCol() {
        return col;
    }

}