package com.waldo.inventory.Utils.parser.SetItem;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.ValueUtils;
import com.waldo.inventory.classes.Value;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SetItemValueParser {

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
    protected List<Value> valueList;

    protected BigDecimal minValue = BigDecimal.ZERO;
    protected BigDecimal maxValue = BigDecimal.ZERO;

    protected SetItemValueParser() {
        valueList = new ArrayList<>();
    }

    public static <P extends SetItemValueParser> P getParser(String type) {
        switch (type) {
            case R: return (P) new ResistorValueParser();
            case C: return (P) new CapacitorValueParser();
            default:return null;
        }
    }

    public abstract String getFileName(String series);

    public abstract List<Value> parse(String series) throws ParseException, IOException;

    public abstract List<Value> crop(int value);

    public abstract Statics.ValueUnits getUnit();

    public List<String> getParseValues(String series) throws IOException {
        List<String> parseValues = new ArrayList<>();

        String fileName = getFileName(series);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        String line;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\\s+");
                parseValues.addAll(Arrays.asList(split));
            }
        }

        return parseValues;
    }

    public void setMinValue(double value, Statics.ValueMultipliers unit) {
        minValue = ValueUtils.parse(String.valueOf(value)+unit.toString());
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMaxValue(double value, Statics.ValueMultipliers unit) {
        maxValue = ValueUtils.parse(String.valueOf(value)+unit.toString());
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
