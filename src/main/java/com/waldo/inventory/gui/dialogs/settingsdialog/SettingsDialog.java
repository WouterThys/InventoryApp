package com.waldo.inventory.gui.dialogs.settingsdialog;

import com.waldo.inventory.gui.Application;

public class SettingsDialog extends SettingsDialogLayout {

    public SettingsDialog(Application application, String title, boolean onError) {
        super(application, title, onError);

        initializeComponents();
        initializeLayouts();

        updateComponents();
    }
}
