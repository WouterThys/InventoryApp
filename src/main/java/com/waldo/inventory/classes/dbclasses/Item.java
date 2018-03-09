package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.Statics.ItemAmountTypes;
import com.waldo.inventory.Utils.Statics.ItemOrderStates;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.FileUtils;

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

    protected long categoryId = UNKNOWN_ID;
    protected Category category;
    private long productId = UNKNOWN_ID;
    protected Product product;
    private long typeId = UNKNOWN_ID;
    protected Type type;
    private boolean isSet = false;

    private String localDataSheet = "";
    private String onlineDataSheet = "";

    private long manufacturerId = UNKNOWN_ID;
    protected Manufacturer manufacturer;
    private long locationId = UNKNOWN_ID;
    protected Location location;
    protected int amount = 0;
    private ItemAmountTypes amountType = ItemAmountTypes.Unknown;
    private ItemOrderStates orderState = ItemOrderStates.NoOrder;

    protected long packageTypeId = UNKNOWN_ID;
    private PackageType packageType;
    protected int pins;

    private float rating;
    private boolean discourageOrder;
    private String remarksFile;

    public Item() {
        this("");
    }

    public Item(String name) {
        super(TABLE_NAME);
        matchCount = 11;
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
        statement.setString(ndx++, getName());
        statement.setString(ndx++, getAlias());
        statement.setNString(ndx++, getDescription());
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
        statement.setInt(ndx++, amount);
        statement.setInt(ndx++, getAmountType().getValue());
        statement.setInt(ndx++, getOrderState().getValue());
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
        statement.setInt(ndx++, getValue().getMultiplier().getMultiplier());
        statement.setNString(ndx++, getValue().getUnit().toString());

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
        if (getAlias().toUpperCase().contains(searchTerm)) match++;
        if (getDescription().toUpperCase().contains(searchTerm)) match++;
        if (getLocalDataSheet().toUpperCase().contains(searchTerm)) match++;
        if (getOnlineDataSheet().toUpperCase().contains(searchTerm)) match++;
        if (getValue().toString().equals(searchTerm)) match++;

        // Covert category, product, type, ...
        if (getCategory() != null && category.hasMatch(searchTerm)) match++;
        if (getProduct() != null && product.hasMatch(searchTerm)) match++;
        if (getType() != null && type.hasMatch(searchTerm)) match++;
        if (getManufacturer() != null && manufacturer.hasMatch(searchTerm)) match++;
        if (getLocation() != null && location.hasMatch(searchTerm)) match++;
        if (getPackageType() != null && packageType.hasMatch(searchTerm)) match++;

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

            if (PcbItem.matchesName(pcbName, itemName) || PcbItem.matchesAlias(pcbName, itemAlias)) match++;
            if (getValue().hasValue()) {
                if (PcbItem.matchesValue(pcbValue, getValue())) match++;
            } else {
                if (PcbItem.matchesName(pcbName, itemName)) match++;
            }

            // Only check footprint match if there is already a match
            if (match > 0 && getPackageTypeId() > UNKNOWN_ID) {
                if (PcbItem.matchesFootprint(pcbFp, getPackageType())) match++;
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
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof Item)) {
                return false;
            } else {
                Item ref = (Item) obj;
                if (!(ref.getAlias().equals(getAlias()))) {
                    cache().clearAliases();
                    if (Main.DEBUG_MODE) System.out.println(ref.getAlias() + " != " + getAlias());
                    return false;
                }
                if (!(ref.getValue().equals(getValue()))) {
                    if (Main.DEBUG_MODE) System.out.println(ref.getValue() + " != " + getValue());
                    return false;
                }
                if (!(ref.getIconPath().equals(getIconPath()))) {
                    if (Main.DEBUG_MODE) System.out.println(ref.getIconPath() + " != " + getIconPath());
                    return false;
                }
                if (!(ref.getDescription().equals(getDescription()))) {
                    if (Main.DEBUG_MODE) System.out.println(ref.getDescription() + " != " + getDescription());
                    return false;
                }
                if (!(ref.getCategoryId() == getCategoryId())) {
                    if (Main.DEBUG_MODE) System.out.println("CategoryId: " + ref.getCategoryId() + " != " + getCategoryId());
                    return false;
                }
                if (!(ref.getProductId() == getProductId())) {
                    if (Main.DEBUG_MODE) System.out.println("ProductId: " + ref.getProductId() + " != " + getProductId());
                    return false;
                }
                if (!(ref.getTypeId() == getTypeId())) {
                    if (Main.DEBUG_MODE) System.out.println("TypeId: " + ref.getTypeId() + " != " + getTypeId());
                    return false;
                }
                if (!(ref.getLocalDataSheet().equals(getLocalDataSheet()))) {
                    if (Main.DEBUG_MODE) System.out.println(ref.getLocalDataSheet() + " != " + getLocalDataSheet());
                    return false;
                }
                if (!(ref.getOnlineDataSheet().equals(getOnlineDataSheet()))) {
                    if (Main.DEBUG_MODE) System.out.println(ref.getOnlineDataSheet() + " != " + getOnlineDataSheet());
                    return false;
                }
                if (!(ref.getManufacturerId() == getManufacturerId())) {
                    if (Main.DEBUG_MODE) System.out.println("Manuf. Id: " + ref.getManufacturerId() + " != " + getManufacturerId());
                    return false;
                }
                if (!(ref.getLocationId() == getLocationId())) {
                    if (Main.DEBUG_MODE) System.out.println("LocationId: " + ref.getLocationId() + " != " + getLocationId());
                    return false;
                }
                if (!(ref.getAmount() == getAmount())) {
                    if (Main.DEBUG_MODE) System.out.println("Amount: " + ref.getAmount() + " != " + getAmount());
                    return false;
                }
                if (!(ref.getAmountType() == getAmountType())) {
                    if (Main.DEBUG_MODE) System.out.println("Amount type: " + ref.getAmountType() + " != " + getAmountType());
                    return false;
                }
                if (!(ref.getOrderState() == getOrderState())) {
                    if (Main.DEBUG_MODE) System.out.println("Order st: " + ref.getOrderState() + " != " + getOrderState());
                    return false;
                }
                if (!(ref.getPackageTypeId() == getPackageTypeId())) {
                    if (Main.DEBUG_MODE) System.out.println("PackageTypeId: " + ref.getPackageTypeId() + " != " + getPackageTypeId());
                    return false;
                }
                if (!(ref.getPins() == getPins())) {
                    if (Main.DEBUG_MODE) System.out.println("Pins: " + ref.getPins() + " != " + getPins());
                    return false;
                }
                if (!(ref.getRating() == getRating())) {
                    if (Main.DEBUG_MODE) System.out.println("Rating: " + ref.getRating() + " != " + getRating());
                    return false;
                }
                if (!(ref.isDiscourageOrder() == isDiscourageOrder())) {
                    if (Main.DEBUG_MODE) System.out.println("Discourage: " + ref.isDiscourageOrder() + " != " + isDiscourageOrder());
                    return false;
                }
                if (!(ref.isSet() == isSet())) {
                    if (Main.DEBUG_MODE) System.out.println("Set: " + ref.isSet() + " != " + isSet());
                    return false;
                }
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
        ItemOrderStates currentState = getOrderState();
        Order lastOrderForItem = SearchManager.sm().findLastOrderForItem(id);
        if (lastOrderForItem == null) {
            orderState = ItemOrderStates.NoOrder;
        } else {
            if (lastOrderForItem.isOrdered() && lastOrderForItem.isReceived()) orderState = ItemOrderStates.NoOrder;
            else if (lastOrderForItem.isOrdered() && !lastOrderForItem.isReceived())
                orderState = ItemOrderStates.Ordered;
            else if (!lastOrderForItem.isOrdered() && !lastOrderForItem.isReceived())
                orderState = ItemOrderStates.Planned;
            else orderState = ItemOrderStates.NoOrder;
        }
        if (currentState != getOrderState()) {
            save();
        }
    }

    public String getPrettyName() {
        String result = getName();
        if (!getAlias().isEmpty()) {
            result = alias;
        }
        if (getValue().hasValue()) {
            result += " " + value.toString();
        }
        if (Main.DEBUG_MODE) {
            result += " (" + id + ")";
        }
        return result;
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
//        if (description != null && !description.isEmpty() && description.contains("?")) {
//            description = description.replace("?", Statics.ValueUnits.R.toString());
//        }
        this.description = description;
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

    public void putLocation(Location location) {
        this.locationId = location.getId();
        this.location = location;
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
        if (amount < 0) {
            amount = 0;
        }
        this.amount = amount;
    }

    public ItemAmountTypes getAmountType() {
        return amountType;
    }

    public void setAmountType(ItemAmountTypes amountType) {
        this.amountType = amountType;
    }

    public void setAmountType(int amountType) {
        this.amountType = ItemAmountTypes.fromInt(amountType);
    }

    public ItemOrderStates getOrderState() {
        return orderState;
    }

    public void setOrderState(ItemOrderStates orderState) {
        this.orderState = orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = ItemOrderStates.fromInt(orderState);
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
