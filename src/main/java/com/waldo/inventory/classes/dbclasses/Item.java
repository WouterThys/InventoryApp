package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.ItemAmountTypes;
import com.waldo.inventory.Utils.Statics.ItemOrderStates;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.search.SearchMatch;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.FileUtils;

import javax.sql.rowset.serial.SerialBlob;
import javax.swing.*;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class Item extends DbObject {

    public static final String TABLE_NAME = "items";

    protected String alias;
    protected Value value;
    protected String description = "";

    private long divisionId = UNKNOWN_ID;
    protected Division division;

    private boolean isSet = false;

    private String localDataSheet = "";
    private String onlineDataSheet = "";

    private long manufacturerId = UNKNOWN_ID;
    protected Manufacturer manufacturer;
    private long locationId = UNKNOWN_ID;
    protected Location location;
    protected int amount = 0;
    private ItemAmountTypes amountType = ItemAmountTypes.Unknown;
    private ItemOrderStates orderState = null;

    private long packageTypeId = UNKNOWN_ID;
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
            this.division = set.getDivision();

            if (division != null) divisionId = division.getId();
        }
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;
        statement.setString(ndx++, getName());
        statement.setString(ndx++, getAlias());
        statement.setNString(ndx++, getDescription());
        statement.setLong(ndx++, getDivisionId());
        statement.setString(ndx++, getLocalDataSheet());
        statement.setString(ndx++, getOnlineDataSheet());
        statement.setString(ndx++, getIconPath());
        statement.setLong(ndx++, getManufacturerId());
        statement.setLong(ndx++, getLocationId());
        statement.setInt(ndx++, getAmount());
        statement.setInt(ndx++, getAmountType().getValue());
        statement.setInt(ndx++, 0);
        statement.setLong(ndx++, getPackageTypeId()); // PackageId
        statement.setInt(ndx++, getPins());
        statement.setFloat(ndx++, getRating());
        statement.setBoolean(ndx++, isDiscourageOrder());
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
    public Item createCopy(DbObject copyInto) {
        Item item = (Item) copyInto;
        copyBaseFields(item);

        item.setAlias(getAlias());
        item.setValue(Value.copy(getValue()));
        item.setDescription(getDescription());
        item.setDivisionId(getDivisionId());
        item.setLocalDataSheet(getLocalDataSheet());
        item.setOnlineDataSheet(getOnlineDataSheet());
        item.setManufacturerId(getManufacturerId());
        item.setLocationId(getLocationId());
        item.setAmount(getAmount());
        item.setAmountType(getAmountType());
        //item.setOrderState(getOrderState());
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
                if (!(ref.getDivisionId() == getDivisionId())) {
                    if (Main.DEBUG_MODE) System.out.println("DivisionId: " + ref.getDivisionId() + " != " + getDivisionId());
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
                if (!(ref.getRemarksFileName().equals(getRemarksFileName()))) {
                    if (Main.DEBUG_MODE) System.out.println("Remarks files differ");
                    return false;
                }
            }
        }
        return result;
    }

    public List<SearchMatch> searchByKeyWord(String searchTerm) {
        List<SearchMatch> matchList = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            searchTerm = searchTerm.toUpperCase();

            SearchMatch m;
            m = SearchMatch.hasMatch(32, getName(), searchTerm);
            if (m != null) matchList.add(m);

            m = SearchMatch.hasMatch(16, getAlias(), searchTerm);
            if (m != null) matchList.add(m);

            m = SearchMatch.hasMatch(8, getValue().toString(), searchTerm);
            if (m != null) matchList.add(m);

            m = SearchMatch.hasMatch(4, getDivision(), searchTerm);
            if (m != null) matchList.add(m);

            m = SearchMatch.hasMatch(2, getManufacturer(), searchTerm);
            if (m != null) matchList.add(m);

            m = SearchMatch.hasMatch(2, getLocation(), searchTerm);
            if (m != null) matchList.add(m);

            m = SearchMatch.hasMatch(2, getPackageType(), searchTerm);
            if (m != null) matchList.add(m);
        }
        return matchList;
    }

    public List<SearchMatch> searchByPcbItem(PcbItemProjectLink pcbItemProjectLink, boolean divisionsEqual) {
        List<SearchMatch> matchList = new ArrayList<>();
        if (pcbItemProjectLink != null) {
            PcbItem pcbItem = pcbItemProjectLink.getPcbItem();

            // Item values
            String iName = getName().toUpperCase();
            String aName = getAlias().toUpperCase();

            // Pcb item values
            String pName = pcbItem.getPartName().toUpperCase();
            String pValue = pcbItemProjectLink.getValue();
            String fValue = pcbItem.getFootprint().toUpperCase();

            if (divisionsEqual) {
                matchList.add(new SearchMatch(64, pName, iName));
            } else {
                // Name
                if (((pName.length() > 2 && iName.length() > 2) && (iName.contains(pName) || pName.contains(iName)))) {
                    matchList.add(new SearchMatch(64, pName, iName));
                } else {
                    // Alias
                    if ((pName.length() > 2 && aName.length() > 2) && (aName.contains(pName) || pName.contains(aName))) {
                        matchList.add(new SearchMatch(16, pName, aName));
                    }
                }
            }


            // Footprint
            if (getPackageTypeId() > UNKNOWN_ID) {
                String pkName = packageType.getPackage().getName().toUpperCase();
                String ptName = packageType.getName().toUpperCase();
                if (fValue.contains(pkName) || pkName.contains(fValue) || fValue.contains(ptName) || ptName.contains(fValue)) {
                    matchList.add(new SearchMatch(32, fValue, pkName));
                }
            }

            // Value
            if (getValue().hasValue()) {
                Value value = Value.tryFindValue(pValue);
                if (value != null && value.equalsIgnoreUnits(getValue())) {
                    matchList.add(new SearchMatch(32, value, getValue()));
                }
            } else {
                if ((pValue.length() > 2 && iName.length() > 2) && (iName.contains(pValue) || pValue.contains(iName))) {
                    matchList.add(new SearchMatch(32, pValue, iName));
                }
            }
        }

        return matchList;
    }

    public String createRemarksFileName() {
        return getId() + "_ItemObject_";
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    public void updateOrderState() {
        orderState = null;
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

    public ImageIcon getItemIcon() {
//        ImageIcon icon = null;
//        if (getDivision() != null) {
//            switch (getDivision().getDisplayType()) {
//                default: break;
//                case R_THT:
//                    if (getValue().hasValue()) {
//                        Resistor r = new Resistor();
//                        try {
//                            r.setBandsForValue(Statics.ResistorBandType.FourBand, getValue(), Statics.ResistorBandValue.Gold);
//                            IResistorImage image = new IResistorImage(r);
//                            icon = new ImageIcon(image.createImage(150, 150));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//            }
//        }
//
//        if (icon == null) {
//            icon = getIconFromPath(getIconPath());
//        }
//        return icon;
        return imageResource.readItemIcon(iconPath);
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

    public long getDivisionId() {
        if (divisionId < UNKNOWN_ID) {
            divisionId = UNKNOWN_ID;
        }
        return divisionId;
    }

    public Division getDivision() {
        if (division == null && getDivisionId() > UNKNOWN_ID) {
            division = SearchManager.sm().findDivisionById(divisionId);
        }
        return division;
    }

    public void setDivisionId(long divisionId) {
        if (division != null && division.getId() != divisionId) {
            division = null;
        }
        this.divisionId = divisionId;
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
        if (manufacturerId < UNKNOWN_ID) {
            manufacturerId = UNKNOWN_ID;
        }
        return manufacturerId;
    }

    public Manufacturer getManufacturer() {
        if (manufacturer == null && manufacturerId > UNKNOWN_ID) {
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
        if (locationId < UNKNOWN_ID) {
            locationId = UNKNOWN_ID;
        }
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
        if (location == null && locationId > UNKNOWN_ID) {
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
        if (amountType == null) {
            amountType = ItemAmountTypes.Unknown;
        }
        return amountType;
    }

    public void setAmountType(ItemAmountTypes amountType) {
        this.amountType = amountType;
    }

    public void setAmountType(int amountType) {
        this.amountType = ItemAmountTypes.fromInt(amountType);
    }

    public ItemOrderStates getOrderState() {
        if (orderState == null) {
            List<Order> orders = SearchManager.sm().findOrdersForItem(getId());
            orderState = ItemOrderStates.NoOrder;

            int inPlanned = 0;
            int inOrdered = 0;
            int inReceived = 0;

            for (Order o : orders) {
                if (o.isPlanned()) {
                    inPlanned++;
                }
                if (o.isOrdered() && !o.isReceived()) {
                    inOrdered++;
                }
                if (o.isOrdered() && o.isReceived()) {
                    inReceived++;
                }
            }

            if (inPlanned > 0) orderState = ItemOrderStates.Planned;
            else if (inOrdered > 0) orderState = ItemOrderStates.Ordered;
            else orderState = ItemOrderStates.NoOrder;
        }
        return orderState;
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

    private String getRemarksFileName() {
        if (remarksFile == null) {
            return "";
        }
        return remarksFile;
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
        } else {
            this.remarksFile = null;
        }
    }

    public long getPackageTypeId() {
        if (packageTypeId < UNKNOWN_ID) {
            packageTypeId = UNKNOWN_ID;
        }
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
        if (packageType == null && packageTypeId > UNKNOWN_ID) {
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
