package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.DbManager;

import java.sql.*;
import java.util.Comparator;

public class Item extends DbObject {

    public static final String TABLE_NAME = "items";

    private String description = "";
    private double price = 0;

    private long categoryId = -1;
    private long productId = -1;
    private long typeId = -1;
    private String localDataSheet = "";
    private String onlineDataSheet = "";
    private long manufacturerId = -1;
    private long locationId = -1;
    private int amount = 0;
    private int amountType = Statics.ItemAmountTypes.NONE;
    private int orderState = Statics.ItemOrderState.NONE;
    private long packageTypeId = -1;
    private int pins;
    private double width, height;

    public Item() {
        super(TABLE_NAME);
    }

    @Override
    protected void insert(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(9, iconPath);
        statement.setString(2, description);
        statement.setDouble(3, price);
        if (categoryId < UNKNOWN_ID) {
            categoryId = UNKNOWN_ID;
        }
        statement.setLong(4, categoryId);
        if (productId < UNKNOWN_ID) {
            productId = UNKNOWN_ID;
        }
        statement.setLong(5, productId);
        if (typeId < UNKNOWN_ID) {
            typeId = UNKNOWN_ID;
        }
        statement.setLong(6, typeId);
        statement.setString(7, localDataSheet);
        statement.setString(8, onlineDataSheet);
        if (manufacturerId < UNKNOWN_ID) {
            manufacturerId = UNKNOWN_ID;
        }
        statement.setLong(10, manufacturerId);
        if (locationId < UNKNOWN_ID) {
            locationId = UNKNOWN_ID;
        }
        statement.setLong(11, locationId);
        statement.setInt(12, amount);
        statement.setInt(13, amountType);
        statement.setInt(14, orderState);
        if (packageTypeId < UNKNOWN_ID) {
            packageTypeId = UNKNOWN_ID;
        }
        statement.setLong(15, packageTypeId);
        statement.setInt(16, pins);
        statement.setDouble(17, width);
        statement.setDouble(18, height);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(9, iconPath);
        statement.setString(2, description);
        statement.setDouble(3, price);
        if (categoryId < UNKNOWN_ID) {
            categoryId = UNKNOWN_ID;
        }
        statement.setLong(4, categoryId);
        if (productId < UNKNOWN_ID) {
            productId = UNKNOWN_ID;
        }
        statement.setLong(5, productId);
        if (typeId < UNKNOWN_ID) {
            typeId = UNKNOWN_ID;
        }
        statement.setLong(6, typeId);
        statement.setString(7, localDataSheet);
        statement.setString(8, onlineDataSheet);
        if (manufacturerId < UNKNOWN_ID) {
            manufacturerId = UNKNOWN_ID;
        }
        statement.setLong(10, manufacturerId);
        if (locationId < UNKNOWN_ID) {
            locationId = UNKNOWN_ID;
        }
        statement.setLong(11, locationId);
        statement.setInt(12, amount);
        statement.setInt(13, amountType);
        statement.setInt(14, orderState);
        if (packageTypeId < UNKNOWN_ID) {
            packageTypeId = UNKNOWN_ID;
        }
        statement.setLong(15, packageTypeId);
        statement.setInt(16, pins);
        statement.setDouble(17, width);
        statement.setDouble(18, height);

        statement.setLong(19, id); // WHERE id
        statement.execute();
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
            Category c = DbManager.db().findCategoryById(categoryId);
            if (c != null && c.hasMatch(searchTerm)) {
                return true;
            }

            Product p = DbManager.db().findProductById(productId);
            if (p != null && p.hasMatch(searchTerm)) {
                return true;
            }

            Type t = DbManager.db().findTypeById(typeId);
            if (t != null && t.hasMatch(searchTerm)) {
                return true;
            }

            Manufacturer m = DbManager.db().findManufacturerById(manufacturerId);
            if (m != null && m.hasMatch(searchTerm)) {
                return true;
            }

            Location l = DbManager.db().findLocationById(locationId);
            if (l != null && l.hasMatch(searchTerm)) {
                return true;
            }

            PackageType pt = DbManager.db().findPackageTypeByIndex(packageTypeId);
            if (pt != null && pt.hasMatch(searchTerm)) {
                return true;
            }

//            Order o = DbManager.db().findOrderById(orderId);
//            if (o != null && o.hasMatch(searchTerm)) {
//                return true
//            }

        }
        return false;
    }

    @Override
    public Item createCopy() {
        Item item = new Item();
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
        item.setPackageTypeId(getPackageTypeId());
        item.setPins(getPins());
        item.setWidth(getWidth());
        item.setHeight(getHeight());

        return item;
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

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
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

    public void setManufacturerId(long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
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

    public long getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(long packageTypeId) {
        this.packageTypeId = packageTypeId;
    }

    public int getPins() {
        return pins;
    }

    public void setPins(int pins) {
        this.pins = pins;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
