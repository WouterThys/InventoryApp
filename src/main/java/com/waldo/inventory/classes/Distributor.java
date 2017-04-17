package com.waldo.inventory.classes;

public class Distributor extends DbObject {

    public static final String TABLE_NAME = "distributors";

    private String website;

    public Distributor() {
        super(TABLE_NAME);
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
