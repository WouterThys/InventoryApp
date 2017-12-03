package com.waldo.inventory;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.managers.LogManager;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;

public class Main {

    private static final LogManager LOG = LogManager.LOG(Main.class);

    private static final String CO = "CACHE_ONLY";
    private static final String DM = "DEBUG_MODE";
    private static final String FS = "FULL_SCREEN";

    public static boolean CACHE_ONLY = false;
    public static boolean DEBUG_MODE = false;
    public static boolean FULL_SCREEN = false;

    public static void main(String[] args) throws SQLException {
        String startUpPath = new File("").getAbsolutePath() + File.separator;
        LOG.startup(startUpPath);

        readArguments(args);

        SwingUtilities.invokeLater(() -> {
            setLookAndFeel();

            Application app = new Application(startUpPath);
            app.setTitle("Inventory");
            app.setLocationByPlatform(true);
            app.setPreferredSize(new Dimension(1500, 800));
            if (FULL_SCREEN) {
                app.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            app.pack();
            app.setVisible(true);
        });
    }

    private static void readArguments(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                try {
                    System.out.println("Reading main input parameter: " + arg);
                    String[] split = arg.split("=");
                    String param = split[0];
                    String value = split[1];

                    switch (param) {
                        case CO:
                            CACHE_ONLY = Boolean.valueOf(value);
                            break;
                        case DM:
                            DEBUG_MODE = Boolean.valueOf(value);
                            break;
                        case FS:
                            FULL_SCREEN = Boolean.valueOf(value);
                            break;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to read input params: " + e);
                }
            }
        }
    }

    private static void setLookAndFeel() {

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel() {
                @Override
                public UIDefaults getDefaults() {
                    UIDefaults defaults = super.getDefaults();

                    defaults.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 15));
                    return defaults;
                }
            });
        } catch (UnsupportedLookAndFeelException e) {
            LOG.error("Error settings look and feel.", e);
        }

    }

    public static void closeApplication(int status) {
        System.exit(status);
    }


}

