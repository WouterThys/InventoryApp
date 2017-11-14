package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.LogManager;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.managers.SearchManager.sm;

public class Item extends DbObject {

    private static final LogManager LOG = LogManager.LOG(Item.class);
    public static final String TABLE_NAME = "items";

    private Value value;
    private String description = "";
    private double price = 0;

    private long categoryId = -1;
    private Category category;
    private long productId = -1;
    private Product product;
    private long typeId = -1;
    private Type type;

    private String localDataSheet = "";
    private String onlineDataSheet = "";

    private long manufacturerId = -1;
    private Manufacturer manufacturer;
    private long locationId = UNKNOWN_ID;
    private Location location;
    private int amount = 0;
    private int amountType = Statics.ItemAmountTypes.NONE;
    private int orderState = Statics.ItemOrderStates.NONE;

    private long packageTypeId = UNKNOWN_ID;
    private PackageType packageType;
    private int pins;

    private float rating;
    private boolean discourageOrder;
    private String remarks;

    private boolean isSet;
    private List<SetItem> setItems;

    public Item() {
        super(TABLE_NAME);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;
        statement.setString(ndx++, name);

        statement.setString(ndx++, description);
        statement.setDouble(ndx++, price);
        if (categoryId < UNKNOWN_ID) {
            categoryId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, categoryId);
        if (productId < UNKNOWN_ID) {
            productId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, productId);
        if (typeId < UNKNOWN_ID) {
            typeId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, typeId);
        statement.setString(ndx++, localDataSheet);
        statement.setString(ndx++, onlineDataSheet);
        statement.setString(ndx++, iconPath);
        if (manufacturerId < UNKNOWN_ID) {
            manufacturerId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, manufacturerId);
        if (locationId < UNKNOWN_ID) {
            locationId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, locationId);
        statement.setInt( ndx++, amount);
        statement.setInt( ndx++, amountType);
        statement.setInt( ndx++, orderState);
        if (packageTypeId < UNKNOWN_ID) {
            packageTypeId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, packageTypeId); // PackageId
        statement.setInt(ndx++, getPins());
        statement.setFloat(ndx++, rating);
        statement.setBoolean(ndx++, discourageOrder);
        statement.setString(ndx++, getRemarks());
        statement.setBoolean(ndx++, isSet());

        // Value
        statement.setDouble(ndx++, getValue().getDoubleValue());
        statement.setInt(ndx++, getValue().getMultiplier());
        statement.setString(ndx++, getValue().getDbUnit());

        // Aud
        statement.setString(ndx++, getAud().getInsertedBy());
        statement.setTimestamp(ndx++, new Timestamp(getAud().getInsertedDate().getTime()), Calendar.getInstance());
        statement.setString(ndx++, getAud().getUpdatedBy());
        statement.setTimestamp(ndx++, new Timestamp(getAud().getUpdatedDate().getTime()), Calendar.getInstance());

        return ndx;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            // Local objects
            if (getDescription().toUpperCase().contains(searchTerm)) {
                return true;
            } else if (getLocalDataSheet().toUpperCase().contains(searchTerm)) {
                return true;
            } else if (getOnlineDataSheet().toUpperCase().contains(searchTerm)) {
                return true;
            }

            if (getValue().toString().equals(searchTerm)) {
                return true;
            }

            // Covert category, product, type, ...
            Category c = sm().findCategoryById(categoryId);
            if (c != null && c.hasMatch(searchTerm)) {
                return true;
            }

            Product p = sm().findProductById(productId);
            if (p != null && p.hasMatch(searchTerm)) {
                return true;
            }

            Type t = sm().findTypeById(typeId);
            if (t != null && t.hasMatch(searchTerm)) {
                return true;
            }

            Manufacturer m = sm().findManufacturerById(manufacturerId);
            if (m != null && m.hasMatch(searchTerm)) {
                return true;
            }

            Location l = sm().findLocationById(locationId);
            if (l != null && l.hasMatch(searchTerm)) {
                return true;
            }

            PackageType pa = sm().findPackageTypeById(packageTypeId);
            if (pa != null && pa.hasMatch(searchTerm)) {
                return true;
            }

        }
        return false;
    }

        @Override
    public Item createCopy(DbObject copyInto) {
        Item item = (Item) copyInto;
        copyBaseFields(item);

        item.setValue(Value.copy(getValue()));
        item.setDescription(getDescription());
        item.setPrice(getPrice());
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
        item.setRemarks(getRemarks());
        item.setSet(isSet());

        return item;
    }

    @Override
    public Item createCopy() {
        return createCopy(new Item());
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof Item)) {
                return false;
            } else {
                Item ref = (Item) obj;
                if (!(ref.getValue().equals(getValue()))) {System.out.println("Value differs"); return false; }
                if (!(ref.getIconPath().equals(getIconPath()))) { System.out.println("IconPath differs"); return false; }
                if (!(ref.getDescription().equals(getDescription()))) { System.out.println("Description differs"); return false; }
                if (!(ref.getPrice() == getPrice())) { System.out.println("Price differs"); return false; }
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
                if (!(ref.getRemarks().equals(getRemarks()))) { System.out.println("Remarks differs"); return false; }
                if (!(ref.isSet() == isSet())) { System.out.println("Is set differs"); return false; }
            }
        }
        return result;
    }

    public static Item getUnknownItem() {
        Item unknown = new Item();
        unknown.setId(UNKNOWN_ID);
        unknown.setName(UNKNOWN_NAME);
        unknown.setCanBeSaved(false);
        unknown.setInserted(true);
        return unknown;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Item> list = db().getItems();
                if (!list.contains(this)) {
                    list.add(this);
                }
                db().notifyListeners(DbManager.OBJECT_INSERT, this, db().onItemsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                db().notifyListeners(DbManager.OBJECT_UPDATE, this, db().onItemsChangedListenerList);
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Item> list = db().getItems();
                if (list.contains(this)) {
                    list.remove(this);
                }
                db().notifyListeners(DbManager.OBJECT_DELETE, this, db().onItemsChangedListenerList);
                break;
            }
        }
    }

    public void updateOrderState() {
        int currentState = getOrderState();
        Order lastOrderForItem = SearchManager.sm().findLastOrderForItem(id);
        if (lastOrderForItem == null) {
            orderState = Statics.ItemOrderStates.NONE;
        } else {
            if (lastOrderForItem.isOrdered() && lastOrderForItem.isReceived()) orderState = Statics.ItemOrderStates.NONE;
            else if (lastOrderForItem.isOrdered() && !lastOrderForItem.isReceived()) orderState = Statics.ItemOrderStates.ORDERED;
            else if (!lastOrderForItem.isOrdered() && !lastOrderForItem.isReceived()) orderState = Statics.ItemOrderStates.PLANNED;
            else orderState = Statics.ItemOrderStates.NONE;
        }
        if (currentState != getOrderState()) {
            save();
        }
    }

    public String getDescription() {
        if (description == null) {
            setDescription("");
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public Category getCategory() {
        if (category == null) {
            category = sm().findCategoryById(categoryId);
        }
        return category;
    }

    public void setCategoryId(long categoryId) {
        category = null;
        this.categoryId = categoryId;
    }

    public long getProductId() {
        return productId;
    }

    public Product getProduct() {
        if (product == null) {
            product = sm().findProductById(productId);
        }
        return product;
    }

    public void setProductId(long productId) {
        product = null;
        this.productId = productId;
    }

    public long getTypeId() {
        return typeId;
    }

    public Type getType() {
        if (type == null) {
            type = sm().findTypeById(typeId);
        }
        return type;
    }

    public void setTypeId(long typeId) {
        type = null;
        this.typeId = typeId;
    }

    public String getLocalDataSheet() {
        if (localDataSheet == null) {
            localDataSheet = "";
        }
        return localDataSheet;
    }

    public void setLocalDataSheet(String localDataSheet) {
        this.localDataSheet = localDataSheet;
    }

    public String getOnlineDataSheet() {
        if (onlineDataSheet == null) {
            onlineDataSheet = "";
        }
        return onlineDataSheet;
    }

    public void setOnlineDataSheet(String onlineDataSheet) {
        this.onlineDataSheet = onlineDataSheet;
    }

    public long getManufacturerId() {
        return manufacturerId;
    }

    public Manufacturer getManufacturer() {
        if (manufacturer == null) {
            manufacturer = sm().findManufacturerById(manufacturerId);
        }
        return manufacturer;
    }

    public void setManufacturerId(long manufacturerId) {
        manufacturer = null;
        this.manufacturerId = manufacturerId;
    }

    public long getLocationId() {
        if (isSet()) {
            // Check if set has locations
            for (SetItem setItem : getSetItems()) {
                if (setItem.getLocationId() > UNKNOWN_ID) {
                    return -1; // If one of the SetItems has a location, the item has no location
                }
            }
        }
        return locationId;
    }

    public void setLocationId(long locationId) {
        location = null;
        this.locationId = locationId;
    }

    public Location getLocation() {
        if (location == null) {
            location = SearchManager.sm().findLocationById(getLocationId());
        }
        return location;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmountType() {
        return amountType;
    }

    public void setAmountType(int amountType) {
        this.amountType = amountType;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isDiscourageOrder() {
        return discourageOrder;
    }

    public void setDiscourageOrder(boolean discourageOrder) {
        this.discourageOrder = discourageOrder;
    }

    public String getRemarks() {
        if (remarks == null) {
            remarks = "";
        }
        return remarks;
    }


    public long getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(long packageTypeId) {
        packageType = null;
        this.packageTypeId = packageTypeId;
    }

    public int getPins() {
        if (getPackageType() != null) {
            if (!packageType.isAllowOtherPinNumbers()) {
                pins = packageType.getDefaultPins();
            }
        }
        return pins;
    }

    public void setPins(int pins) {
        if (getPackageType() != null) {
            if (packageType.isAllowOtherPinNumbers()) {
                this.pins = pins;
            }
        } else {
            this.pins = pins;
        }
    }

    public PackageType getPackageType() {
        if (packageType == null) {
            packageType = sm().findPackageTypeById(packageTypeId);
        }
        return packageType;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public List<SetItem> getSetItems() {
        if (isSet()) {
            if (setItems == null) {
                setItems = SearchManager.sm().findSetItemsByItemId(getId());
            }
            return setItems;
        }
        return null;
    }

    public Value getValue() {
        if (value == null) {
            value = new Value();
        }
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public void setValue(double value, int multiplier, String unit) {
        this.value = new Value(value, multiplier, unit);
    }
}
