package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.classes.Manufacturer;

public interface ManufacturersChangedListener {
    void onManufacturerAdded(Manufacturer manufacturer);
    void onManufacturerUpdated(Manufacturer manufacturer);
    void onManufacturerDeleted(Manufacturer manufacturer);
}
