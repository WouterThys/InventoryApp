package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.resource.ImageResource;
import com.waldo.inventory.managers.LogManager;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.database.DatabaseAccess.db;
import static com.waldo.inventory.gui.Application.imageResource;

public class IStatusStrip extends JPanel implements GuiUtils.GuiInterface {

    private static final LogManager LOG = LogManager.LOG(IStatusStrip.class);
    private static final IStatusStrip INSTANCE = new IStatusStrip();
    private static final int ERROR_TIME = 5000; // milli seconds
    private static final int WARN_TIME = 3000; // milli seconds
    private static final int MESSAGE_TIME = 2000; // milli seconds

    public static IStatusStrip Status() {
        return INSTANCE;
    }

    private JLabel statusLabel;
    private JLabel dbConnectionLabel;

    private JLabel dbConnectedIcon;
    private JLabel imageConnectedIcon;

    private Timer timer;

    private IStatusStrip() {}

    public void init() {
        initializeComponents();
        initializeLayouts();

        timer = new Timer(MESSAGE_TIME, e -> clear());
        timer.setRepeats(false);
    }

    public void setDbConnectionText(boolean connected, String ip, String dbName, String userName) {
        if (connected) {
            dbConnectionLabel.setText("Connected to " + dbName + " as " + userName + " at " + ip);
            dbConnectionLabel.setForeground(Color.GRAY);
        } else {
            dbConnectionLabel.setText("Not connected..");
            dbConnectionLabel.setForeground(Color.RED);
        }
    }

    public void setMessage(String message) {
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setText(message);
        runTimer(MESSAGE_TIME);
    }

    public void holdMessage(String message) {
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setText(message);
    }

    public void setWarning(String warning) {
        setWarning(warning, null);
    }

    public void setWarning(String warning, Throwable throwable) {
        if (throwable != null) {
            LOG.warning(warning ,throwable);
        } else {
            LOG.warning(warning);
        }
        statusLabel.setForeground(Color.ORANGE);
        statusLabel.setText(warning);
        runTimer(WARN_TIME);
    }

    public void setError(String error) {
        setError(error, null);
    }

    public void setError(String error, Throwable throwable) {
        if (throwable != null) {
            LOG.error(error, throwable);
        } else {
            LOG.error(error);
        }
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(error);
        runTimer(ERROR_TIME);
    }

    private void runTimer(int delay) {
        if (timer.isRunning()) {
            timer.stop();
        }
        timer.setDelay(delay);
        timer.start();
    }

    public void clear() {
        if (timer.isRunning()) {
            timer.stop();
        }
        statusLabel.setText(" ");
    }

//    Status.dbOk
//    Status.dbNok
//    Status.serverOk
//    Status.serverNok

    public void updateConnectionStatus() {
        if (db().isInitialized()) {
            dbConnectedIcon.setIcon(ImageResource.scaleImage(imageResource.readIcon("Status.dbOk"), new Dimension(16,16)));
            dbConnectedIcon.setToolTipText("Connected");
        } else {
            dbConnectedIcon.setIcon(ImageResource.scaleImage(imageResource.readIcon("Status.dbNok"), new Dimension(16,16)));
            dbConnectedIcon.setToolTipText("Not connected..");
        }

        if (imageResource.serverConnected()) {
            imageConnectedIcon.setIcon(ImageResource.scaleImage(imageResource.readIcon("Status.serverOk"), new Dimension(16,16)));
            imageConnectedIcon.setToolTipText("Connected as " + imageResource.getClient().getClientName());
        } else {
            imageConnectedIcon.setIcon(ImageResource.scaleImage(imageResource.readIcon("Status.serverNok"), new Dimension(16,16)));
            imageConnectedIcon.setToolTipText("Not connected..");
        }
    }

    @Override
    public void initializeComponents() {
        statusLabel = new JLabel("", JLabel.LEFT);
        dbConnectionLabel = new JLabel("", JLabel.RIGHT);

        dbConnectedIcon = new JLabel();
        imageConnectedIcon = new JLabel();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel connectionPnl = new JPanel();

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0,2,2,2),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        BorderFactory.createEmptyBorder(3,10,3,20)
                )
        ));

        connectionPnl.add(dbConnectionLabel);
        connectionPnl.add(dbConnectedIcon);
        connectionPnl.add(imageConnectedIcon);

        add(statusLabel, BorderLayout.WEST);
        add(connectionPnl, BorderLayout.EAST);
    }

    @Override
    public void updateComponents(Object... object) {}
}
