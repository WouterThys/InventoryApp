package com.waldo.inventory.gui.dialogs.importfromcsvdialog;


import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.OrderFile;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.gui.Application.imageResource;

public class ImportCsvDialog extends ImportCsvDialogLayout {

    private File csvFile;
    private String importError = "";
    private SearchManager searchManager;

    public ImportCsvDialog(Application application, String title, File csvFile, boolean useHeader, int firsRow, int valueColumn) {
        super(application, title);
        setResizable(true);

        searchManager = new SearchManager(DbObject.TYPE_ITEM);

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
        application.beginWait();
        try {
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
                String headerRow;
                if (useHeader) {
                    tableColumnNames = usefulRows[0].split(",");
                    tableColumnNames = ArrayUtils.removeElement(tableColumnNames, valueColumn);
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
                    // First search
                    tableObject.setFound(searchManager.search(tableObject.getItemReference()).size());

                    // Add
                    tableObjects.add(tableObject);
                }
            } catch (Exception e) {
                importError = e.toString();
                return false;
            }
        } finally {
            application.endWait();
        }

        return true;
    }


    private void initActions() {

    }

    private void order(List<Item> itemsToOrder) {
        // Check if not already ordered???
        // Do orders
        OrderItemDialog dialog = new OrderItemDialog(application, "Order", itemsToOrder);
        dialog.showDialog();
        // Set items ordered?
    }

    private void deleteTableObjects(List<TableObject> objectsToDelete) {
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.CANCEL_OPTION;
            if (objectsToDelete.size() == 1) {
                result = JOptionPane.showConfirmDialog(
                        ImportCsvDialog.this,
                        "Are you sure you want to delete " + objectsToDelete.get(0).getItemReference() + "?",
                        "Confirm delete",
                        JOptionPane.YES_NO_OPTION);
            } else if (objectsToDelete.size() > 1) {
                result = JOptionPane.showConfirmDialog(
                        ImportCsvDialog.this,
                        "Are you sure you want to delete " + objectsToDelete.size() + " items?",
                        "Confirm delete",
                        JOptionPane.YES_NO_OPTION);
            }

            if (result == JOptionPane.OK_OPTION) {
                for (TableObject row : objectsToDelete) {
                    ((TableModel) objectTable.getModel()).removeRow(tableModel.getObjectList().indexOf(row));
                }
            }
        });
    }

    private JPopupMenu createDeletePopup() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete", imageResource.readImage("Toolbar.DeleteIcon"));
        deleteItem.addActionListener(e -> {
            // Get selected values
            int[] selectedRows = objectTable.getSelectedRows();
            if (selectedRows.length > 0) {
                List<TableObject> toDelete = new ArrayList<>(selectedRows.length);
                for (int row : selectedRows) {
                    TableObject to = tableModel.getObject(row);
                    if (to != null) {
                        toDelete.add(to);
                    }
                }
                // Delete them
                deleteTableObjects(toDelete);
            }
        });
        popup.add(deleteItem);
        return popup;
    }

    //
    // List item selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            int row = objectTable.getSelectedRow();
            if (row >= 0) {
                updateComponents(tableModel.getObject(row));
            }
        }
    }

    //
    // Item found
    //
    @Override
    public void onItemSelected(TableObject tableObject, Item item) {
        tableObject.setItem(item);

        tableModel.fireTableDataChanged();

        tableObjectPanel.updateEnabledComponents();
    }

    @Override
    public void onOrderPressed() {
        int[] selectedRows = objectTable.getSelectedRows();
        if (selectedRows.length > 0) {
            int error = 0;
            List<Item> itemsToOrder = new ArrayList<>();
            for (int row : selectedRows) {
                TableObject object = tableModel.getObject(row);
                if (object != null && object.isValid()) {
                    itemsToOrder.add(object.getItem());
                } else {
                    error++;
                }
            }

            if (error > 0) {
                int result = JOptionPane.showConfirmDialog(application,
                        "There were " + String.valueOf(error) +  " objects without an item, order all valid items in stead?",
                        "Invalid items",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    order(itemsToOrder);
                }
            } else {
                order(itemsToOrder);
            }
        }
    }

    //
    // Table clicked
    //
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu popup = createDeletePopup();
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
