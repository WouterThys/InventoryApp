package com.waldo.inventory.gui.dialogs.orderdetailsdialog;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog.EditReceivedItemsLocationDialog;
import com.waldo.utils.OpenUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class OrderDetailsCacheDialog extends OrderDetailsCacheDialogLayout implements CacheChangedListener<ItemOrder> {

    public OrderDetailsCacheDialog(Application application, String title, ItemOrder itemOrder) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(itemOrder);

        addCacheListener(ItemOrder.class,this);

        checkAndUpdate();
    }

    private void checkOrderedItemsLocations(ItemOrder itemOrder) {
        if (itemOrder.isReceived() && itemOrder.getDistributorType() == Statics.DistributorType.Items) {
            // Find items without location
            List<Item> itemsWithoutLocation = new ArrayList<>();
//            for (ItemOrderLine oi : itemOrder.getItemOrderLines()) {
//                if ((oi.getItem().getLocationId() <= DbObject.UNKNOWN_ID)) {
//                    itemsWithoutLocation.add(oi.getItem());
//                }
//            }

            // There are items without location -> ask to set them
            if (itemsWithoutLocation.size() > 0) {
                int res = JOptionPane.showConfirmDialog(
                        this,
                        "Some items do not have a location yet, do you want to set it now?",
                        "New item locations",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (res == JOptionPane.YES_OPTION) {
                    EditReceivedItemsLocationDialog dialog = new EditReceivedItemsLocationDialog(this, "Set location", itemsWithoutLocation);
                    dialog.showDialog();
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isShown && !e.getValueIsAdjusting()) {
            String selected = stepList.getSelectedValue();
            if (selected != null && !selected.isEmpty()) {
                currentPanel = selected;
                cardLayout.show(cardPanel, currentPanel);
                updateVisibleComponents();
                updateEnabledComponents();
            }
        }
    }

    private void checkAndUpdate() {
        List<String> errorList = checkOrder(itemOrder);
        if (errorList.size() == 0) {
            fillTableData();

            fileOkLbl.setIcon(imageResource.readIcon("OrderConfirm.Check"));
            parseSuccess = true;
        } else {
            showErrors(errorList);
            parseSuccess = false;
        }

        updateEnabledComponents();
        updateVisibleComponents();
    }

    private void showErrors(List<String> errorList) {
        StringBuilder builder = new StringBuilder();
        builder.append("Creation of itemOrder file failed with next ").append(errorList.size()).append("error(s): ").append("\n");
        for (String error : errorList) {
            builder.append(error).append("\n");
        }

        JOptionPane.showMessageDialog(this,
                builder.toString(),
                "ItemOrder file errors",
                JOptionPane.ERROR_MESSAGE);
    }

    private void copyToClipboard() {
        String orderText = createOrderText(itemOrder);
        StringSelection selection = new StringSelection(orderText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private String createOrderText(ItemOrder itemOrder) {
        StringBuilder builder = new StringBuilder();
//        for (ItemOrderLine orderItem : itemOrder.getItemOrderLines()) {
//            builder.append(orderItem.getDistributorPartLink().getReference());
//            builder.append(itemOrder.getDistributor().getOrderFileFormat().getSeparator());
//            builder.append(orderItem.getAmount());
//            builder.append("\n");
//        }
        return builder.toString();
    }

    private void viewParsed() {
        JOptionPane.showMessageDialog(this,
                createOrderText(itemOrder),
                "ItemOrder file",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void browseDistributor() {
        try {
            OpenUtils.browseLink(itemOrder.getDistributor().getWebsite());
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(OrderDetailsCacheDialog.this,
                    "Unable to browse: " + itemOrder.getDistributor().getWebsite(),
                    "Browse error",
                    JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
    }

    private void browseOrderPage() {
        try {
            OpenUtils.browseLink(itemOrder.getDistributor().getOrderLink());
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(OrderDetailsCacheDialog.this,
                    "Unable to browse: " + itemOrder.getDistributor().getOrderLink(),
                    "Browse error",
                    JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
    }

    //
    // Db edited listener
    //
    @Override
    public void onInserted(ItemOrder itemOrder) {
        if (itemOrder.isReceived()) {
            checkOrderedItemsLocations(itemOrder);
        }
    }

    @Override
    public void onUpdated(ItemOrder itemOrder) {
        if (itemOrder.isReceived()) {
            checkOrderedItemsLocations(itemOrder);
        }
    }

    @Override
    public void onDeleted(ItemOrder itemOrder) {
        // Should not happen
    }

    @Override
    public void onCacheCleared() {
        // Don't care at all
    }

    //
    // Buttons
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(copyToClipboardBtn))  {
            if (itemOrder != null && itemOrder.getDistributor() != null && itemOrder.getDistributor().getOrderFileFormat() != null) {
                copyToClipboard();
            } else {
                JOptionPane.showMessageDialog(OrderDetailsCacheDialog.this,
                        "Could not copy to clipboard..",
                        "Error copying",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(parseBtn)) {
            checkAndUpdate();
        } else if (e.getSource().equals(viewParsedBtn)) {
            if (itemOrder != null && itemOrder.getDistributor() != null && itemOrder.getDistributor().getOrderFileFormat() != null) {
                viewParsed();
            } else {
                JOptionPane.showMessageDialog(OrderDetailsCacheDialog.this,
                        "Could not copy to clipboard..",
                        "Error copying",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(distributorsBrowseBtn)) {
            if ((itemOrder.getDistributor() != null) && !(itemOrder.getDistributor().getWebsite().isEmpty())) {
                browseDistributor();
            } else {
                JOptionPane.showMessageDialog(OrderDetailsCacheDialog.this,
                        "Could browse website..",
                        "Error browsing",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(orderUrlBrowseBtn)) {
            if ((itemOrder.getDistributor() != null) && !(itemOrder.getDistributor().getOrderLink().isEmpty())) {
                browseOrderPage();
            } else {
                JOptionPane.showMessageDialog(OrderDetailsCacheDialog.this,
                        "Could browse itemOrder page..",
                        "Error browsing",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {

    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            return itemOrder;
        }
        return null;
    }
}
