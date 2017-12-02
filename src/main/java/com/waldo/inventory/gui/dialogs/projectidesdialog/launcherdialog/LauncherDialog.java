package com.waldo.inventory.gui.dialogs.projectidesdialog.launcherdialog;

import com.waldo.inventory.gui.Application;

public class LauncherDialog extends LauncherDialogLayout {


    public LauncherDialog(Application application, String title, boolean useDefaultLauncher, String launcherPath) {
        super(application, title, useDefaultLauncher, launcherPath);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    public boolean isUseDefaultLauncher() {
        return useDefaultLauncherCb.isSelected();
    }

    public String getLauncherPath() {
        return launcherPathTf.getText();
    }

}
