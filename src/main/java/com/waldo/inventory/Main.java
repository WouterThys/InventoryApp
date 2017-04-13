package com.waldo.inventory;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {

        LOG.info("\n \t Starting application \n *******************************************************************\n");

        DbManager.dbInstance().init();
        DbManager.dbInstance().registerShutDownHook();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLookAndFeel();

                Application app = new Application();
                app.setTitle("Inventory");
                app.setMinimumSize(new Dimension(800,600));
                app.setLocationByPlatform(true);
                //app.setExtendedState(JFrame.MAXIMIZED_BOTH);
                app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                app.setVisible(true);
            }
        });
    }

    private static void setLookAndFeel() {
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
//            e.printStackTrace();
//        }
    }
}

