package com.waldo.inventory.Utils.parser.SetItem;

import com.waldo.inventory.Utils.ValueUtils;
import com.waldo.inventory.classes.dbclasses.SetItem;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public abstract  class SetItemParser {

    public static final String R = "R";
    public static final String C = "C";
    public static final String L = "L";

    public static final String E3 = "_E3";
    public static final String E6 = "_E6";
    public static final String E12 = "_E12";
    public static final String E24 = "_E24";
    public static final String E48 = "_E48";
    public static final String E96 = "_E96";

    public static final String[] R_SERIES = {E3, E6, E12, E24, E48, E96};

    protected File parserFile;
    protected List<SetItem> setItems;

    protected BigDecimal minValue = BigDecimal.ZERO;
    protected BigDecimal maxValue = BigDecimal.ZERO;

    protected SetItemParser() {
        setItems = new ArrayList<>();
    }

    public static <P extends SetItemParser> P getParser(String type) {
        switch (type) {
            case R: return (P) new ResistorParser();
            case C: return (P) new CapacitorParser();
            default:return null;
        }
    }

    public abstract List<SetItem> parse(String series) throws ParseException, IOException;

    public abstract List<SetItem> crop(int value);

    public abstract String getUnit();

    public void setMinValue(double value, String unit) {
        minValue = ValueUtils.parse(String.valueOf(value)+unit);
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMaxValue(double value, String unit) {
        maxValue = ValueUtils.parse(String.valueOf(value)+unit);
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    protected List<BigDecimal> toDecimalList(List<String> stringValues) {
        List<BigDecimal> decimals = new ArrayList<>();

        for (String s : stringValues) {
            decimals.add(new BigDecimal(s));
        }

        return decimals;
    }


}
