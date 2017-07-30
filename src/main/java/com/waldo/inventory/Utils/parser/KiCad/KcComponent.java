package com.waldo.inventory.Utils.parser.KiCad;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class KcComponent extends com.waldo.inventory.Utils.parser.Component {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    // Bitwise matches
    public static final int MATCH_NAME = 1;
    public static final int MATCH_VALUE = 2;
    public static final int MATCH_FOOTPRINT = 4;

    private String ref;
    private String value;
    private String footprint;
    private KcLibSource libSource;
    private KcSheetPath sheetPath;
    private Date tStamp;
    // After sorting multiple references can group together in one KcComponent
    private List<String> references;

    // For matching with item
    private TreeMap<Integer, List<DbObject>> itemMatchMap;

    public void parseTimeStamp(String tStamp) {
        if (!tStamp.isEmpty()) {
            long l = new BigInteger(tStamp, 16).longValue();
            this.tStamp = new Date(l*1000);
        }
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

    private void findInSet(Item item) {
        String kcName = getLibSource().getPart().toUpperCase();
        String kcValue = getValue().toUpperCase();
        String kcFp = getFootprint().toUpperCase();
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
            if (kcValue.contains(setItemValue)) {
                match |= MATCH_VALUE;
            }
            if (!setItemFp.isEmpty() && kcFp.contains(setItemFp)) {
                match |= MATCH_FOOTPRINT;
            }

            // Add
            if (match > 0) {
                if (!itemMatchMap.containsKey(match)) {
                    itemMatchMap.put(match, new ArrayList<>());
                }
                itemMatchMap.get(match).add(setItem);
            }
        }
    }

    private void findInItem(Item item) {
        int match = 0;
        String itemName = item.getName().toUpperCase();
        String kcName = getLibSource().getPart().toUpperCase();
        String kcValue = getValue().toUpperCase();
        String kcFp = getFootprint().toUpperCase();
        if (itemName.contains(kcName) || kcName.contains(itemName)) {
            match |= MATCH_NAME;
        }
        if (itemName.contains(kcValue) || kcValue.contains(itemName)) {
            match |= MATCH_VALUE;
        }
        String setItemFp = "";
        if (item.getDimensionType() != null) {
            setItemFp = item.getDimensionType().getIconPath();
        } else if (item.getPackage() != null && item.getPackage().getPackageType() != null){
            setItemFp = item.getPackage().getPackageType().getName();
        }
        if (!setItemFp.isEmpty() && kcFp.contains(setItemFp)) {
            match |= MATCH_FOOTPRINT;
        }

        // Add
        if (match > 0) {
            if (!itemMatchMap.containsKey(match)) {
                itemMatchMap.put(match, new ArrayList<>());
            }
            itemMatchMap.get(match).add(item);
        }
    }

    public void findMatchingItems() {
        itemMatchMap = new TreeMap<>(new MatchComparator());

        for (Item item : DbManager.db().getItems()) {
            if (getLibSource().getLib().toUpperCase().equals("DEVICE")) {
                if (item.isSet()) {
                    findInSet(item);
                }
            } else {
                findInItem(item);
            }
        }
    }

    public static int getMatchCount(int i) {
        i = i - ((i >>> 1) & 0x55555555);
        i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
        return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
    }

    public TreeMap<Integer, List<DbObject>> getItemMatchMap() {
        if (itemMatchMap == null) {
            findMatchingItems();
        }
        return itemMatchMap;
    }

    public int matchCount() {
        int total = 0;
        for (Integer key : getItemMatchMap().keySet()) {
            total += getItemMatchMap().get(key).size();
        }
        return total;
    }

    public int highestMatch() {
        if (getItemMatchMap().size() > 0) {
            return getItemMatchMap().firstKey();
        }
        return 0;
    }

    private class MatchComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            int mc1 = getMatchCount(o1);
            int mc2 = getMatchCount(o2);
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