package com.waldo.inventory.gui.dialogs.locationmapdialog;

import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.Application;

public class LocationMapDialog extends LocationMapDialogLayout {


    public LocationMapDialog(Application application, String title, LocationType locationType, int row, int col) {
        super(application, title);

        this.row = row;
        this.col = col;

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