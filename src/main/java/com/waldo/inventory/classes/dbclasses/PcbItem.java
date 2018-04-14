package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.managers.SearchManager;

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

    // Values used when parsing from file, these values will be empty when fetched from db
    private String ref; // => to PcbItemProjectLink
    private String value; // => to PcbItemProjectLink
    private String sheetName; // => to PcbItemProjectLink
    private List<String> references; // => PcbItemProjectLink

    private Date tStamp; // not used

    // Order
    private OrderLine orderItem = null;
    private int orderAmount  = 0;

    // All known links
    private List<PcbItemItemLink> knownItemLinks;



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
        String result = getPartName();
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

            return ref.getFootprint().equals(getFootprint()) &&
                    ref.getPartName().equals(getPartName());
        }
        return false;
    }

    public static boolean matchesName(String pcbName, String itemName) {
        String pName = pcbName.toUpperCase();
        String iName = itemName.toUpperCase();
        return (pName.length() > 2 && iName.length() > 2) && (iName.contains(pName) || pName.contains(iName));
    }

    public static boolean matchesAlias(String pcbName, String alias) {
        String pName = pcbName.toUpperCase();
        String aName = alias.toUpperCase();
        return (pName.contains(aName) || aName.contains(pName));
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

    public static boolean matchesFootprint(String pcbFp, PackageType packageType) {
        if (pcbFp != null && !pcbFp.isEmpty() && packageType != null) {
            String pkName = packageType.getPackage().getName().toUpperCase();
            String ptName = packageType.getName().toUpperCase();
            String pcbName = pcbFp.toUpperCase();

            if (pcbName.contains(pkName) || pkName.contains(pcbName) || pcbName.contains(ptName) || ptName.contains(pcbName)) {
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

    public List<PcbItemItemLink> getKnownItemLinks() {
        if (knownItemLinks == null) {
            knownItemLinks = SearchManager.sm().findPcbItemItemLinksForPcbItem(getId());
        }
        return knownItemLinks;
    }

    //
    // Extra's for ordering
    //

    public boolean isOrdered() {
        return orderItem != null;
    }

    public OrderLine getOrderItem() {
        return orderItem;
    }

    public void setOrderLine(OrderLine orderItem) {
        this.orderItem = orderItem;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }
}