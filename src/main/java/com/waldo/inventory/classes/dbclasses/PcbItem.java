package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.database.DbManager;
import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class PcbItem extends DbObject {

    public static final String TABLE_NAME = "pcbitems";

    private String ref;
    private String value; // db
    private String footprint; // db
    private String library;
    private String partName;
    private String sheetName;
    private Date tStamp;
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

        statement.setString(ndx++, getValue());
        statement.setString(ndx++, getFootprint());
        statement.setString(ndx++, getLibrary());
        statement.setString(ndx++, getPartName());

        return ndx;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<PcbItem> list = db().getPcbItems();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<PcbItem> list = db().getPcbItems();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onPcbItemChangedListenerList);
    }

    @Override
    public PcbItem createCopy() {
        return createCopy(new PcbItem());
    }

    @Override
    public PcbItem createCopy(DbObject copyInto) {
        PcbItem cpy = new PcbItem();
        copyBaseFields(cpy);
        cpy.setValue(getValue());
        cpy.setFootprint(getFootprint());
        cpy.setLibrary(getLibrary());
        cpy.setRef(getRef());
        cpy.setReferences(getReferences());
        cpy.setPartName(getPartName());
        cpy.settStamp(gettStamp());
        cpy.setSheetName(getSheetName());
        return cpy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PcbItem) {
            PcbItem ref = (PcbItem) obj;

            return ref.getLibrary().equals(getLibrary()) &&
                    ref.getValue().equals(getValue()) &&
                    ref.getFootprint().equals(getFootprint()) &&
                    ref.getSheetName().equals(getSheetName()) &&
                    ref.getPartName().equals(getPartName());
        }
        return false;
    }

    public String getRef() {
        if (ref == null)  {
            ref = "";
        }
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getValue() {
        if (value == null) {
            value = "";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Date gettStamp() {
        return tStamp;
    }

    public void settStamp(Date tStamp) {
        this.tStamp = tStamp;
    }

    public List<String> getReferences() {
        if (references == null) {
            references = new ArrayList<>();
            references.add(getRef()); // Add your own reference
        }
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }


    public void addReference(String reference) {
        if (!getReferences().contains(reference)) {
            getReferences().add(reference);
        }
    }

    public String getReferenceString() {
        StringBuilder refs = new StringBuilder();
        getReferences().sort(new ReferenceComparer());
        for (String r : getReferences()) {
            refs.append(r).append(", ");
        }
        refs.delete(refs.lastIndexOf(", "), refs.length());
        return refs.toString();
    }

    private static class ReferenceComparer implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            String one = StringUtils.leftPad(o1, 5, "0");
            String two = StringUtils.leftPad(o2, 5, "0");
            return one.compareTo(two);
        }
    }

    public List<PcbItemItemLink> getItemLinkList() {
        if (itemLinkList == null) {
            itemLinkList = PcbItemParser.getInstance().findLinkWithItem(this);
        }
        return itemLinkList;
    }

    public int matchCount() {
        return getItemLinkList().size();
    }

    public int highestMatch() {
        if (getItemLinkList().size() > 0) {
            return getItemLinkList().get(0).getMatch();
        }
        return 0;
    }

    public boolean hasMatch() {
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