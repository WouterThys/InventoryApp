package com.waldo.inventory.gui.components;

import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.gui.GuiInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class IStatusStrip extends JPanel implements GuiInterface {

    private static final LogManager LOG = LogManager.LOG(IStatusStrip.class);
    private static final IStatusStrip INSTANCE = new IStatusStrip();
    private static final int ERROR_TIME = 5000; // milli seconds
    private static final int WARN_TIME = 3000; // milli seconds
    private static final int MESSAGE_TIME = 2000; // milli seconds

    public static IStatusStrip Status() {
        return INSTANCE;
    }


    private JLabel statusLabel;
    private Timer timer;


    private IStatusStrip() {}

    public void init() {
        initializeComponents();
        initializeLayouts();

        timer = new Timer(MESSAGE_TIME, e -> clear());
        timer.setRepeats(false);
    }

    public void setMessage(String message) {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText(message);
        runTimer(MESSAGE_TIME);
    }

    public void holdMessage(String message) {
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setText(message);
    }

    public void setWarning(String warning) {
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

    @Override
    public void initializeComponents() {
        statusLabel = new JLabel("", JLabel.LEFT);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(3,10,3,3));
        add(statusLabel, BorderLayout.WEST);
    }

    @Override
    public void updateComponents(Object object) {}
}
