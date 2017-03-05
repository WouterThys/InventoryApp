package com.waldo.inventory.Utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Error {
    public static final int NO_ERROR = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;

    private ResourceManager resourceManager;
    private int errorType;
    private String message;

    public Error(int errorType, String message) {
        this.errorType = errorType;
        this.message = message;

        URL url = Error.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
    }

    protected int getErrorType() {
        return errorType;
    }

    protected String getMessage() {
        return message;
    }

    public Color getColor() {
        switch (errorType) {
            case ERROR:
                return new Color(resourceManager.readInteger("ErrorProvider.ErrorColor", 16));
            case INFO:
                return new Color(resourceManager.readInteger("ErrorProvider.InfoColor", 16));
            case NO_ERROR:
                return Color.WHITE;
            case WARNING:
                return new Color(resourceManager.readInteger("ErrorProvider.WarningColor", 16));
            default:
                throw new IllegalArgumentException("Not a valid error type");
        }
    }

    public ImageIcon getImage() {
        switch (errorType) {
            case ERROR:
                return resourceManager.readImage("ErrorProvider.ErrorIcon");
            case INFO:
                return resourceManager.readImage("ErrorProvider.InfoIcon");
            case NO_ERROR:
                return null;
            case WARNING:
                return resourceManager.readImage("ErrorProvider.WarningIcon");
            default:
                throw new IllegalArgumentException("Not a valid error type");
        }
    }
}
