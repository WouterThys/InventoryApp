package com.waldo.inventory;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {

        LOG.info("\n \t Starting application \n *******************************************************************\n");

        DbManager.dbInstance().init();
        DbManager.dbInstance().registerShutDownHook();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Font f = new Font("sans-serif", Font.PLAIN, 12);
                UIManager.put("Menu.font", f);

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
}
