package com.waldo.inventory.gui.panels.mainpanel.detailpanel;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.waldo.inventory.database.DbManager.db;

public class MainDetailPanel extends MainDetailPanelLayout {

    private Item selectedItem;

    public MainDetailPanel() {
        super();
        initializeComponents();
        initializeLayouts();
        initActions();
    }

    @Override
    public void updateComponents(Object object) {
        if (object == null) {
            setVisible(false);
            selectedItem = null;
        } else {
            setVisible(true);

            selectedItem = (Item) object;

            updateIcon(selectedItem);
            updateTextFields(selectedItem);
            updateButtons(selectedItem);
        }
    }

    private void initActions() {
        dataSheetButton.addActionListener(e -> openDataSheet(selectedItem));
    }

    private void openDataSheet(Item item) {
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

    private void updateIcon(Item item) {
        iconLabel.setIcon(item.getIconPath());
        iconLabel.setHorizontalAlignment(ILabel.CENTER);
        iconLabel.setVerticalAlignment(ILabel.CENTER);
        iconLabel.setPreferredSize(new Dimension(150,150));
    }

    private void updateTextFields(Item item) {
        nameTextField.setText(item.getName());
        StringBuilder builder = new StringBuilder();

        if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
            builder.append(" / ").append(db().findCategoryById(item.getCategoryId()).getName());
            if (item.getProductId() > DbObject.UNKNOWN_ID) {
                builder.append(" / ").append(db().findProductById(item.getProductId()).getName());
                if (item.getTypeId() > DbObject.UNKNOWN_ID) {
                    builder.append(" / ").append(db().findTypeById(item.getTypeId()).getName());
                }
            }
        }
        divisionTextField.setText(builder.toString());

        if (item.getManufacturerId() > DbObject.UNKNOWN_ID) {
            manufacturerTextField.setText(db().findManufacturerById(item.getManufacturerId()).getName());
        } else {
            manufacturerTextField.setText("");
        }

        descriptionTextArea.setText(item.getDescription());
    }

    private void updateButtons(Item item) {
        dataSheetButton.setEnabled(!item.getLocalDataSheet().isEmpty() || !item.getOnlineDataSheet().isEmpty());
    }
}
