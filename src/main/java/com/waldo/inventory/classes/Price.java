package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.Statics;

public class Price {

    private double price;
    private Statics.PriceUnits priceUnits;

    public Price() {

    }

    public Price(double price, Statics.PriceUnits priceUnits) {
        this.price = price;
        this.priceUnits = priceUnits;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Statics.PriceUnits getPriceUnits() {
        return priceUnits;
    }

    public void setPriceUnits(Statics.PriceUnits priceUnits) {
        this.priceUnits = priceUnits;
    }
}
