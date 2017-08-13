package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.database.settings.SettingsManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;
import com.waldo.inventory.gui.dialogs.historydialog.HistoryDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.database.settings.SettingsManager.settings;

public class ItemDetailPanel extends ItemDetailPanelLayout {

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
            if (object instanceof Item) {
                setVisible(true);

                selectedItem = (Item) object;

                updateIcon(selectedItem);
                updateTextFields(selectedItem);
                updateButtons(selectedItem);
            }
        }
    }

    public void setRemarksPanelVisible(boolean visible) {
        remarksPanel.setVisible(visible);
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
        int result = JOptionPane.YES_OPTION;
        if (item.isDiscourageOrder()) {
            result = JOptionPane.showConfirmDialog(
                    application,
                    "This item is marked to discourage new orders, \n do you really want to order it?",
                    "Discouraged to order",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
        }
        if (result == JOptionPane.YES_OPTION) {
            OrderItemDialog dialog = new OrderItemDialog(application, "Order " + item.getName(), item, true);
            dialog.showDialog();
        }
    }

    private void updateIcon(Item item) {
        Path path = Paths.get(settings().getFileSettings().getImgItemsPath(), item.getIconPath());
        iconLabel.setIcon(path.toString());
    }

    private void updateTextFields(Item item) {
        if (item != null) {
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
            divisionTa.setText(builder.toString());

            if (item.getManufacturerId() > DbObject.UNKNOWN_ID) {
                manufacturerTextField.setText(sm().findManufacturerById(item.getManufacturerId()).getName());
            } else {
                manufacturerTextField.setText("");
            }

            descriptionTextArea.setText(item.getDescription());

            starRater.setRating(item.getRating());
            discourageOrder.setSelected(item.isDiscourageOrder());
            remarksTa.setText(item.getRemarks());
        }
    }

    private void updateButtons(Item item) {
        if (item != null) {
            dataSheetButton.setEnabled(!item.getLocalDataSheet().isEmpty() || !item.getOnlineDataSheet().isEmpty());
        }
    }

}
