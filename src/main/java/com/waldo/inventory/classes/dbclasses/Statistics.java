package com.waldo.inventory.classes.dbclasses;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Statistics extends DbObject {

    public static final String TABLE_NAME = "statistics";

    // Variables
    private Date creationTime;

    private int itemsCount;
    private int locationsCount;
    private int manufacturersCount;
    private int distributorsCount;
    private int packagesCount;
    private int ordersCount;
    private int projectsCount;


    public Statistics(
            Timestamp creationTime,
            int itemsCount,
            int locationsCount,
            int manufacturersCount,
            int distributorsCount,
            int packagesCount,
            int ordersCount,
            int projectsCount) {
        super(TABLE_NAME);
        this.creationTime = new Date(creationTime.getTime());
        this.itemsCount = itemsCount;
        this.locationsCount = locationsCount;
        this.manufacturersCount = manufacturersCount;
        this.distributorsCount = distributorsCount;
        this.packagesCount = packagesCount;
        this.ordersCount = ordersCount;
        this.projectsCount = projectsCount;

        setCanBeSaved(false);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        return 0;
    }

    @Override
    public Statistics createCopy(DbObject copyInto) {
        return null;
    }

    @Override
    public Statistics createCopy() {
        return null;
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
    }

    // Getters

    public Date getCreationTime() {
        return creationTime;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public int getLocationsCount() {
        return locationsCount;
    }

    public int getManufacturersCount() {
        return manufacturersCount;
    }

    public int getDistributorsCount() {
        return distributorsCount;
    }

    public int getPackagesCount() {
        return packagesCount;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public int getProjectsCount() {
        return projectsCount;
    }
}