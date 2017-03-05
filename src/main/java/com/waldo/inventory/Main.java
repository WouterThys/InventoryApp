package com.waldo.inventory;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.ItemDbManager;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.sql.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException {

        DbManager.getInstance().init();
        DbManager.getInstance().registerShutDownHook();

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
