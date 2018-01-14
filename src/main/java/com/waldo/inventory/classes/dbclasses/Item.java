package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class Item extends DbObject {

    public static final String TABLE_NAME = "items";

    protected String alias;
    protected Value value;
    protected String description = "";
    protected double price = 0;

    protected long categoryId = -1;
    protected Category category;
    protected long productId = -1;
    protected Product product;
    protected long typeId = -1;
    protected Type type;
    protected boolean isSet = false;

    protected String localDataSheet = "";
    protected String onlineDataSheet = "";

    protected long manufacturerId = -1;
    protected Manufacturer manufacturer;
    protected long locationId = UNKNOWN_ID;
    protected Location location;
    protected int amount = 0;
    protected int amountType = Statics.ItemAmountTypes.NONE;
    protected int orderState = Statics.ItemOrderStates.NONE;

    protected long packageTypeId = UNKNOWN_ID;
    protected PackageType packageType;
    protected int pins;

    protected float rating;
    protected boolean discourageOrder;
    protected String remarksFile;

    public Item() {
        this("");
    }

    public Item(String name) {
        super(TABLE_NAME);
        matchCount = 13;
        setName(name);
    }

    public Item(String name, String alias, Value value, Manufacturer manufacturer, PackageType packageType, int pins, int amount, Location location, Set set) {
        this(name);
        if (value == null) {
            value = new Value();
        }
        this.value = value;
        this.manufacturer = manufacturer;
        this.location = location;
        this.packageType = packageType;
        this.pins = pins;
        this.amount = amount;
        this.alias = alias;
        this.description = alias + " " + value.toString();

        if (manufacturer != null) manufacturerId = manufacturer.getId();
        if (location != null) locationId = location.getId();
        if (packageType != null) packageTypeId = packageType.getId();

        if (set != null) {
            this.rating = set.getRating();
            this.category = set.getCategory();
            this.product = set.getProduct();
            this.type = set.getType();

            if (category != null) categoryId = category.getId();
            if (product != null) productId = product.getId();
            if (type != null) typeId = type.getId();
        }
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;
        statement.setString(ndx++, name);
        statement.setString(ndx++, alias);
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
        // Remarks
        SerialBlob blob = FileUtils.fileToBlob(getRemarksFile());
        if (blob != null) {
            statement.setBlob(ndx++, blob);
        } else {
            statement.setString(ndx++, null);
        }

        // Set items
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
    protected int findMatch(String searchTerm) {
        if (this.isUnknown()) return 0;
        getObjectMatch().setMatchCount(matchCount);
        int match = super.findMatch(searchTerm);


        // Local objects
        if (getAlias().toUpperCase().contains(searchTerm)) match ++;
        if (getDescription().toUpperCase().contains(searchTerm)) match++;
        if (getLocalDataSheet().toUpperCase().contains(searchTerm)) match++;
        if (getOnlineDataSheet().toUpperCase().contains(searchTerm)) match++;
        if (getValue().toString().equals(searchTerm)) match++;

        // Covert category, product, type, ...
        Category c = sm().findCategoryById(categoryId);
        if (c != null && c.hasMatch(searchTerm)) match++;

        Product p = sm().findProductById(productId);
        if (p != null && p.hasMatch(searchTerm)) match++;

        Type t = sm().findTypeById(typeId);
        if (t != null && t.hasMatch(searchTerm)) match++;

        Manufacturer m = sm().findManufacturerById(manufacturerId);
        if (m != null && m.hasMatch(searchTerm)) match++;

        Location l = sm().findLocationById(locationId);
        if (l != null && l.hasMatch(searchTerm)) match++;

        PackageType pa = sm().findPackageTypeById(packageTypeId);
        if (pa != null && pa.hasMatch(searchTerm)) match++;

        return match;
    }

    @Override
    protected int findMatch(DbObject dbObject) {
        if (this.isUnknown()) return 0;
        int match = 0;
        if (dbObject != null && dbObject instanceof PcbItemProjectLink) {
            getObjectMatch().setMatchCount(3);

            PcbItemProjectLink projectLink = (PcbItemProjectLink) dbObject;
            PcbItem pcbItem = projectLink.getPcbItem();

            String pcbName = pcbItem.getPartName().toUpperCase();
            String pcbValue = projectLink.getValue();
            String pcbFp = pcbItem.getFootprint();

            ParserItemLink parserLink = SearchManager.sm().findParserItemLinkByPcbItemName(pcbName);
            if (parserLink != null) {

                if (parserLink.hasCategory()) {
                    if (getCategoryId() != parserLink.getCategoryId()) {
                        return 0;
                    }
                }

                if (parserLink.hasProduct()) {
                    if (getProductId() != parserLink.getProductId()) {
                        return 0;
                    }
                }

                if (parserLink.hasType()) {
                    if (getTypeId() != parserLink.getTypeId()) {
                        return 0;
                    }
                }
            }

                String itemName = getName().toUpperCase();
                String itemAlias = getAlias().toUpperCase();

                if(PcbItem.matchesName(pcbName, itemName) || PcbItem.matchesAlias(pcbName, itemAlias)) match++;
                if (getValue().hasValue()) {
                    if (PcbItem.matchesValue(pcbValue, getValue(), itemName)) match++;
                } else {
                    if(PcbItem.matchesName(pcbName, itemName)) match++;
                }

                // Only check footprint match if there is already a match
                if (match > 0 && getPackageTypeId() > UNKNOWN_ID) {
                    if(PcbItem.matchesFootprint(pcbFp, getPackageType())) match++;
                }

        }
        return match;
    }

    @Override
    public Item createCopy(DbObject copyInto) {
        Item item = (Item) copyInto;
        copyBaseFields(item);

        item.setAlias(getAlias());
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
        item.setRemarksFile(getRemarksFile());
        item.setIsSet(isSet());

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
                if (!(ref.getAlias().equals(getAlias()))) {System.out.println("Alias differs"); return false; }
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
                if (!(ref.isSet() == isSet())) { System.out.println("IsSet differs"); return false; }
            }
        }
        return result;
    }

    public String createRemarksFileName() {
        return getId() + "_ItemObject_";
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                    List<Item> list = cache().getItems();
                    if (!list.contains(this)) {
                        list.add(this);
                    }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                    List<Item> list = cache().getItems();
                    if (list.contains(this)) {
                        list.remove(this);
                    }

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

    public String getAlias() {
        if (alias == null) {
            alias = "";
        }
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
        if (category != null && category.getId() != categoryId) {
            category = null;
        }
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
        if (product != null && product.getId() != productId) {
            product = null;
        }
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
        if (type != null && type.getId() != typeId) {
            type = null;
        }
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
        if (manufacturer != null && manufacturer.getId() != manufacturerId) {
            manufacturer = null;
        }
        this.manufacturerId = manufacturerId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        if (location != null && location.getId() != locationId) {
            location = null;
        }
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

    public File getRemarksFile() {
        if (remarksFile != null && !remarksFile.isEmpty()) {
            return new File(remarksFile);
        }
        return null;
    }

    public void setRemarksFile(File remarksFile) {
        if (remarksFile != null && remarksFile.exists()) {
            this.remarksFile = remarksFile.getAbsolutePath();
        }
    }

    public long getPackageTypeId() {
        return packageTypeId;
    }

    public void setPackageTypeId(long packageTypeId) {
        if (packageType != null && packageType.getId() != packageTypeId) {
            packageType = null;
        }
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

    public boolean isSetItem() {
        return SearchManager.sm().findSetsByItemId(getId()).size() > 0;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setIsSet(boolean isSet) {
        this.isSet = isSet;
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
