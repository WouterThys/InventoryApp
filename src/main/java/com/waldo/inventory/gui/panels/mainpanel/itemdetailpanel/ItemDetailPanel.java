package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.SelectDataSheetDialog;
import com.waldo.inventory.gui.dialogs.historydialog.HistoryDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.waldo.inventory.managers.SearchManager.sm;
import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class ItemDetailPanel extends ItemDetailPanelLayout {

    public ItemDetailPanel(Application application) {
        super(application);
        initializeComponents();
        initializeLayouts();
        initActions();

    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length == 0 || object[0] == null) {
            setVisible(false);
            selectedItem = null;
        } else {
            if (object[0] instanceof Item) {
                setVisible(true);

                selectedItem = (Item) object[0];

                updateIcon(selectedItem);
                updateTextFields(selectedItem);
                updateButtons(selectedItem);
            }
        }
    }

    public void setRemarksPanelVisible(boolean visible) {
        remarksPnl.setVisible(visible);
    }

    public void setOrderButtonVisible(boolean visible) {
        orderBtn.setVisible(visible);
    }

    private void initActions() {
        dataSheetBtn.addActionListener(e -> openDataSheet(selectedItem));
        orderBtn.addActionListener(e -> orderItem(selectedItem));
        historyBtn.addActionListener(e -> {
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
        try {
            Path path = Paths.get(settings().getFileSettings().getImgItemsPath(), item.getIconPath());
            iconLbl.setIcon(path.toString());
        } catch (Exception e) {
            Status().setError("Failed to set item icon");
        }
    }

    private void updateTextFields(Item item) {
        if (item != null) {
            nameTf.setText(item.getName());
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
                manufacturerTf.setText(sm().findManufacturerById(item.getManufacturerId()).getName());
            } else {
                manufacturerTf.setText("");
            }

            descriptionTa.setText(item.getDescription());

            starRater.setRating(item.getRating());
            discourageOrderCb.setSelected(item.isDiscourageOrder());
            remarksTa.setText(item.getRemarks());
        }
    }

    private void updateButtons(Item item) {
        if (item != null) {
            dataSheetBtn.setEnabled(!item.getLocalDataSheet().isEmpty() || !item.getOnlineDataSheet().isEmpty());
        }
    }

}
