package com.waldo.inventory.gui.dialogs.importfromcsvdialog;


import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.OrderFile;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ImportCsvDialog extends ImportCsvDialogLayout {

    private File csvFile;
    private String importError = "";

    public ImportCsvDialog(Application application, String title, File csvFile, boolean useHeader, int firsRow, int valueColumn) {
        super(application, title);
        setResizable(true);

        if (convertCsvFile(csvFile, useHeader, firsRow, valueColumn)) {
            initializeComponents();
            initializeLayouts();
            initActions();
            updateComponents(null);
        } else {

            JOptionPane.showMessageDialog(application, "Error importing file: " + importError, "Error", JOptionPane.ERROR_MESSAGE);

            dialogResult = CANCEL;
            dispose();
        }
    }

    private boolean convertCsvFile(File csvFile, boolean useHeader, int firstRow, int valueColumn) {
        // Check file
        if (csvFile == null || !csvFile.exists()) {
            importError = "File does not exist";
            return false;
        } else {
            this.csvFile = csvFile;
        }

        // Read content
        String rawData = FileUtils.getRawStringFromFile(csvFile);
        if (rawData.isEmpty()) {
            importError = "Unable to read file content";
            return false;
        }

        // Do stuff with content
        try {
            String[] rawRows = rawData.split("\n");
            String[] usefulRows = Arrays.copyOfRange(rawRows, firstRow, rawRows.length);
            if (useHeader) {
                tableColumnNames = usefulRows[0].split(",");
                tableColumnNames[valueColumn] = "Item name";
                usefulRows = Arrays.copyOfRange(usefulRows, 1, usefulRows.length);
            }

            tableObjects = new ArrayList<>();
            for (String s : usefulRows) {
                String[] split = s.split(",");
                TableObject tableObject = new TableObject();
                for (int i = 0; i < split.length; i++) {
                    if (i == valueColumn) {
                        tableObject.setItemReference(split[i].replace("\"", ""));
                    } else {
                        tableObject.getExtraData().add(split[i].replace("\"", ""));
                    }
                }
                tableObjects.add(tableObject);
            }
        } catch (Exception e) {
            importError = e.toString();
            return false;
        }

        return true;
    }

    void fillTableData(DefaultTableModel model, String rawData) {
        try {
            String[] parts = rawData.split("\n");
            String[] references = new String[parts.length];
            String[] amounts = new String[parts.length];
            for (int i = 0; i < parts.length; i++) {
                String[] subParts = parts[i].split(OrderFile.SEPARATOR);
                references[i] = subParts[0];
                amounts[i] = subParts[1];
            }
            model.addColumn("Reference", references);
            model.addColumn("Amount", amounts);
            model.fireTableDataChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initActions() {

    }

    //
    // List item selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {

    }
}
