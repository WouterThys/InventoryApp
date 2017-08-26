package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.ValueUtils;

import java.math.BigDecimal;

public class Value {

    private double value;
    private int multiplier; // Real value is value *10^multiplier
    private String unit;


    public Value() {
        value = 0.0;
        multiplier = 0;
        unit = "";
    }

    public Value(BigDecimal bigDecimal) {
        multiplier = bigDecimal.precision() - bigDecimal.scale() - 1;
        value = bigDecimal.scaleByPowerOfTen(-multiplier).doubleValue();
    }

    public Value (double value, int multiplier, String unit) {
        this.value = value;
        this.multiplier = multiplier;
        this.unit = unit;
    }


    @Override
    public String toString() {
        return ValueUtils.convert(getRealValue(), 1) + unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getRealValue() {
        return new BigDecimal(String.valueOf(value * Math.pow(10, multiplier)));
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
