package com.waldo.inventory.gui.dialogs.settingsdialog;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.settingsdialog.panels.DbPanel;
import com.waldo.inventory.gui.dialogs.settingsdialog.panels.FilesPanel;
import com.waldo.inventory.gui.dialogs.settingsdialog.panels.LogsPanel;

import javax.swing.*;

import static com.waldo.inventory.database.settings.SettingsManager.*;
import static com.waldo.inventory.gui.Application.imageResource;


public abstract class SettingsDialogLayout extends IDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITabbedPane tabbedPane;

    DbPanel dbPanel;
    FilesPanel filesPanel;
    LogsPanel logsPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SettingsDialogLayout(Application application, String title) {
        super(application, title);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */




    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Title
        setTitleIcon(imageResource.readImage("SettingsDialog.TitleIcon"));
        setTitleName("Settings");

        // Panels
        dbPanel = new DbPanel(application);
        filesPanel = new FilesPanel(application);
        logsPanel = new LogsPanel(application);

        // Tabbed pane
        tabbedPane = new ITabbedPane(ITabbedPane.LEFT);
        ITabbedPane.AbstractTabRenderer renderer = (ITabbedPane.AbstractTabRenderer) tabbedPane.getTabRenderer();
        renderer.setPrototypeText("Database");
        renderer.setHorizontalTextAlignment(SwingConstants.TRAILING);
    }

    @Override
    public void initializeLayouts() {
        // Add tabs
        tabbedPane.addTab("Database ", imageResource.readImage("SettingsDialog.DbIcon"), dbPanel, "Database settings");
        tabbedPane.addTab("Files ", imageResource.readImage("SettingsDialog.FileIcon"), filesPanel, "File settings");
        tabbedPane.addTab("Logs ", imageResource.readImage("SettingsDialog.LogIcon"), logsPanel, "Log settings");

        getContentPanel().add(tabbedPane);
        pack();
    }

    @Override
    public void updateComponents(Object object) {

        dbPanel.updateComponents(settings().getDbSettings());
        filesPanel.updateComponents(settings().getFileSettings());
        logsPanel.updateComponents(settings().getLogSettings());

    }
}
