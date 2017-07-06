package com.waldo.inventory.Utils.parser.KiCad;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KcComponent extends com.waldo.inventory.Utils.parser.Component {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private String ref;
    private String value;
    private String footprint;
    private KcLibSource libSource;
    private KcSheetPath sheetPath;
    private Date tStamp;

    public void parseTimeStamp(String tStamp) {
        if (!tStamp.isEmpty()) {
            long l = new BigInteger(tStamp, 16).longValue();
            this.tStamp = new Date(l*1000);
        }
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFootprint() {
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
}
