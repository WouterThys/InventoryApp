package com.waldo.inventory.gui.dialogs.projectidesdialog.launcherdialog;

import java.awt.*;

public class LauncherDialog extends LauncherDialogLayout {


    public LauncherDialog(Window parent, String title, boolean useDefaultLauncher, String launcherPath) {
        super(parent, title, useDefaultLauncher, launcherPath);

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
