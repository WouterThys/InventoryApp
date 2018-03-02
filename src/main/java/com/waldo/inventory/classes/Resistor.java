package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.ResistorBandType;
import com.waldo.inventory.Utils.Statics.ResistorBandValue;

public class Resistor {

    private ResistorBandValue firstBand;
    private ResistorBandValue secondBand;
    private ResistorBandValue thirdBand;
    private ResistorBandValue multiplierBand;
    private ResistorBandValue toleranceBand;
    private ResistorBandValue ppmBand;

    private Value value;
    private ResistorBandType bandType;

    public Resistor() {
        value = new Value();
        bandType = ResistorBandType.FourBand;

        firstBand = ResistorBandValue.Brown;
        secondBand = ResistorBandValue.Black;
        thirdBand = ResistorBandValue.Black;
        multiplierBand = ResistorBandValue.Black;
        toleranceBand = ResistorBandValue.Gold;
        ppmBand = ResistorBandValue.Black;
    }


    public void setBandsForValue(ResistorBandType type, Value value, ResistorBandValue tolerance) throws Exception {
        this.bandType = type;
        this.value = value;
        this.toleranceBand = tolerance;

        if (value != null) {
               switch (type) {
                   case FourBand:
                       calculateFourBand(value);
                       break;
                   case FiveBand:
                   case SixBand:
                       calculateFiveBand(value);
                       break;
               }
        }
    }

    private void calculateFourBand(Value value) throws Exception {
        double v = value.getRealValue().doubleValue();

        int first = 0;
        int second = 0;
        int multiplier = 0;

        if (v >= 100) {
            first = (int) v / 100;
            second = (int) v / 10;
            multiplier = value.getMultiplier().getMultiplier();
        } else if (v >= 10) {

        } else if (v >= 1) {

        }

        firstBand = ResistorBandValue.valueBandFromValue(first);
        secondBand = ResistorBandValue.valueBandFromValue(second);
        multiplierBand = ResistorBandValue.multiplierBandFromValue(multiplier);

        if (firstBand == null || secondBand == null || multiplierBand == null) {
            throw new Exception("Invalid resistor value: " + value);
        }
    }

    private void calculateFiveBand(Value value) {

    }

    public void setValueFromBands(
            ResistorBandValue firstBand,
            ResistorBandValue secondBand,
            ResistorBandValue multiplierBand,
            ResistorBandValue toleranceBand) {

        this.firstBand = firstBand;
        this.secondBand = secondBand;
        this.multiplierBand = multiplierBand;
        this.toleranceBand = toleranceBand;

        double v = firstBand.getValue() * 10;
        v += secondBand.getValue();

        value = new Value(v, multiplierBand.getValue(), Statics.ValueUnits.R);
    }

    public void setValueFromBands(
            ResistorBandValue firstBand,
            ResistorBandValue secondBand,
            ResistorBandValue thirdBand,
            ResistorBandValue multiplierBand,
            ResistorBandValue toleranceBand) {

        this.firstBand = firstBand;
        this.secondBand = secondBand;
        this.thirdBand = thirdBand;
        this.multiplierBand = multiplierBand;
        this.toleranceBand = toleranceBand;

        double v = firstBand.getValue() * 100;
        v += secondBand.getValue() * 10;
        v += thirdBand.getValue();

        value = new Value(v, multiplierBand.getValue(), Statics.ValueUnits.R);
    }

    public void setValueFromBands(
            ResistorBandValue firstBand,
            ResistorBandValue secondBand,
            ResistorBandValue thirdBand,
            ResistorBandValue multiplierBand,
            ResistorBandValue toleranceBand,
            ResistorBandValue ppmBand) {

        this.firstBand = firstBand;
        this.secondBand = secondBand;
        this.thirdBand = thirdBand;
        this.multiplierBand = multiplierBand;
        this.toleranceBand = toleranceBand;
        this.ppmBand = ppmBand;

        double v = firstBand.getValue() * 100;
        v += secondBand.getValue() * 10;
        v += thirdBand.getValue();

        value = new Value(v, multiplierBand.getValue(), Statics.ValueUnits.R);
    }


    public ResistorBandValue getFirstBand() {
        return firstBand;
    }

    public void setFirstBand(ResistorBandValue firstBand) {
        this.firstBand = firstBand;
    }

    public ResistorBandValue getSecondBand() {
        return secondBand;
    }

    public void setSecondBand(ResistorBandValue secondBand) {
        this.secondBand = secondBand;
    }

    public ResistorBandValue getThirdBand() {
        return thirdBand;
    }

    public void setThirdBand(ResistorBandValue thirdBand) {
        this.thirdBand = thirdBand;
    }

    public ResistorBandValue getMultiplierBand() {
        return multiplierBand;
    }

    public void setMultiplierBand(ResistorBandValue multiplierBand) {
        this.multiplierBand = multiplierBand;
    }

    public ResistorBandValue getToleranceBand() {
        return toleranceBand;
    }

    public void setToleranceBand(ResistorBandValue toleranceBand) {
        this.toleranceBand = toleranceBand;
    }

    public ResistorBandValue getPpmBand() {
        return ppmBand;
    }

    public void setPpmBand(ResistorBandValue ppmBand) {
        this.ppmBand = ppmBand;
    }

    public ResistorBandType getBandType() {
        return bandType;
    }

    public void setBandType(ResistorBandType bandType) {
        this.bandType = bandType;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
