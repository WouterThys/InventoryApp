package com.waldo.inventory;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.sql.*;

public class Main {

    public static void main(String[] args) throws SQLException {

        DbManager.dbInstance().init();
        DbManager.dbInstance().registerShutDownHook();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Application app = new Application();
                app.setTitle("Inventory");
                app.setExtendedState(JFrame.MAXIMIZED_BOTH);
                app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                app.setVisible(true);
            }
        });
    }
}
