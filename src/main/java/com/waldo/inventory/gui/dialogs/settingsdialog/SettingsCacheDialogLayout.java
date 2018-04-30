package com.waldo.inventory.gui.dialogs.settingsdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.dialogs.settingsdialog.panels.*;
import com.waldo.utils.icomponents.ITabbedPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;


abstract class SettingsCacheDialogLayout extends ICacheDialog implements ChangeListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITabbedPane tabbedPane;

    private GeneralPanel generalPanel;
    private DbPanel dbPanel;
    private ImageServerPanel imageServerPanel;
    private LogsPanel logsPanel;
    private EventsPanel eventsPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final boolean onError;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SettingsCacheDialogLayout(Application application, String title, boolean onError) {
        super(application, title);
        this.onError = onError;
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
        setTitleIcon(imageResource.readIcon("Settings.Title"));
        setTitleName("Settings");

        // Panels
        dbPanel = new DbPanel(this);
        if (!onError) {
            generalPanel = new GeneralPanel(this);
            imageServerPanel = new ImageServerPanel(this);
            logsPanel = new LogsPanel(this);
            eventsPanel = new EventsPanel(this);
        }

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
        if (!onError) {
            tabbedPane.addTab("General ", imageResource.readIcon("Settings.Tab.General"), generalPanel, "General settings");
        }
        tabbedPane.addTab("Database ", imageResource.readIcon("Settings.Tab.Db"), dbPanel, "Database settings");
        if (!onError) {
            tabbedPane.addTab("Images ", imageResource.readIcon("Settings.Tab.Server"), imageServerPanel, "Image server settings");
            tabbedPane.addTab("Logs ", imageResource.readIcon("Settings.Tab.Log"), logsPanel, "Log settings");
            tabbedPane.addTab("Events ", imageResource.readIcon("Settings.Tab.Events"), eventsPanel, "Events settings");
        }
        tabbedPane.addChangeListener(this);

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
    public void updateComponents(Object... object) {
        if (!onError) {
            generalPanel.updateComponents(settings());
        } else {
            dbPanel.updateComponents(settings());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ((GuiUtils.GuiInterface) tabbedPane.getSelectedComponent()).updateComponents(settings());
    }
}