package com.waldo.inventory.gui.dialogs.settingsdialog;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.settingsdialog.panels.DbPanel;
import com.waldo.inventory.gui.dialogs.settingsdialog.panels.FilesPanel;
import com.waldo.inventory.gui.dialogs.settingsdialog.panels.LogsPanel;

import javax.swing.*;

import java.awt.*;

import static com.waldo.inventory.database.settings.SettingsManager.*;
import static com.waldo.inventory.gui.Application.imageResource;


public abstract class SettingsDialogLayout extends IDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITabbedPane tabbedPane;

    private DbPanel dbPanel;
    private FilesPanel filesPanel;
    private LogsPanel logsPanel;

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
        setTitleIcon(imageResource.readImage("Settings.Title"));
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
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));
        // Add tabs
        tabbedPane.addTab("Database ", imageResource.readImage("Settings.Tab.Db"), dbPanel, "Database settings");
        tabbedPane.addTab("Files ", imageResource.readImage("Settings.Tab.File"), filesPanel, "File settings");
        tabbedPane.addTab("Logs ", imageResource.readImage("Settings.Tab.Log"), logsPanel, "Log settings");

        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        getContentPanel().add(separator);
        getContentPanel().add(tabbedPane);
        separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        getContentPanel().add(separator);

        pack();
    }

    @Override
    public void updateComponents(Object object) {

        dbPanel.updateComponents(settings().getDbSettings());
        filesPanel.updateComponents(settings().getFileSettings());
        logsPanel.updateComponents(settings().getLogSettings());

    }
}
