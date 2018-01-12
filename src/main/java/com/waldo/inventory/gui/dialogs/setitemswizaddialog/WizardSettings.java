package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.Manufacturer;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.inventory.classes.dbclasses.Set;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WizardSettings {

    private final Set selectedSet;

    private Map<Value, Location> valueLocationMap = new TreeMap<>(new ComparatorUtils.ValueComparator());
    private int numberOfLocations;

    private Manufacturer manufacturer;

    private PackageType packageType;
    private int pins;

    private int amount;
    private String typeName;
    private boolean keepOldSetItems;
    private boolean replaceValues;

    WizardSettings(Set selectedSet) {
        this.selectedSet = selectedSet;
    }

    public void setValues(List<Value> valueList) {
        valueLocationMap.clear();
        for (Value value : valueList) {
            valueLocationMap.put(value, null);
        }
    }

    public java.util.Set<Value> getValues() {
        return valueLocationMap.keySet();
    }

    public void setLocation(Value value, Location location) {
        if (valueLocationMap.containsKey(value)) {
            valueLocationMap.put(value, location);
        }
    }

    public int getNumberOfItems() {
        return valueLocationMap.size();
    }



    public Location getLocation(Value value) {
        return valueLocationMap.get(value);
    }

    public Set getSelectedSet() {
        return selectedSet;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public PackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType;
    }

    public int getPins() {
        return pins;
    }

    public void setPins(int pins) {
        this.pins = pins;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isKeepOldSetItems() {
        return keepOldSetItems;
    }

    public void setKeepOldSetItems(boolean keepOldSetItems) {
        this.keepOldSetItems = keepOldSetItems;
    }

    public boolean isReplaceValues() {
        return replaceValues;
    }

    public void setReplaceValues(boolean replaceValues) {
        this.replaceValues = replaceValues;
    }

    public String getTypeName() {
        if (typeName == null) {
            typeName = "";
        }
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getNumberOfLocations() {
        return numberOfLocations;
    }

    public void setNumberOfLocations(int numberOfLocations) {
        this.numberOfLocations = numberOfLocations;
    }
}
