package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.database.DatabaseAccess;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbItem extends DbObject {

    public static final String TABLE_NAME = "pcbitems";

    private String footprint; // db
    private String library; // db
    private String partName; // db

    private String ref; // => to PcbItemProjectLink
    private String value; // => to PcbItemProjectLink
    private String sheetName; // => to PcbItemProjectLink

    private Date tStamp; // not used


    // After sorting multiple references can group together in one PcbItem
    private List<String> references;

    // Link with item
    private List<PcbItemItemLink> itemLinkList;
    private PcbItemItemLink matchedItem;

    // Order
    private OrderItem orderItem = null;
    private int orderAmount  = 0;



    public PcbItem() {
        super(TABLE_NAME);
    }

    public PcbItem(String ref, String value, String footprint, String library, String partName, String sheetName, Date tStamp) {
        super(TABLE_NAME);
        this.ref = ref;
        this.value = value;
        this.footprint = footprint;
        this.library = library;
        this.partName = partName;
        this.sheetName = sheetName;
        this.tStamp = tStamp;

        references = new ArrayList<>();
        references.add(ref);
    }

    @Override
    public String toString() {
        String result = getRef() + ", " + getValue();
        if (getId() <= 0) {
            result += " *";
        }
        return result;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setString(ndx++, getFootprint());
        statement.setString(ndx++, getLibrary());
        statement.setString(ndx++, getPartName());

        return ndx;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<PcbItem> list = cache().getPcbItems();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<PcbItem> list = cache().getPcbItems();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    @Override
    public PcbItem createCopy() {
        return createCopy(new PcbItem());
    }

    @Override
    public PcbItem createCopy(DbObject copyInto) {
        PcbItem cpy = new PcbItem();
        copyBaseFields(cpy);
        cpy.setFootprint(getFootprint());
        cpy.setLibrary(getLibrary());
        cpy.setPartName(getPartName());
        cpy.settStamp(gettStamp());
        return cpy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PcbItem) {
            PcbItem ref = (PcbItem) obj;

            return ref.getLibrary().equals(getLibrary()) &&
                    ref.getFootprint().equals(getFootprint()) &&
                    ref.getPartName().equals(getPartName());
        }
        return false;
    }

    public static boolean matchesName(String pcbName, String itemName) {
        return (pcbName.length() > 2 && itemName.length() > 2) && (itemName.contains(pcbName) || pcbName.contains(itemName));
    }

    public static boolean matchesValue(String pcbValue, Value value) {
        Value pcbVal = Value.tryFindValue(pcbValue);
        if (pcbVal != null) {
            if (pcbVal.equalsIgnoreUnits(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesValue(String pcbValue, Value value, String itemName) {
        boolean res = matchesValue(pcbValue, value);

        if (!res) { // Try to match with name
            if (pcbValue.contains(itemName) || itemName.contains(pcbValue)) {
                res = true;
            }
        }

        return res;
    }

    public static boolean matchesFootprint(String pcbFp, PackageType packageType) {
        if (pcbFp != null && !pcbFp.isEmpty() && packageType != null) {
            String pkName = packageType.getPackage().getName().toUpperCase();
            String ptName = packageType.getName().toUpperCase();

            if (pcbFp.contains(pkName) || pkName.contains(pcbFp) || pcbFp.contains(ptName) || ptName.contains(pcbFp)) {
                return true;
            }
        }
        return false;
    }

    public String getRef() {
        if (ref == null)  {
            ref = "";
        }
        return ref;
    }

    public String getValue() {
        if (value == null) {
            value = "";
        }
        return value;
    }

    public String getFootprint() {
        if (footprint == null) {
            footprint = "";
        }
        return footprint;
    }

    public void setFootprint(String footprint) {
        this.footprint = footprint;
    }

    public String getLibrary() {
        if (library == null) {
            library = "";
        }
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getPartName() {
        if (partName == null) {
            partName = "";
        }
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getSheetName() {
        if (sheetName == null) {
            sheetName = "";
        }
        return sheetName;
    }

    public Date gettStamp() {
        return tStamp;
    }

    public void settStamp(Date tStamp) {
        this.tStamp = tStamp;
    }

    public List<PcbItemItemLink> getItemLinkList() {
        if (itemLinkList == null) {
            itemLinkList = PcbItemParser.getInstance().findLinkWithItem(this);
        }
        return itemLinkList;
    }

    public boolean hasMatchedItem() {
        return matchedItem != null;
    }

    public PcbItemItemLink getMatchedItemLink() {
        return matchedItem;
    }

    public void setMatchedItem(PcbItemItemLink matchedItem) {
        if (matchedItem != null) {
            matchedItem.setMatched(true);
        }
        this.matchedItem = matchedItem;
    }

    public void addReference(String reference) {
        if (references != null && !references.contains(reference)) {
            references.add(reference);
        }
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    //
    // Extra's for ordering
    //

    public boolean isOrdered() {
        return orderItem != null;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }
}