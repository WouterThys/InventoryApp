package com.waldo.inventory.Utils;

import com.waldo.inventory.classes.dbclasses.*;

import java.util.Comparator;

public class ComparatorUtils {

    public static class DbObjectNameComparator<T extends DbObject> implements Comparator<T> {

        @Override
        public String toString() {
            return "Name";
        }

        @Override
        public int compare(T dbo1, T dbo2) {
            if (dbo1.isUnknown() && !dbo2.isUnknown()) {
                return -1;
            }
            if (!dbo1.isUnknown() && dbo2.isUnknown()) {
                return 1;
            }
            return dbo1.getName().compareTo(dbo2.getName());
        }
    }

    //
    // Item
    //
    public static class ItemDivisionComparator implements Comparator<Item> {

        @Override
        public String toString() {
            return "Division";
        }

        @Override
        public int compare(Item i1, Item i2) {
            if (i1.getCategoryId() == i2.getCategoryId()) {

                if (i1.getProductId() == i2.getProductId()) {

                    if (i1.getTypeId() == i2.getTypeId()) {

                        return i1.getName().compareTo(i2.getName());

                    } else if (i1.getTypeId() > i2.getTypeId()) {
                        return 1;
                    } else {
                        return -1;
                    }

                } else if (i1.getProductId() > i2.getProductId()) {
                    return 1;
                } else {
                    return -1;
                }

            } else if (i1.getCategoryId() > i2.getCategoryId()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public static class ItemManufacturerComparator implements Comparator<Item> {

        @Override
        public String toString() {
            return "Manufacturer";
        }

        @Override
        public int compare(Item o1, Item o2) {
            ManufacturerComparator mc = new ManufacturerComparator();
            return mc.compare(o1.getManufacturer(), o2.getManufacturer());
        }
    }

    public static class ItemLocationComparator implements Comparator<Item> {

        @Override
        public String toString() {
            return "Location";
        }

        @Override
        public int compare(Item o1, Item o2) {
            LocationComparator lc = new LocationComparator();
            return lc.compare(o1.getLocation(), o2.getLocation());
        }
    }

    //
    // Manufacturer
    //
    public static class ManufacturerComparator implements Comparator<Manufacturer> {

        @Override
        public String toString() {
            return "Manufacturer";
        }

        @Override
        public int compare(Manufacturer o1, Manufacturer o2) {
            if (o1 == null && o2 != null) {
                return -1;
            } else if (o1 != null && o2 == null) {
                return 1;
            } else if (o1 != null){
                // Actual compare
                return o1.getName().compareTo(o2.getName());
            } else {
                return -1;
            }
        }
    }

    //
    // Location
    //
    public static class LocationComparator implements Comparator<Location> {

        @Override
        public String toString() {
            return "Location";
        }

        @Override
        public int compare(Location o1, Location o2) {
            if (o1 == null && o2 != null) {
                return -1;
            } else if (o1 != null && o2 == null) {
                return 1;
            } else if (o1 != null){
                // Actual compare
                LocationTypeComparator ltc = new LocationTypeComparator();
                int res = ltc.compare(o1.getLocationType(), o2.getLocationType());
                if (res == 0) {
                    res = Integer.compare(o1.getRow(), o2.getRow());
                    if (res == 0) {
                        res = Integer.compare(o1.getCol(), o2.getCol());
                    }
                }
                return res;
            } else {
                return -1;
            }
        }
    }

    public static class LocationTypeComparator implements Comparator<LocationType> {

        @Override
        public String toString() {
            return "Stock";
        }

        @Override
        public int compare(LocationType o1, LocationType o2) {
            if (o1 == null && o2 != null) {
                return -1;
            } else if (o1 != null && o2 == null) {
                return 1;
            } else if (o1 != null){
                // Actual compare
                return o1.getName().compareTo(o2.getName());
            } else {
                return -1;
            }
        }
    }

    //
    // Set item
    //
    public static class SetItemComparator implements Comparator<SetItem> {
        @Override
        public int compare(SetItem o1, SetItem o2) {
            if (o1.getValue() != null && o2.getValue() != null) {
                return o1.getValue().getRealValue().compareTo(o2.getValue().getRealValue());
            }
            return 0;
        }
    }
}