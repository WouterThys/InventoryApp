package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.GeneralSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.utils.icomponents.ICheckBox;
import com.waldo.utils.icomponents.IComboBox;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class GeneralPanel extends SettingsPnl<GeneralSettings> {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<Statics.GuiDetailsView> detailsViewCb;
    private IComboBox<String> lookAndFeelCb;
    private ICheckBox fullScreenCb;
    private ICheckBox debugCb;
    private ICheckBox logHistoryCb;
    private ICheckBox autoOrderCb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public GeneralPanel(iDialog dialog) {
        super(dialog, settings().getGeneralSettings());

        initializeComponents();
        initializeLayouts();

        settings().addGeneralSettingsListener(this);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    protected List<GeneralSettings> getAllSettingsList() {
        return settings().getGeneralSettingsList();
    }

    @Override
    protected GeneralSettings getSettingsByName(String name) {
        return settings().getGeneralSettingsByName(name);
    }

    @Override
    protected GeneralSettings createNew(String name) {
        return new GeneralSettings(name);
    }

    @Override
    protected GeneralSettings refreshSettings() {
        settings().updateSelectedSettings();
        selectedSettings = settings().getGeneralSettings();
        return selectedSettings;
    }

    @Override
    protected boolean updateEnabledComponents() {
        boolean enabled = super.updateEnabledComponents();

        detailsViewCb.setEnabled(enabled);
        lookAndFeelCb.setEnabled(enabled);
        fullScreenCb.setEnabled(enabled);
        debugCb.setEnabled(enabled);
        autoOrderCb.setEnabled(enabled);
        logHistoryCb.setEnabled(enabled);

        return enabled;
    }

    @Override
    protected void updateFieldValues(GeneralSettings selectedSettings) {
        super.updateFieldValues(selectedSettings);
        if (selectedSettings != null) {
            detailsViewCb.setSelectedItem(selectedSettings.getGuiDetailsView());
            lookAndFeelCb.setSelectedItem(selectedSettings.getGuiLookAndFeel());
            fullScreenCb.setSelected(selectedSettings.isGuiStartUpFullScreen());
            debugCb.setSelected(Main.DEBUG_MODE);
            autoOrderCb.setSelected(selectedSettings.isAutoOrderEnabled());
            logHistoryCb.setSelected(Main.LOG_HISTORY);
        }
    }

    @Override
    public void onSettingsChanged(GeneralSettings newSettings) {
        super.onSettingsChanged(newSettings);
        SwingUtilities.invokeLater(() -> {
            try {
                final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                final File currentJar = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                String message = "These settings will take effect after restarting the application.";
                boolean canRestart = false;
                if (currentJar.getName().endsWith(".jar")) {
                    message +=  "Restart now?";
                    canRestart = true;
                }
                int res = JOptionPane.showConfirmDialog(
                        GeneralPanel.this,
                        message,
                        "Settings changed",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (canRestart && res == JOptionPane.OK_OPTION) {
                    restartApplication(currentJar, javaBin);
                }
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void restartApplication(File currentJar, String javaBin) throws IOException, URISyntaxException {
        /* is it a jar file? */
        if(!currentJar.getName().endsWith(".jar")) {
            return;
        }

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }

    /*
                     *                  LISTENERS
                     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();

        detailsViewCb = new IComboBox<>(Statics.GuiDetailsView.values());
        DefaultComboBoxModel<String> listModel = new DefaultComboBoxModel<>();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            listModel.addElement(info.getName());
        }
        lookAndFeelCb = new IComboBox<>(listModel);
        fullScreenCb = new ICheckBox();
        debugCb = new ICheckBox();
        logHistoryCb = new ICheckBox();
        autoOrderCb = new ICheckBox();

        debugCb.addActionListener(e -> Main.DEBUG_MODE = debugCb.isSelected());
        logHistoryCb.addActionListener(e -> Main.LOG_HISTORY = logHistoryCb.isSelected());

        detailsViewCb.addEditedListener(this, "guiDetailsView", String.class);
        lookAndFeelCb.addEditedListener(this, "guiLookAndFeel", String.class);
        fullScreenCb.addEditedListener(this, "guiStartUpFullScreen");
        autoOrderCb.addEditedListener(this, "autoOrderEnabled");
    }

    @Override
    public void initializeLayouts() {
        JPanel settingsPanel = new JPanel();

        // Add to panel
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingsPanel, 180);
        gbc.addLine("Details panel position: ", detailsViewCb);
        gbc.addLine("Look and feel: ", lookAndFeelCb);
        gbc.addLine("Start up full screen: ", fullScreenCb);
        gbc.addLine("Debug mode: ", debugCb);
        gbc.addLine("Log history: ", logHistoryCb);
        gbc.addLine("Auto order: ", autoOrderCb);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createInlineTitleBorder("Gui options"),
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        // Content panel
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(settingsPanel, BorderLayout.NORTH);

        super.initializeLayouts();
    }

    @Override
    public void updateComponents(Object... args) {
        Application.beginWait(GeneralPanel.this);
        try {
            if (args.length > 0) {
                if (args[0] instanceof SettingsManager) {
                    selectedSettings = ((SettingsManager) args[0]).getGeneralSettings();
                } else {
                    selectedSettings = (GeneralSettings) args[0];
                }
            }

            if (selectedSettings != null) {
                cbSelectSettings(selectedSettings);
                originalSettings = selectedSettings.createCopy();
                updateFieldValues(selectedSettings);
            } else {
                originalSettings = null;
            }
            updateEnabledComponents();
        } finally {
            Application.endWait(GeneralPanel.this);
        }
    }
}
