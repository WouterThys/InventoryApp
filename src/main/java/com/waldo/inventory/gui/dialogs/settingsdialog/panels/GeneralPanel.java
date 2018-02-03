package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.GeneralSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICheckBox;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;

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

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public GeneralPanel(IDialog dialog) {
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

        return enabled;
    }

    @Override
    protected void updateFieldValues(GeneralSettings selectedSettings) {
        super.updateFieldValues(selectedSettings);
        if (selectedSettings != null) {
            detailsViewCb.setSelectedItem(selectedSettings.getGuiDetailsView());
            lookAndFeelCb.setSelectedItem(selectedSettings.getGuiLookAndFeel());
            fullScreenCb.setSelected(selectedSettings.isGuiStartUpFullScreen());
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

        detailsViewCb.addEditedListener(this, "guiDetailsView", String.class);
        lookAndFeelCb.addEditedListener(this, "guiLookAndFeel", String.class);
        fullScreenCb.addEditedListener(this, "guiStartUpFullScreen");
    }

    @Override
    public void initializeLayouts() {
        JPanel settingsPanel = new JPanel();

        // Add to panel
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingsPanel, 160);
        gbc.addLine("Details panel position: ", detailsViewCb);
        gbc.addLine("Look and feel: ", lookAndFeelCb);
        gbc.addLine("Start up full screen: ", fullScreenCb);

        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createTitleBorder("Gui options"),
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
