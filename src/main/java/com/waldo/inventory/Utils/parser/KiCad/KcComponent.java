package com.waldo.inventory.Utils.parser.KiCad;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class KcComponent extends com.waldo.inventory.Utils.parser.Component {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private String ref;
    private String value;
    private String footprint;
    private KcLibSource libSource;
    private KcSheetPath sheetPath;
    private Date tStamp;

    private List<String> references;

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
}
