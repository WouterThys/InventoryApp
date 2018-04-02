package com.waldo.inventory.Utils;

import com.waldo.inventory.classes.ObjectLog;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.cache.CacheList;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.classes.search.ObjectMatch;

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
            if (i1 == null && i2 != null) {
                return -1;
            } else if (i1 != null && i2 == null) {
                return 1;
            } else if (i1 != null){
                // Actual compare
                return Long.compare(i1.getDivisionId(), i2.getDivisionId());
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

    public static class ItemValueComparator implements Comparator<Item> {
        @Override
        public String toString() {
            return "Value";
        }

        @Override
        public int compare(Item o1, Item o2) {
            ValueComparator lc = new ValueComparator();
            return lc.compare(o1.getValue(), o2.getValue());
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
    // Value
    //
    public static class ValueComparator implements Comparator<Value> {
        @Override
        public int compare(Value v1, Value v2) {
            if (v1 != null && v2 != null) {
                return v1.getRealValue().compareTo(v2.getRealValue());
            }
            return 0;
        }
    }

    //
    // Cache logs
    //
    public static class CacheLogComparator implements Comparator<ObjectLog> {
        @Override
        public int compare(ObjectLog o1, ObjectLog o2) {
            CacheList cacheList1 = o1.getCacheList();
            CacheList cacheList2 = o2.getCacheList();

            if (cacheList1.isFetched() && !cacheList2.isFetched()) {
                return -1;
            } else if (!cacheList1.isFetched() && cacheList2.isFetched()) {
                return 1;
            } else if (cacheList1.isFetched()) {
                return -Integer.compare(cacheList1.size(), cacheList2.size());
            } else {
                return 1;
            }
        }
    }

    //
    // Match
    //
    public static class FoundMatchComparator implements Comparator<ObjectMatch> {
        @Override
        public int compare(ObjectMatch m1, ObjectMatch m2) {
            if (m1 == null && m2 != null) {
                return -1;
            } else if (m1 != null && m2 == null) {
                return 1;
            } else if (m1 != null){
                int res = Integer.compare(m1.getPercent(), m2.getPercent());
                if (res == 0) {
                    res = Integer.compare(m2.getTotalMatches(), m1.getTotalMatches());
                }
                return res;
            } else {
                return -1;
            }
        }
    }
}
