package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;

public class Value {

    private double value;
    private int multiplier; // Real value is value *10^multiplier
    private String unit;


    public Value() {
        value = 0.0;
        multiplier = 0;
        unit = "";
    }

    public Value (double value, int multiplier, String unit) {
        this.value = value;
        this.multiplier = multiplier;
        this.unit = unit;
    }


    @Override
    public String toString() {
        String valueTxt = String.valueOf(value);
        String multiplierTxt = Statics.UnitMultipliers.multiplierFor(multiplier);
        return valueTxt + multiplierTxt + unit;
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

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
