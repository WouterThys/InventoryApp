package com.waldo.inventory.gui.components.tablemodels;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.inventory.gui.components.ILabel;

import java.util.List;

public class IItemTableModel extends IAbstractTableModel<Item> {

    // Names and classes
    private static final String[] COLUMN_NAMES = {"", "Name", "Description", "Manufacturer", "Location"};
    private static final Class[] COLUMN_CLASSES = {ILabel.class, String.class, String.class, String.class, String.class};

    public IItemTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public void setItemList(List<Item> itemList) {
        //itemList.sort(new Item.ItemDivisionComparator());
        super.setItemList(itemList);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = getItemAt(rowIndex);

        if (item != null) {
            switch (columnIndex) {
                case -1:
                    return item;
                case 0: // Amount label
                    return item;
                case 1: // Name
                    return item.toString();
                case 2: // Description
                    return item.getDescription();
                case 3: // Manufacturer
                    Manufacturer m = SearchManager.sm().findManufacturerById(item.getManufacturerId());
                    if (m != null && !m.isUnknown()) {
                        return m.toString();
                    }
                    return "";
                case 4: // Location
                    Location l = SearchManager.sm().findLocationById(item.getLocationId());
                    if (l != null && !l.isUnknown()) {
                        if (item.isSet()) {
                            return l.getLocationType().getName().substring(0,3);
                        } else {
                            return l.getPrettyString();
                        }
                    }
                    return "";
            }
        }
        return null;
    }


}
