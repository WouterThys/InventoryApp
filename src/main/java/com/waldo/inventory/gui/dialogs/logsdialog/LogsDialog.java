package com.waldo.inventory.gui.dialogs.logsdialog;


import com.waldo.inventory.classes.Log;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

public class LogsDialog extends LogsDialogLayout {

    @Override
    public int showDialog() {
        setLocationRelativeTo(application);
        setPreferredSize(new Dimension(1400, 800));
        pack();
        setMinimumSize(getSize());
        setVisible(true);
        return dialogResult;
    }

    public LogsDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateLogTable(true, true, true, true);

        updateComponents(null);
    }

    private void deleteAllLogs() {
        application.beginWait();
        try {
            List<Log> allLogs = DbManager.db().getLogs();

            for (Log log : allLogs) {
                log.delete();
            }
        } finally {
            application.endWait();
        }

        updateLogTable(
                showInfoCb.isSelected(),
                showDebugCb.isSelected(),
                showWarnCb.isSelected(),
                showErrorCb.isSelected());
        updateComponents(null);
    }

    //
    // Checkbox values changed
    //
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED || e.getStateChange() == ItemEvent.DESELECTED) {
            updateLogTable(
                    showInfoCb.isSelected(),
                    showDebugCb.isSelected(),
                    showWarnCb.isSelected(),
                    showErrorCb.isSelected());
            updateComponents(null);
        }
    }

    //
    // Log table selection changed
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            int row = logTable.getSelectedRow();
            if (row >= 0) {
                updateComponents(logTable.getValueAtRow(row));
            }
        }
    }

    //
    // Clear button clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(
                LogsDialog.this,
                "Do you really want to delete all logs?",
                "Delete logs",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            deleteAllLogs();
        }
    }
}
