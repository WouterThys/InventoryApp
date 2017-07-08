package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;
import com.waldo.inventory.gui.dialogs.historydialog.HistoryDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;

import javax.swing.*;
import java.io.IOException;

import static com.waldo.inventory.database.SearchManager.sm;

public class ItemDetailPanel extends ItemDetailPanelLayout {

    private Item selectedItem;

    public ItemDetailPanel(Application application) {
        super(application);
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

    public void setOrderButtonVisible(boolean visible) {
        orderButton.setVisible(visible);
    }

    private void initActions() {
        dataSheetButton.addActionListener(e -> openDataSheet(selectedItem));
        orderButton.addActionListener(e -> orderItem(selectedItem));
        historyButton.addActionListener(e -> {
            HistoryDialog dialog = new HistoryDialog(application, selectedItem);
            dialog.showDialog();
        });
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

    private void orderItem(Item item) {
        OrderItemDialog dialog = new OrderItemDialog(application, "Order " + item.getName(), item);
        dialog.showDialog();
    }

    private void updateIcon(Item item) {
        iconLabel.setIcon(item.getIconPath());
    }

    private void updateTextFields(Item item) {
        nameTextField.setText(item.getName());
        StringBuilder builder = new StringBuilder();

        if (item.getCategoryId() > DbObject.UNKNOWN_ID) {
            builder.append(" / ").append(sm().findCategoryById(item.getCategoryId()).getName());
            if (item.getProductId() > DbObject.UNKNOWN_ID) {
                builder.append(" / ").append(sm().findProductById(item.getProductId()).getName());
                if (item.getTypeId() > DbObject.UNKNOWN_ID) {
                    builder.append(" / ").append(sm().findTypeById(item.getTypeId()).getName());
                }
            }
        }
        divisionTextField.setText(builder.toString());

        if (item.getManufacturerId() > DbObject.UNKNOWN_ID) {
            manufacturerTextField.setText(sm().findManufacturerById(item.getManufacturerId()).getName());
        } else {
            manufacturerTextField.setText("");
        }

        descriptionTextArea.setText(item.getDescription());
    }

    private void updateButtons(Item item) {
        dataSheetButton.setEnabled(!item.getLocalDataSheet().isEmpty() || !item.getOnlineDataSheet().isEmpty());
    }
}