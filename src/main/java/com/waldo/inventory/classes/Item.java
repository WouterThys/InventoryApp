package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.LogManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;

import static com.waldo.inventory.database.SearchManager.sm;

public class Item extends DbObject {

    private static final LogManager LOG = LogManager.LOG(Item.class);
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
    private int orderState = Statics.ItemOrderStates.NONE;
    private long packageTypeId = -1;
    private int pins;
    private double width, height;
    private float rating;
    private boolean discourageOrder;
    private String remarks;

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
        statement.setDouble(17, getWidth());
        statement.setDouble(18, getHeight());
        statement.setFloat(19, rating);
        statement.setBoolean(20, discourageOrder);
        statement.setString(21, getRemarks());
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
        statement.setDouble(17, getWidth());
        statement.setDouble(18, getHeight());
        statement.setFloat(19, rating);
        statement.setBoolean(20, discourageOrder);
        statement.setString(21, getRemarks());

        statement.setLong(22, id); // WHERE id
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

            PackageType pt = sm().findPackageTypeById(packageTypeId);
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
        item.setPackageTypeId(getPackageTypeId());
        item.setPins(getPins());
        item.setWidth(getWidth());
        item.setHeight(getHeight());
        item.setRating(getRating());
        item.setDiscourageOrder(isDiscourageOrder());
        item.setRemarks(getRemarks());

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
                if (!(ref.getDescription().equals(getDescription()))) { return false; }
                if (!(ref.getPrice() == getPrice())) return false;
                if (!(ref.getCategoryId() == getCategoryId())) return false;
                if (!(ref.getProductId() == getProductId())) return false;
                if (!(ref.getTypeId() == getTypeId())) return false;
                if (!(ref.getLocalDataSheet().equals(getLocalDataSheet()))) return false;
                if (!(ref.getOnlineDataSheet().equals(getOnlineDataSheet()))) return false;
                if (!(ref.getManufacturerId() == getManufacturerId())) return false;
                if (!(ref.getLocationId() == getLocationId())) return false;
                if (!(ref.getAmount() == getAmount())) return false;
                if (!(ref.getAmountType() == getAmountType())) return false;
                if (!(ref.getOrderState() == getOrderState())) return false;
                if (!(ref.getPackageTypeId() == getPackageTypeId())) return false;
                if (!(ref.getPins() == getPins())) return false;
                if (!(ref.getWidth() == getWidth())) return false;
                if (!(ref.getHeight() == getHeight())) return false;
                if (!(ref.getRating() == getRating())) return false;
                if (!(ref.isDiscourageOrder() == isDiscourageOrder())) return false;
                if (!(ref.getRemarks().equals(getRemarks()))) return false;
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

    public void setPrice(String price) {
        try {
            if (!price.isEmpty()) {
                this.price = Double.valueOf(price);
            }
        } catch (Exception e) {
            LOG.error("Error setting price.", e);
        }
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryId(String categoryId) {
        try {
            this.categoryId = Long.valueOf(categoryId);
        } catch (Exception e) {
            LOG.error("Error setting category id.", e);
        }
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public void setProductId(String productId) {
        try {
            this.productId = Long.valueOf(productId);
        } catch (Exception e) {
            LOG.error("Error setting product id.", e);
        }
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public void setTypeId(String typeId) {
        try {
            this.typeId = Long.valueOf(typeId);
        } catch (Exception e) {
            LOG.error("Error setting type id.", e);
        }
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

    public void setManufacturerId(String manufacturerId) {
        try {
            this.manufacturerId = Long.valueOf(manufacturerId);
        } catch (Exception e) {
            LOG.error("Error setting manufacturer id.", e);
        }
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

    public void setAmount(String amount) {
        try {
            this.amount = Integer.valueOf(amount);
        } catch (Exception e) {
            LOG.error("Error setting amount.", e);
        }
    }

    public int getAmountType() {
        return amountType;
    }

    public void setAmountType(int amountType) {
        this.amountType = amountType;
    }

    public void setAmountType(String amountType) {
        try {
            this.amountType = Integer.valueOf(amountType);
        } catch (Exception e) {
            LOG.error("Error setting amount type.", e);
        }
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public void setOrderState(String orderState) {
        try {
            this.orderState = Integer.valueOf(orderState);
        } catch (Exception e) {
            LOG.error("Error setting order state.", e);
        }
    }

    public long getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(long packageTypeId) {
        this.packageTypeId = packageTypeId;
    }

    public void setPackageTypeId(String packageTypeId) {
        try {
            this.packageTypeId = Long.valueOf(packageTypeId);
        } catch (Exception e) {
            LOG.error("Error setting package type.", e);
        }
    }

    public int getPins() {
        return pins;
    }

    public void setPins(int pins) {
        this.pins = pins;
    }

    public void setPins(String pins) {
        try {
            this.pins = Integer.valueOf(pins);
        } catch (Exception e) {
            LOG.error("Error setting pins.", e);
        }
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setWidth(String width) {
        try {
            if (!width.isEmpty()) {
                this.width = Integer.valueOf(width);
            }
        } catch (Exception e) {
            LOG.error("Error setting width.", e);
        }
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setHeight(String height) {
        try {
            if (!height.isEmpty()) {
                this.height = Integer.valueOf(height);
            }
        } catch (Exception e) {
            LOG.error("Error setting height.", e);
        }
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setRating(String rating) {
        try {
            if (!rating.isEmpty()) {
                this.rating = Float.valueOf(rating);
            }
        } catch (Exception e) {
            LOG.error("Error setting rating.", e);
        }
    }

    public boolean isDiscourageOrder() {
        return discourageOrder;
    }

    public String getDiscourageOrder() {
        return String.valueOf(discourageOrder);
    }

    public void setDiscourageOrder(boolean discourageOrder) {
        this.discourageOrder = discourageOrder;
    }

    public void setDiscourageOrder(String discourageOrder) {
        try {
            this.discourageOrder = Boolean.parseBoolean(discourageOrder);
        } catch (Exception e) {
            LOG.error("Error setting iscourage order", e);
        }
    }

    public String getRemarks() {
        if (remarks == null) {
            remarks = "";
        }
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
