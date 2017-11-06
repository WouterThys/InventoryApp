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
//                    defaults.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", null);
//                    defaults.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", null);
//                    defaults.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", null);
//                    defaults.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", null);

                    return defaults;
                }
            });
        } catch (UnsupportedLookAndFeelException e) {
            LOG.error("Error settings look and feel.", e);
        }


//        UIManager.put("Table.selectionForeground", Color.YELLOW);
//        try {
//            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getNameText())) {
//                    UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            // If Nimbus is not available, you can set the GUI to another look and feel.
//        }
        //setAllFonts(new FontUIResource("sansserif", Font.PLAIN, 15));

//        SynthLookAndFeel lookAndFeel = new SynthLookAndFeel();
//
//        try {
//            InputStream stream = Main.class.getResourceAsStream("/lookandfeel.xml");
//            lookAndFeel.load(stream, Main.class);
//            UIManager.setLookAndFeel(lookAndFeel);
//        } catch (Exception e) {
//            System.err.println("Couldn't get specified look and feel ("
//                    + lookAndFeel
//                    + "), for some reason.");
//            System.err.println("Using the default look and feel.");
//        }
    }

    private static void setAllFonts(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    private static void updateLookAndFeel() {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    LOG.error("Error loading Nimbus look and feel", e);
                }
                UIManager.getLookAndFeelDefaults().put(
                        "TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", null);
                UIManager.getLookAndFeelDefaults().put(
                        "TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", null);
                UIManager.getLookAndFeelDefaults().put(
                        "TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", null);
                UIManager.getLookAndFeelDefaults().put(
                        "TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", null);
                break;
            }
        }
    }

    public static void closeApplication(int status) {
        System.exit(status);
    }


}

