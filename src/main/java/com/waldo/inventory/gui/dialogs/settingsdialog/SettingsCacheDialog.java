package com.waldo.inventory.gui.dialogs.settingsdialog;

import com.waldo.inventory.gui.Application;

public class SettingsCacheDialog extends SettingsCacheDialogLayout {

    public SettingsCacheDialog(Application application, String title, boolean onError) {
        super(application, title, onError);

        initializeComponents();
        initializeLayouts();

        updateComponents();
    }
}
