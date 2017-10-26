package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.ValueUtils;

import java.math.BigDecimal;

public class Value {

    // Double value
    private double doubleValue;
    private int multiplier; // Real doubleValue is doubleValue *10^multiplier
    private String unit;

    public Value() {
        doubleValue = 0.0;
        multiplier = 0;
        unit = "";
    }

    public Value(BigDecimal bigDecimal) {
        multiplier = bigDecimal.precision() - bigDecimal.scale() - 1;
        doubleValue = bigDecimal.scaleByPowerOfTen(-multiplier).doubleValue();
    }

    public Value (double doubleValue, int multiplier, String unit) {
        setUnit(unit);
        createValues(doubleValue, multiplier);
    }

    public Value(String stringValue) {
        this.doubleValue = 0;
        this.multiplier = 0;
        this.unit = "";
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
                this.multiplier = m;
            } else {
                this.multiplier = -m;
            }

        } else {
            this.doubleValue = doubleValue;
            this.multiplier = multiplier;
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
            if (v.getDoubleValue() == getDoubleValue() && v.getMultiplier() == getMultiplier() && v.getUnit().equals(getUnit())) {
                return true;
            }
        }
        return false;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public String getUnit() {
        if (unit == null) {
            unit = "";
        }
        return unit;
    }

    public BigDecimal getRealValue() {
        return new BigDecimal(String.valueOf(doubleValue * Math.pow(10, multiplier)));
    }

    public String getDbUnit() {
        if (unit == null) {
            unit = "";
        } else {
            if (unit.equals(Statics.Units.R_UNIT)) {
                return "O";
            }
        }

        return unit;
    }

    public void setUnit(String unit) {
        if (unit == null) {
            this.unit = "";
        } else {
            if (unit.equals("O")) {
                this.unit = Statics.Units.R_UNIT;
            } else {
                this.unit = unit;
            }
        }
    }
}
