package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.FileSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class FilesPanel extends SettingsPnl<FileSettings> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private GuiUtils.IBrowseFilePanel distributorsPathPnl;
    private GuiUtils.IBrowseFilePanel divisionsPathPnl;
    private GuiUtils.IBrowseFilePanel idesPathPnl;
    private GuiUtils.IBrowseFilePanel itemsPathPnl;
    private GuiUtils.IBrowseFilePanel manufacturersPathPnl;
    private GuiUtils.IBrowseFilePanel projectsPathPnl;
    private GuiUtils.IBrowseFilePanel orderFilesPathPnl;



    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public FilesPanel(IDialog dialog) {
        super(dialog, settings().getFileSettings());
        settings().addFileSettingsListener(this);

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected List<FileSettings> getAllSettingsList() {
        return settings().getFileSettingsList();
    }

    @Override
    protected FileSettings getSettingsByName(String name) {
        return settings().getFileSettingsByName(name);
    }

    @Override
    protected FileSettings createNew(String name) {
        return new FileSettings(name);
    }

    @Override
    protected FileSettings refreshSettings() {
        settings().updateSelectedSettings();
        selectedSettings = settings().getFileSettings();
        return selectedSettings;
    }

    @Override
    protected boolean updateEnabledComponents() {
        boolean enabled = super.updateEnabledComponents();

        distributorsPathPnl.setEnabled(enabled);
        divisionsPathPnl.setEnabled(enabled);
        idesPathPnl.setEnabled(enabled);
        itemsPathPnl.setEnabled(enabled);
        manufacturersPathPnl.setEnabled(enabled);
        projectsPathPnl.setEnabled(enabled);
        orderFilesPathPnl.setEnabled(enabled);

        return enabled;
    }

    @Override
    protected void updateFieldValues(FileSettings selectedSettings) {
        super.updateFieldValues(selectedSettings);
        if (selectedSettings != null) {
            distributorsPathPnl.setText(selectedSettings.getImgDistributorsPath());
            divisionsPathPnl.setText(selectedSettings.getImgDivisionsPath());
            idesPathPnl.setText(selectedSettings.getImgIdesPath());
            itemsPathPnl.setText(selectedSettings.getImgItemsPath());
            manufacturersPathPnl.setText(selectedSettings.getImgManufacturersPath());
            projectsPathPnl.setText(selectedSettings.getImgProjectsPath());
            orderFilesPathPnl.setText(selectedSettings.getFileOrdersPath());
        }
    }

    private void setDefaultPaths() {
        if (selectedSettings == null) {
            distributorsPathPnl.setDefaultPath("home/");
            divisionsPathPnl.setDefaultPath("home/");
            idesPathPnl.setDefaultPath("home/");
            itemsPathPnl.setDefaultPath("home/");
            manufacturersPathPnl.setDefaultPath("home/");
            projectsPathPnl.setDefaultPath("home/");
            orderFilesPathPnl.setDefaultPath("home/");
        } else {
            distributorsPathPnl.setDefaultPath(selectedSettings.getImgDistributorsPath());
            divisionsPathPnl.setDefaultPath(selectedSettings.getImgDivisionsPath());
            idesPathPnl.setDefaultPath(selectedSettings.getImgIdesPath());
            itemsPathPnl.setDefaultPath(selectedSettings.getImgItemsPath());
            manufacturersPathPnl.setDefaultPath(selectedSettings.getImgManufacturersPath());
            projectsPathPnl.setDefaultPath(selectedSettings.getImgProjectsPath());
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();

        // Text fields
        distributorsPathPnl = new GuiUtils.IBrowseFilePanel();
        divisionsPathPnl = new GuiUtils.IBrowseFilePanel();
        idesPathPnl = new GuiUtils.IBrowseFilePanel();
        itemsPathPnl = new GuiUtils.IBrowseFilePanel();
        manufacturersPathPnl = new GuiUtils.IBrowseFilePanel();
        projectsPathPnl = new GuiUtils.IBrowseFilePanel();
        orderFilesPathPnl = new GuiUtils.IBrowseFilePanel();

        distributorsPathPnl.addFieldEditedListener(this, "imgDistributorsPath");
        divisionsPathPnl.addFieldEditedListener(this, "imgDivisionsPath");
        idesPathPnl.addFieldEditedListener(this, "imgIdesPath");
        itemsPathPnl.addFieldEditedListener(this, "imgItemsPath");
        manufacturersPathPnl.addFieldEditedListener(this, "imgManufacturersPath");
        projectsPathPnl.addFieldEditedListener(this, "imgProjectsPath");
        orderFilesPathPnl.addFieldEditedListener(this, "fileOrdersPath");
    }

    @Override
    public void initializeLayouts() {
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        // - Add to panel
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingsPanel, 160);

        gbc.addLine("Distributor images: ", distributorsPathPnl);
        gbc.addLine("Division images: ", divisionsPathPnl);
        gbc.addLine("IDE images: ", idesPathPnl);
        gbc.addLine("Item images: ", itemsPathPnl);
        gbc.addLine("Manufacturer images: ", manufacturersPathPnl);
        gbc.addLine("Project images: ", projectsPathPnl);
        gbc.addLine("Order file images: ", orderFilesPathPnl);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createTitleBorder("File options"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(settingsPanel, BorderLayout.CENTER);

        super.initializeLayouts();
    }

    @Override
    public void updateComponents(Object... object) {
        Application.beginWait(FilesPanel.this);
        try {
            if (object.length > 0) {
                if (object[0] instanceof SettingsManager) {
                    selectedSettings = ((SettingsManager) object[0]).getFileSettings();
                } else {
                    selectedSettings = (FileSettings) object[0];
                }
            }

            if (selectedSettings != null) {
                cbSelectSettings(selectedSettings);
                originalSettings = selectedSettings.createCopy();
                updateFieldValues(selectedSettings);
            } else {
                originalSettings = null;
                setDefaultPaths();
            }
            updateEnabledComponents();
        } finally {
            Application.endWait(FilesPanel.this);
        }
    }
}
    