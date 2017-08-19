package com.waldo.inventory.Utils.parser.SetItem;

import com.waldo.inventory.classes.SetItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.Utils.ValueUtils.convertUnit;

public abstract  class SetItemParser {

    public static final String R = "R";
    public static final String C = "C";
    public static final String L = "L";

    public static final String[] TYPES = {R, C, L};

    public static final String E3 = "_E3";
    public static final String E6 = "_E6";
    public static final String E12 = "_E12";
    public static final String E24 = "_E24";
    public static final String E48 = "_E48";
    public static final String E96 = "_E96";

    public static final String[] R_SERIES = {E3, E6, E12, E24, E48, E96};

    protected File parserFile;
    protected List<SetItem> setItems;

    protected double minValue = 0;
    protected double maxValue = 0;

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
        minValue = convertUnit(value, unit);
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMaxValue(double value, String unit) {
        maxValue = convertUnit(value, unit);
    }

    public double getMaxValue() {
        return maxValue;
    }

    public List<Double> decadeValues(double minValue, double maxValue) {
        List<Double> decades = new ArrayList<>();

        double maxNormal = maxValue * (1 / minValue);

        int val = 1;
        while (maxNormal >= 0.1) {
            decades.add(val * minValue);

            maxNormal /= 10;

            val *= 10;
        }

        return decades;
    }

}
