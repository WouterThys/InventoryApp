package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.ValueUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Value {

    // Double value
    private double doubleValue;
    private Statics.ValueMultipliers multiplier; // Real doubleValue is doubleValue *10^multiplier
    private Statics.ValueUnits unit;

    public Value() {
        doubleValue = 0.0;
        multiplier = Statics.ValueMultipliers.x;
        unit = Statics.ValueUnits.Unknown;
    }

    public Value(BigDecimal bigDecimal) {
        int m = bigDecimal.precision() - bigDecimal.scale() - 1;
        double v = bigDecimal.scaleByPowerOfTen(-m).doubleValue();
        createValues(v, m);
    }

    public Value (double doubleValue, int multiplier, String unit) {
        setUnit(unit);
        createValues(doubleValue, multiplier);
    }

    public Value (double doubleValue, int multiplier, Statics.ValueUnits unit) {
        setUnit(unit);
        createValues(doubleValue, multiplier);
    }

    public Value (double doubleValue, Statics.ValueMultipliers multiplier, String unit) {
        setUnit(unit);
        createValues(doubleValue, multiplier.getMultiplier());
    }

    public Value (double doubleValue, Statics.ValueMultipliers multiplier, Statics.ValueUnits unit) {
        setUnit(unit);
        createValues(doubleValue, multiplier.getMultiplier());
    }


    private void createValues(double doubleValue, int multiplier) {
        if (multiplier != 0) {
            boolean positive = multiplier >= 0;
            int m = Math.abs(multiplier);

            while ((m != 0) && !(m % 3 == 0)) {
                if (positive) {
                    m = m - 1;
                } else {
                    m = m + 1;
                }
                doubleValue = doubleValue * 10;
            }

            this.doubleValue = doubleValue;
            if (positive) {
                this.multiplier = Statics.ValueMultipliers.fromInt(m);
            } else {
                this.multiplier = Statics.ValueMultipliers.fromInt(-m);
            }

        } else {
            this.doubleValue = doubleValue;
            this.multiplier = Statics.ValueMultipliers.fromInt(multiplier);
        }
    }

    public static Value copy(Value v) {
        return new Value(v.getDoubleValue(), v.getMultiplier(), v.getUnit());
    }


    @Override
    public String toString() {
        return ValueUtils.convert(getRealValue(), 1) + getUnit();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Value) {
            Value v = (Value) obj;
            return v.getRealValue().equals(getRealValue()) && v.getUnit().equals(getUnit());
        }
        return false;
    }

    public boolean equalsIgnoreUnits(Object obj) {
        if (obj != null && obj instanceof Value) {
            Value v = (Value) obj;
            return v.getRealValue().equals(getRealValue());
        }
        return false;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Statics.ValueMultipliers getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = Statics.ValueMultipliers.fromInt(multiplier);
    }

    public void setMultiplier(Statics.ValueMultipliers multiplier) {
        this.multiplier = multiplier;
    }

    public Statics.ValueUnits getUnit() {
        if (unit == null) {
            unit = Statics.ValueUnits.Unknown;
        }
        return unit;
    }

    public BigDecimal getRealValue() {
        return new BigDecimal(String.valueOf(doubleValue * Math.pow(10, multiplier.getMultiplier())));
    }

    public void setUnit(String unit) {
        this.unit = Statics.ValueUnits.fromString(unit);
    }

    public void setUnit(Statics.ValueUnits unit) {
        this.unit = unit;
    }

    public boolean hasValue() {
        return (getDoubleValue() != 0) || (getUnit() != Statics.ValueUnits.Unknown);
    }

    public static Value tryFindValue(String valueTxt) {

        double foundDouble = -1;
        int foundMultiplier = 0;
        if (valueTxt != null && !valueTxt.isEmpty()) {
            valueTxt = valueTxt.trim();
            valueTxt = valueTxt.replace(',', '.');

            Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(valueTxt);
            while (m.find()) {
                foundDouble = Double.parseDouble(m.group(1));
            }

            if (foundDouble >= 0) {
                if (valueTxt.contains("f")) foundMultiplier = -15;
                else if (valueTxt.contains("p")) foundMultiplier = -12;
                else if (valueTxt.contains("n")) foundMultiplier = -9;
                else if (valueTxt.contains("µ") || valueTxt.contains("u")) foundMultiplier = -6;
                else if (valueTxt.contains("m")) foundMultiplier = -3;
                else if (valueTxt.contains("k") || valueTxt.contains("K")) foundMultiplier = 3;
                else if (valueTxt.contains("M")) foundMultiplier = 6;
                else if (valueTxt.contains("G")) foundMultiplier = 9;
                else if (valueTxt.contains("T")) foundMultiplier = 12;

                return new Value(foundDouble, foundMultiplier, "");
            }
        }
        return null;
    }
}
