package com.waldo.inventory.gui.dialogs.settingsdialog.panels;

import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.database.settings.settingsclasses.ImageServerSettings;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.dialogs.imagedialogs.selectimagedialog.SelectImageDialog;
import com.waldo.inventory.gui.dialogs.imagedialogs.sendfullcontentdialog.SendFullContentDialog;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.test.client.Client;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;

public class ImageServerPanel extends SettingsPnl<ImageServerSettings> implements Client.ImageClientListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ICheckBox imageServerEnabledCb;
    private IComboBox<Statics.ImageServerTypes> imageServerTypesCb;

    // Server
    private ITextField imageServerNameTf;
    private ITextField connectAsNameTf;
    private ILabel connectionStateLbl;

    private IActions.ConnectAction connectAction;
    private IActions.DisconnectAction disconnectAction;
    private IActions.SendContentAction sendContentAction;

    // Database
    private ITextField imageServerDbNameTf;
    private ITextField imageServerDbIpTf;
    private ITextField imageServerDbUserTf;
    private ITextField imageServerDbPwTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ImageServerPanel(iDialog dialog) {
        super(dialog, settings().getImageServerSettings());
        settings().addImageServerSettingsListener(this);

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void sendFolderContent() {
        File folder = null;
        ImageType type = null;

        if (!imageResource.getClient().isConnected()) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Client is not connected..",
                    "Not connected",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Folder
        SelectImageDialog selectImageDialog = new SelectImageDialog(parent, true);
        if (selectImageDialog.showDialog() == IDialog.OK) {
            folder = selectImageDialog.getSelectedFile();
            type = selectImageDialog.getImageType();
        }

        if (folder != null && folder.exists() && folder.isDirectory() && type != null) {
            // Send to client
            SendFullContentDialog dialog = new SendFullContentDialog(parent, imageResource.getClient(), folder, type);
            dialog.showDialog();
        }
    }

    @Override
    protected List<ImageServerSettings> getAllSettingsList() {
        return settings().getImageServerSettingsList();
    }

    @Override
    protected ImageServerSettings getSettingsByName(String name) {
        return settings().getImageServerSettingsByName(name);
    }

    @Override
    protected ImageServerSettings createNew(String name) {
        return new ImageServerSettings(name);
    }

    @Override
    protected ImageServerSettings refreshSettings() {
        settings().updateSelectedSettings();
        selectedSettings = settings().getImageServerSettings();
        return selectedSettings;
    }

    @Override
    protected boolean updateEnabledComponents() {
        boolean enabled = super.updateEnabledComponents();
        boolean serverEnabled = imageServerEnabledCb.isSelected();

        boolean seType = enabled && selectedSettings.getType().equals(Statics.ImageServerTypes.Server);
        boolean dbType = enabled && selectedSettings.getType().equals(Statics.ImageServerTypes.Database);

        imageServerNameTf.setEnabled(seType && serverEnabled);
        connectAsNameTf.setEnabled(seType && serverEnabled);
        connectAction.setEnabled(seType && serverEnabled);
        disconnectAction.setEnabled(seType && serverEnabled);
        sendContentAction.setEnabled(seType && serverEnabled);

        imageServerDbNameTf.setEnabled(dbType && serverEnabled);
        imageServerDbIpTf.setEnabled(dbType && serverEnabled);
        imageServerDbUserTf.setEnabled(dbType && serverEnabled);
        imageServerDbPwTf.setEnabled(dbType && serverEnabled);


        return enabled;
    }

    @Override
    protected void updateFieldValues(ImageServerSettings selectedSettings) {
        super.updateFieldValues(selectedSettings);
        if (selectedSettings != null) {
            imageServerTypesCb.setSelectedItem(selectedSettings.getType());

            imageServerNameTf.setText(selectedSettings.getImageServerName());
            connectAsNameTf.setText(selectedSettings.getConnectAsName());
            connectionStateLbl.setText(imageResource.serverConnected() ? "Connected" : "Not connected");

            imageServerDbNameTf.setText(selectedSettings.getImageDbSettings().getDbName());
            imageServerDbIpTf.setText(selectedSettings.getImageDbSettings().getDbIp());
            imageServerDbUserTf.setText(selectedSettings.getImageDbSettings().getDbUserName());
            imageServerDbPwTf.setText("***");
        } else {
            imageServerTypesCb.setSelectedItem(Statics.ImageServerTypes.Unknown);

            imageServerNameTf.setText("");
            connectAsNameTf.setText("");
            connectionStateLbl.setText("");

            imageServerDbNameTf.setText("");
            imageServerDbIpTf.setText("");
            imageServerDbUserTf.setText("");
            imageServerDbPwTf.setText("");
        }
    }


    private JPanel createServerLayout() {
        JPanel serverPanel = new JPanel(new BorderLayout());
        JPanel settingsPanel = new JPanel();
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.add(GuiUtils.createNewToolbar(connectAction, disconnectAction, sendContentAction), BorderLayout.EAST);

        // - Add to panel
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingsPanel, 160);

        gbc.addLine("Server name: ", imageServerNameTf);
        gbc.addLine("Connect as: ", connectAsNameTf);
        gbc.addLine("State: ", connectionStateLbl);

        serverPanel.setBorder(GuiUtils.createInlineTitleBorder("Server"));

        serverPanel.add(settingsPanel, BorderLayout.CENTER);
        serverPanel.add(toolbarPanel, BorderLayout.SOUTH);

        return serverPanel;
    }

    private JPanel createDatabasePanel() {
        JPanel dbPanel = new JPanel(new BorderLayout());
        JPanel settingsPanel = new JPanel();

        // - Add to panel
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(settingsPanel, 160);

        gbc.addLine("Db name: ", imageServerDbNameTf);
        gbc.addLine("Db ip: ", imageServerDbIpTf);
        gbc.addLine("Db user: ", imageServerDbUserTf);
        gbc.addLine("Db pw: ", imageServerDbPwTf);

        dbPanel.setBorder(GuiUtils.createInlineTitleBorder("Database"));
        dbPanel.add(settingsPanel, BorderLayout.CENTER);

        return dbPanel;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        super.initializeComponents();

        imageServerEnabledCb = new ICheckBox("Enabled", Main.IMAGE_SERVER);
        imageServerEnabledCb.addActionListener(e -> {
            if (imageResource.serverConnected()) {
                if (imageResource.getClient() != null) {
                    imageResource.getClient().disconnectClient(true);
                }
            }
            Main.IMAGE_SERVER = imageServerEnabledCb.isSelected();
            updateEnabledComponents();
        });

        imageServerNameTf = new ITextField(this, "imageServerName");
        connectAsNameTf = new ITextField(this, "connectAsName");
        connectionStateLbl = new ILabel();

        // Actions
        connectAction = new IActions.ConnectAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageResource.updateImageServerConnection();
                imageResource.getClient().addImageClientListener(ImageServerPanel.this);
            }
        };
        disconnectAction = new IActions.DisconnectAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageResource.getClient().disconnectClient(false);
            }
        };
        sendContentAction = new IActions.SendContentAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> sendFolderContent());
            }
        };

        imageServerTypesCb = new IComboBox<>(Statics.ImageServerTypes.values());
        imageServerTypesCb.addItemListener(e -> {
            SwingUtilities.invokeLater(() -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ImageServerPanel.this.onValueChanged(imageServerTypesCb, "type", "", "");
                    updateEnabledComponents();
                }
            });
        });

        imageServerDbNameTf = new ITextField(this, "dbName");
        imageServerDbIpTf = new ITextField(this, "dbIp");
        imageServerDbUserTf = new ITextField(this, "dbUser");
        imageServerDbPwTf = new ITextField(this, "dbPw");

    }

    @Override
    public void initializeLayouts() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                GuiUtils.createInlineTitleBorder("Image server options"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));


        JPanel serverPanel = createServerLayout();
        JPanel dbPanel = createDatabasePanel();

        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.add(imageServerEnabledCb, BorderLayout.WEST);
        optionsPanel.add(imageServerTypesCb, BorderLayout.EAST);

        Box box = Box.createVerticalBox();
        box.add(serverPanel);
        box.add(dbPanel);

        mainPanel.add(optionsPanel, BorderLayout.PAGE_START);
        mainPanel.add(box, BorderLayout.CENTER);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        super.initializeLayouts();
    }

    @Override
    public void updateComponents(Object... object) {
        Application.beginWait(ImageServerPanel.this);
        try {
            if (object.length > 0) {
                if (object[0] instanceof SettingsManager) {
                    selectedSettings = ((SettingsManager) object[0]).getImageServerSettings();
                } else {
                    selectedSettings = (ImageServerSettings) object[0];
                }
            }

            if (imageResource.getClient() != null) {
                imageResource.getClient().addImageClientListener(this);
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
            Application.endWait(ImageServerPanel.this);
        }
    }


    //
    // Image client listener
    //

    @Override
    public void onConnected(String clientName) {
        connectionStateLbl.setText("Connected");
    }

    @Override
    public void onDisconnected(String clientName) {
        connectionStateLbl.setText("Disconnected");
    }

    @Override
    public void onImageTransmitted(String imageName, ImageType imageType) {

    }

    @Override
    public void onImageReceived(BufferedImage bufferedImage, String imageName, ImageType imageType) {

    }
}
    