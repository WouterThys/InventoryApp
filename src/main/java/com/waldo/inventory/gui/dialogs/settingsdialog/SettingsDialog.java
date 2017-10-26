package com.waldo.inventory.gui.dialogs.settingsdialog;

import com.waldo.inventory.gui.Application;

public class SettingsDialog extends SettingsDialogLayout {

    public SettingsDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();

        updateComponents();
    }
}
