package com.waldo.inventory.classes.dbclasses;

import java.io.File;

public interface Orderable {
    long getId();
    void save();
    void setAmount(int amount);
    int getAmount();
    void updateOrderState();
    File getRemarksFile();
    AbstractOrderLine createOrderLine(AbstractOrder order);
}
