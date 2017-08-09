package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.database.SearchManager.sm;

public class Item extends DbObject {

    private static final LogManager LOG = LogManager.LOG(Item.class);
    public static final String TABLE_NAME = "items";

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
    private long locationId = -1;
    private Location location;
    private int amount = 0;
    private int amountType = Statics.ItemAmountTypes.NONE;
    private int orderState = Statics.ItemOrderStates.NONE;

    private long packageId = UNKNOWN_ID;
    private Package itemPackage;
    private long dimensionTypeId = -1;
    private DimensionType dimensionType;

    private float rating;
    private boolean discourageOrder;
    private String remarks;

    private boolean isSet;

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
        if (packageId < UNKNOWN_ID) {
            packageId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, packageId); // PackageId
        statement.setFloat(ndx++, rating);
        statement.setBoolean(ndx++, discourageOrder);
        statement.setString(ndx++, getRemarks());
        statement.setBoolean(ndx++, isSet());
        if (dimensionTypeId < UNKNOWN_ID) {
            dimensionTypeId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, getDimensionTypeId());

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

            Package pa = sm().findPackageById(packageId);
            if (pa != null && pa.hasMatch(searchTerm)) {
                return true;
            }

            if (getDimensionType() != null && getDimensionType().hasMatch(searchTerm)) {
                return true;
            }

        }
        return false;
    }

        @Override
    public Item createCopy(DbObject copyInto) {
        Item item = (Item) copyInto;
        copyBaseFields(item);

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
        item.setPackageId(getPackageId());
        item.setRating(getRating());
        item.setDiscourageOrder(isDiscourageOrder());
        item.setRemarks(getRemarks());
        item.setSet(isSet());
        item.setDimensionTypeId(getDimensionTypeId());

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
                if (!(ref.getPackageId() == getPackageId())) { System.out.println("Package differs"); return false; }
                if (!(ref.getRating() == getRating())) { System.out.println("Rating differs"); return false; }
                if (!(ref.isDiscourageOrder() == isDiscourageOrder())) { System.out.println("Discourage differs"); return false; }
                if (!(ref.getRemarks().equals(getRemarks()))) { System.out.println("Remarks differs"); return false; }
                if (!(ref.isSet() == isSet())) { System.out.println("Is set differs"); return false; }
                if (!(ref.getDimensionTypeId() == getDimensionTypeId())) {
                    System.out.println("Dimension differs: ref id:" + ref.getDimensionTypeId() + " this id: " + getDimensionTypeId());
                    return false;
                }
            }
        }
        return result;
    }

    public static class ItemComparator implements Comparator<Item> {
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
        return locationId;
    }

    public void setLocationId(long locationId) {
        location = null;
        this.locationId = locationId;
    }

    public Location getLocation() {
        if (location == null) {
            location = SearchManager.sm().findLocationById(locationId);
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


    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        itemPackage = null;
        this.packageId = packageId;
    }

    public Package getPackage() {
        if (itemPackage == null) {
            itemPackage = sm().findPackageById(packageId);
        }
        return itemPackage;
    }

    public double getWidth() {
        if (getPackage() != null) {
            return getPackage().getWidth();
        }
        return 0;
    }

    public void setWidth(double width) {
        if (getPackage() != null) {
            getPackage().setWidth(width);
        } else {
            itemPackage = new Package();
            itemPackage.setWidth(width);
        }
    }

    public double getHeight() {
        if (getPackage() != null) {
            return getPackage().getHeight();
        }
        return 0;
    }

    public void setHeight(double height) {
        if (getPackage() != null) {
            getPackage().setHeight(height);
        } else {
            itemPackage = new Package();
            itemPackage.setHeight(height);
        }
    }

    public int getPins() {
        if (getPackage() != null) {
            return getPackage().getPins();
        }
        return 0;
    }

    public void setPins(int pins) {
        if (getPackage() != null) {
            getPackage().setPins(pins);
        } else {
            itemPackage = new Package();
            itemPackage.setPins(pins);
        }
    }

    public long getPackageTypeId() {
        if (getPackage() != null) {
            return getPackage().getPackageTypeId();
        }
        return UNKNOWN_ID;
    }

    public void setPackageTypeId(long packageTypeId) {
        if (getPackage() != null) {
            getPackage().setPackageTypeId(packageTypeId);
        } else {
            itemPackage = new Package();
            itemPackage.setPackageTypeId(packageTypeId);
        }
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

    public long getDimensionTypeId() {
        if (dimensionTypeId < UNKNOWN_ID) {
            dimensionTypeId = UNKNOWN_ID;
        }
        return dimensionTypeId;
    }

    public void setDimensionTypeId(long dimensionTypeId) {
        dimensionType = null;
        this.dimensionTypeId = dimensionTypeId;
    }

    public DimensionType getDimensionType() {
        if (dimensionType == null) {
            if (dimensionTypeId > DbObject.UNKNOWN_ID) {
                dimensionType = sm().findDimensionTypeById(dimensionTypeId);
            }
        }
        return dimensionType;
    }
}
