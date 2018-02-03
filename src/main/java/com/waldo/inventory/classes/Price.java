package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Price {

    private double value;
    private Statics.PriceUnits priceUnits;

    public Price() {

    }

    public Price(double value, Statics.PriceUnits priceUnits) {
        this.value = value;
        this.priceUnits = priceUnits;
    }

    @Override
    public String toString() {
        if (value != 0) {
            return priceUnits + String.valueOf(round(this).getValue());
        }
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Price) {
            Price p = (Price) obj;
            return p.getValue() == getValue() && p.getPriceUnits() == getPriceUnits();
        }
        return false;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Statics.PriceUnits getPriceUnits() {
        return priceUnits;
    }

    public void setPriceUnits(Statics.PriceUnits priceUnits) {
        this.priceUnits = priceUnits;
    }

    public static Price multiply(Price price, double multiplier) {
        return new Price(price.getValue() * multiplier, price.getPriceUnits());
    }

    public static Price multiply(Price price1, Price price2) {
        if (price1.getPriceUnits() == price2.getPriceUnits()) {
            return new Price(price1.getValue() * price2.getValue(), price1.getPriceUnits());
        }
        return price1;
    }

    public static Price add(Price price, double amount) {
        return new Price (price.getValue() + amount, price.getPriceUnits());
    }

    public static Price add(Price price1, Price price2) {
        if (price1.getPriceUnits() == price2.getPriceUnits()) {
            return new Price(price1.getValue() + price2.getValue(), price1.getPriceUnits());
        }
        return price1;
    }

    public static Price round(Price price) {
        BigDecimal bd = new BigDecimal(price.getValue());
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double rounded = bd.doubleValue();
        return new Price(rounded, price.getPriceUnits());
    }
}
