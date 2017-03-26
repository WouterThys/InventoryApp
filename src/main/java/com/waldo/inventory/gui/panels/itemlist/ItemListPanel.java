package com.waldo.inventory.gui.panels.itemlist;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class ItemListPanel extends ItemListPanelLayout {

    private Application application;

    public ItemListPanel(Application application) {
        URL url = ItemListPanel.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
        this.application = application;

        initActions();
        initializeComponents();
        initializeLayouts();
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    private void initActions() {
        initMouseClicked();
        initItemSelectedListener();
        DbManager.dbInstance().addOnItemsChangedListener(this);
    }

    private void initItemSelectedListener() {
        itemSelectedListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if(!e.getValueIsAdjusting()) {
                        int row = itemTable.getSelectedRow();
                        if (row >= 0) {
                            Item selected = null;
                            try {
                                selected = getItemAt(itemTable.getSelectedRow());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                            selectedItem = selected;
                        }
                    }
                }
            }
        };
    }

    private void initMouseClicked() {
        mouseClicked = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                if (e.getClickCount() == 2) {
                    ActionListener a = application.updateItemAction;
                    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,  null));
                }
                if (e.getClickCount() == 1) {
                    try {
                        dataSheetColumnClicked(table.columnAtPoint(e.getPoint()), table.rowAtPoint(e.getPoint()));
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
    }

    private void dataSheetColumnClicked(int col, int row) throws SQLException {
        if (col == 3) { // Data sheet column
            Item item = getItemAt(row);
            if (item != null) {
                String local = item.getLocalDataSheet();
                String online = item.getOnlineDataSheet();
                if (local != null && !local.isEmpty() && online != null && !online.isEmpty()) {
                    SelectDataSheetDialog.showDialog(application, online, local);
                } else if (local != null && !local.isEmpty()) {
                    try {
                        OpenUtils.openPdf(local);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(application,
                                "Error opening the file: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else if (online != null && !online.isEmpty()) {
                    try {
                        OpenUtils.browseLink(online);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(application,
                                "Error opening the file: " + e1.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
