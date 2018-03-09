package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.classes.Value;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Set extends Item {
    // Variables
    private List<Item> setItems;

    public Set() {

    }

    public Set(String name) {
        setName(name);
    }

    @Override
    public boolean isSet() {
        return true;
    }

    @Override
    public Set createCopy(DbObject copyInto) {
        Set item = (Set) copyInto;
        copyBaseFields(item);

        item.setValue(Value.copy(getValue()));
        item.setDescription(getDescription());
        item.setCategoryId(getCategoryId());
        item.setProductId(getProductId());
        item.setTypeId(getTypeId());
        item.setLocalDataSheet(getLocalDataSheet());
        item.setOnlineDataSheet(getOnlineDataSheet());
        item.setManufacturerId(getManufacturerId());
        item.setLocationId(getLocationId());
        item.setAmount(getAmount());
        item.setAmountType(getAmountType());
        item.setOrderState(getOrderState());
        item.setPackageTypeId(getPackageTypeId());
        item.setPins(getPins());
        item.setRating(getRating());
        item.setDiscourageOrder(isDiscourageOrder());
        item.setRemarksFile(getRemarksFile());

        return item;
    }

    @Override
    public Set createCopy() {
        return createCopy(new Set());
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof Set)) {
                return false;
            } else {
                Set ref = (Set) obj;
                if (!(ref.getValue().equals(getValue()))) {System.out.println("Value differs"); return false; }
                if (!(ref.getIconPath().equals(getIconPath()))) { System.out.println("IconPath differs"); return false; }
                if (!(ref.getDescription().equals(getDescription()))) { System.out.println("Description differs"); return false; }
                if (!(ref.getCategoryId() == getCategoryId())) { System.out.println("Category differs"); return false; }
                if (!(ref.getProductId() == getProductId())) { System.out.println("Product differs"); return false; }
                if (!(ref.getTypeId() == getTypeId())) { System.out.println("Type differs"); return false; }
                if (!(ref.getLocalDataSheet().equals(getLocalDataSheet()))) { System.out.println("Local datasheet differs"); return false; }
                if (!(ref.getOnlineDataSheet().equals(getOnlineDataSheet()))) { System.out.println("Online datasheet differs"); return false; }
                if (!(ref.getManufacturerId() == getManufacturerId())) { System.out.println("Manufacturer differs"); return false; }
                if (!(ref.getLocationId() == getLocationId())) { System.out.println("Location differs"); return false; }
                if (!(ref.getAmount() == getAmount())) { System.out.println("Amount differs"); return false; }
                if (!(ref.getAmountType() == getAmountType())) { System.out.println("Amount type differs"); return false; }
                if (!(ref.getOrderState() == getOrderState())) { System.out.println("Order state differs"); return false; }
                if (!(ref.getPackageTypeId() == getPackageTypeId())) {
                    System.out.println("Package type differs: " + ref.getPackageTypeId() + "<->" + getPackageTypeId());
                    return false; }
                if (!(ref.getPins() == getPins())) { System.out.println("Pins differ"); return false; }
                if (!(ref.getRating() == getRating())) { System.out.println("Rating differs"); return false; }
                if (!(ref.isDiscourageOrder() == isDiscourageOrder())) { System.out.println("Discourage differs"); return false; }
                if (!(ref.isSet() == isSet())) { System.out.println("IsSet differs"); return false; }
            }
        }
        return result;
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {

                    List<Set> list = cache().getSets();
                    if (!list.contains(this)) {
                        list.add(this);
                    }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                    List<Set> list = cache().getSets();
                    if (list.contains(this)) {
                        list.remove(this);
                    }

                break;
            }
        }
    }

    public boolean addSetItem(Item setItem) {
        boolean result = false;
        if (setItem != null && !getSetItems().contains(setItem)) {
            SetItemLink link = new SetItemLink(this, setItem);
            link.save();
            setItems.add(setItem);
            result = true;
        }
        return result;
    }

    public void removeSetItem(Item setItem) {
        if (setItem != null) {
            SetItemLink link = SearchManager.sm().findSetItemLinkBySetAndItem(getId(), setItem.getId());
            if (link != null) {
                link.delete();
                setItems.remove(setItem);
            }
        }
    }

    public void updateSetItems() {
        setItems = null;
    }

    // Getters and setters
    public List<Item> getSetItems() {
        if (setItems == null) {
            setItems = SearchManager.sm().findSetItemsBySetId(getId());
        }
        return setItems;
    }
}