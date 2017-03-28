package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.Location;

public interface LocationChangedListener {
    void onLocationAdded(Location location);
    void onLocationUpdated(Location location);
    void onLocationDeleted(Location location);
}
