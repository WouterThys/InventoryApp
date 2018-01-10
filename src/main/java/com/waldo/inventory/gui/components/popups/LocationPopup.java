package com.waldo.inventory.gui.components.popups;

import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILocationMapPanel;

import javax.swing.*;

public class LocationPopup extends JPopupMenu {


    public LocationPopup(Application application, Location location) {
        LocationType type = location.getLocationType();

        ILocationMapPanel panel = new ILocationMapPanel(application, type.getLocations(), null, false);
        panel.setHighlighted(location, ILocationMapPanel.GREEN);

        JMenuItem name = new JMenuItem(type.getName());
        name.setEnabled(false);

        add(name);
        addSeparator();
        add(panel);
    }
}
