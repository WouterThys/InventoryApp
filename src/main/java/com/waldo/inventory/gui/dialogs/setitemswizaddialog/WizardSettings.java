package com.waldo.inventory.gui.dialogs.setitemswizaddialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.*;

import java.util.ArrayList;
import java.util.List;

public class WizardSettings {

    private final Set selectedSet;

    private List<Item> itemList = new ArrayList<>();
    private int numberOfLocations;

    private Manufacturer manufacturer;

    private PackageType packageType;
    private int pins;

    private int amount;
    private String typeName;
    private boolean keepOldSetItems;
    private boolean replaceValues;
    private boolean overWriteLocations;

    WizardSettings(Set selectedSet) {
        this.selectedSet = selectedSet;
    }

    public void setItems(List<Item> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        this.itemList.sort(new ComparatorUtils.ItemValueComparator());
    }

    public List<Item> getItems() {
        return itemList;
    }

    public void setLocation(Item item, Location location) {
        int ndx = itemList.indexOf(item);
        if (ndx >= 0) {
            itemList.get(ndx).putLocation(location);
        }
    }

    public int getNumberOfItems() {
        return itemList.size();
    }



    public Location getLocation(Item item) {
        int ndx = itemList.indexOf(item);
        if (ndx >= 0) {
            itemList.get(ndx).getLocation();
        }
        return null;
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

    public boolean isOverWriteLocations() {
        return overWriteLocations;
    }

    public void setOverWriteLocations(boolean overWriteLocations) {
        this.overWriteLocations = overWriteLocations;
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
