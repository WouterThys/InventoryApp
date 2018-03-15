package com.waldo.inventory;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.managers.LogManager;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class Main {

    // TODO: smart list in db that clears itself after time -> create cache tab
    // TODO: setting for logging session variables in DbQueue -> in logging tab
    // TODO: setting for logging db history -> logging tab
    // TODO: closing down application with wait process and doing all the logs
    // TODO: create cached settings object for above stuff

    // TODO: right click on projects
    // TODO: retest aud projects and project objects (codes, pcbs, ...)

    //

    private static final LogManager LOG = LogManager.LOG(Main.class);

    private static final String CO = "CACHE_ONLY";
    private static final String DM = "DEBUG_MODE";
    private static final String FS = "FULL_SCREEN";
    private static final String LH = "LOG_HISTORY";

    public static boolean CACHE_ONLY = false;
    public static boolean DEBUG_MODE = false;
    public static boolean FULL_SCREEN = false;
    public static boolean LOG_HISTORY = false;

    private static Application app;

    public static void main(String[] args) throws SQLException {
        String startUpPath = new File("").getAbsolutePath() + File.separator;
        LOG.startup(startUpPath);

        readArguments(args);

        SwingUtilities.invokeLater(() -> {
            setLookAndFeel();
            app = new Application(startUpPath);
            app.setTitle("Inventory");
            app.setLocationByPlatform(true);
            app.setPreferredSize(new Dimension(1600, 800));
            app.setMinimumSize(new Dimension(800, 400));
            if (FULL_SCREEN || settings().getGeneralSettings().isGuiStartUpFullScreen()) {
                app.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setLookAndFeel(settings().getGeneralSettings().getGuiLookAndFeel());
            app.initComponents();
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
                        case LH:
                            LOG_HISTORY = Boolean.valueOf(value);
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

    public static void setLookAndFeel(String settingsName) {
        if (app != null) {
            try {
                String lfName = "";

                NimbusLookAndFeel nimbusLookAndFeel = new NimbusLookAndFeel() ;
//                {
//                    @Override
//                    public UIDefaults getDefaults() {
//                        UIDefaults defaults = super.getDefaults();
//                        defaults.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 15));
//                        return defaults;
//                    }
//                };

                if (settingsName != null && !settingsName.isEmpty()) {
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if (info.getName().equals(settingsName)) {
                            lfName = info.getClassName();
                            break;
                        }
                    }

                    if (!lfName.isEmpty()) {
                        UIManager.setLookAndFeel(lfName);
                    } else {
                        UIManager.setLookAndFeel(nimbusLookAndFeel);
                    }
                } else {
                    UIManager.setLookAndFeel(nimbusLookAndFeel);
                }


                SwingUtilities.updateComponentTreeUI(app);
                UIManager.getLookAndFeelDefaults().put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 15));
                app.pack();

            } catch (UnsupportedLookAndFeelException e) {
                LOG.error("Error settings look and feel.", e);
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeApplication(int status) {
        System.exit(status);
    }


}

