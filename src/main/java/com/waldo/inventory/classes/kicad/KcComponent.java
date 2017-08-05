package com.waldo.inventory.classes.kicad;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.waldo.inventory.classes.KcItemLink.MATCH_FOOTPRINT;
import static com.waldo.inventory.classes.KcItemLink.MATCH_NAME;
import static com.waldo.inventory.classes.KcItemLink.MATCH_VALUE;
import static com.waldo.inventory.database.DbManager.db;

public class KcComponent extends DbObject {

    public static final String TABLE_NAME = "kccomponents";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private String ref;
    private String value; // db
    private String footprint; // db
    private KcLibSource libSource; // db
    private KcSheetPath sheetPath;
    private Date tStamp;
    // After sorting multiple references can group together in one KcComponent
    private List<String> references;

    // For matching with item
    private List<KcItemLink> itemMatchMap;

    // Matched item
    private KcItemLink matchedItem;

    // Order
    private OrderItem orderItem = null;

    public KcComponent() {
        super(TABLE_NAME);

        libSource = new KcLibSource();
        sheetPath = new KcSheetPath();
    }

    public void parseTimeStamp(String tStamp) {
        if (!tStamp.isEmpty()) {
            long l = new BigInteger(tStamp, 16).longValue();
            this.tStamp = new Date(l*1000);
        }
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setString(ndx++, getValue());
        statement.setString(ndx++, getFootprint());
        statement.setString(ndx++, getLibSource().getLib());
        statement.setString(ndx++, getLibSource().getPart());

        return ndx;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<KcComponent> list = db().getKcComponents();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<KcComponent> list = db().getKcComponents();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onKcComponentChangedListenerList);
    }

    @Override
    public KcComponent createCopy() {
        return createCopy(new KcComponent());
    }

    @Override
    public KcComponent createCopy(DbObject copyInto) {
        KcComponent cpy = new KcComponent();
        copyBaseFields(cpy);
        cpy.setValue(getValue());
        cpy.setFootprint(getFootprint());
        cpy.setLibSource(getLibSource());
        cpy.setRef(getRef());
        cpy.setSheetPath(getSheetPath());
        cpy.settStamp(gettStamp());
        return cpy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof KcComponent) {
            KcComponent ref = (KcComponent) obj;

            return ref.getLibSource().getPart().equals(getLibSource().getPart()) &&
                    ref.getValue().equals(getValue()) &&
                    ref.getFootprint().equals(getFootprint()) &&
                    ref.getSheetPath().getNames().equals(getSheetPath().getNames());
        }
        return false;
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
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

    public KcLibSource getLibSource() {
        return libSource;
    }

    public void setLibSource(KcLibSource libSource) {
        this.libSource = libSource;
    }

    public KcSheetPath getSheetPath() {
        return sheetPath;
    }

    public void setSheetPath(KcSheetPath sheetPath) {
        this.sheetPath = sheetPath;
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
            references.add(getRef());
        }
        return references;
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

    public static List<KcItemLink> findInSet(KcComponent component, Item item) {
        List<KcItemLink> itemMatches = new ArrayList<>();
        String kcName = component.getLibSource().getPart().toUpperCase();
        String kcValue = component.getValue().toUpperCase();
        String kcFp = component.getFootprint().toUpperCase();
        for (SetItem setItem : SearchManager.sm().findSetItemsByItemId(item.getId())) {
            int match = 0;
            String setItemName = setItem.getName().toUpperCase();
            String setItemValue = setItem.getValue().toUpperCase();
            String setItemFp = "";

            if (item.getDimensionType() != null) {
                setItemFp = item.getDimensionType().getName();
            } else if (item.getPackage() != null && item.getPackage().getPackageType() != null){
                setItemFp = item.getPackage().getPackageType().getName();
            }

            if (setItemName.equals(kcName)) {
                match |= MATCH_NAME;
            }
            if (kcValue.contains(setItemValue) || setItemValue.contains(kcValue)) {
                match |= MATCH_VALUE;
            }
            // Only check footprint match if there is already a match
            if (((match & MATCH_NAME) == MATCH_NAME) || ((match & MATCH_VALUE) == MATCH_VALUE)) {
                if (!setItemFp.isEmpty() && kcFp.contains(setItemFp)) {
                    match |= MATCH_FOOTPRINT;
                }
            }

            // Add
            KcItemLink link = SearchManager.sm().findKcItemLinkWithSetItemId(setItem.getId(), component.getId());
            if (link != null) {
                itemMatches.add(link);
                component.setMatchedItem(link);
            } else {
                if (match > 0) {
                    itemMatches.add(new KcItemLink(match, component, setItem));
                }
            }

        }
        return itemMatches;
    }

    public static List<KcItemLink> findInItem(KcComponent component, Item item) {
        List<KcItemLink> itemMatches = new ArrayList<>();
        int match = 0;
        String itemName = item.getName().toUpperCase();
        String kcName = component.getLibSource().getPart().toUpperCase();
        String kcValue = component.getValue().toUpperCase();
        String kcFp = component.getFootprint().toUpperCase();
        if (itemName.contains(kcName) || kcName.contains(itemName)) {
            match |= MATCH_NAME;
        }
        if (itemName.contains(kcValue) || kcValue.contains(itemName)) {
            match |= MATCH_VALUE;
        }
        String setItemFp = "";
        if (item.getDimensionType() != null) {
            setItemFp = item.getDimensionType().getName();
        } else if (item.getPackage() != null && item.getPackage().getPackageType() != null){
            setItemFp = item.getPackage().getPackageType().getName();
        }
        // Only check footprint match if there is already a match
        if (((match & MATCH_NAME) == MATCH_NAME) || ((match & MATCH_VALUE) == MATCH_VALUE)) {
            if (!setItemFp.isEmpty() && (kcFp.contains(setItemFp) || setItemFp.contains(kcFp))) {
                match |= MATCH_FOOTPRINT;
            }
        }

        // Add

        KcItemLink link = SearchManager.sm().findKcItemLinkWithItemId(item.getId(), component.getId());
        if (link != null) {
            itemMatches.add(link);
            component.setMatchedItem(link);
        } else {
            if (match > 0) {
                itemMatches.add(new KcItemLink(match, component, item));
            }
        }

        return itemMatches;
    }

    public void findMatchingItems() {
        itemMatchMap = new ArrayList<>();

        for (Item item : DbManager.db().getItems()) {
            if (getLibSource().getLib().toUpperCase().equals("DEVICE")) {
                if (item.isSet()) {
                    itemMatchMap.addAll(findInSet(this, item));
                }
            } else {
                itemMatchMap.addAll(findInItem(this, item));
            }
        }

        itemMatchMap.sort(new MatchComparator());
    }

    public static int getMatchCount(int i) {
        i = i - ((i >>> 1) & 0x55555555);
        i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
        return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
    }

    public List<KcItemLink> getItemMatchMap() {
        if (itemMatchMap == null) {
            findMatchingItems();
        }
        return itemMatchMap;
    }

    public int matchCount() {
        return getItemMatchMap().size();
    }

    public int highestMatch() {
        if (getItemMatchMap().size() > 0) {
            return getItemMatchMap().get(0).getMatch();
        }
        return 0;
    }

    public boolean hasMatch() {
        return matchedItem != null;
    }

    public KcItemLink getMatchedItem() {
        return matchedItem;
    }

    public void setMatchedItem(KcItemLink matchedItem) {
        if (matchedItem != null) {
            matchedItem.setMatched(true);
        }
        this.matchedItem = matchedItem;
    }

    public boolean isOrdered() {
        return orderItem != null;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    private class MatchComparator implements Comparator<KcItemLink> {
        @Override
        public int compare(KcItemLink o1, KcItemLink o2) {
            int mc1 = getMatchCount(o1.getMatch());
            int mc2 = getMatchCount(o2.getMatch());
            if (mc1 < mc2) {
                return 1;
            } else if (mc1 > mc2) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}