package com.waldo.inventory.database.settings.settingsclasses;

import com.waldo.inventory.database.settings.SettingsManager;

public abstract class DbSettingsObject {

    protected String name;


    public boolean isDefault() {
        return name.equals(SettingsManager.DEFAULT);
    }

    public abstract DbSettingsObject creatCopy(DbSettingsObject original);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
