package com.waldo.inventory.database.interfaces;

import com.waldo.inventory.database.settings.settingsclasses.DbSettingsObject;

public interface DbSettingsListener<T extends DbSettingsObject> {
    void onSettingsChanged(T newSettings);
}
