package com.waldo.inventory.gui.dialogs.orderconfirmdialog;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog.EditReceivedItemsLocationDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class OrderConfirmDialog extends OrderConfirmDialogLayout implements CacheChangedListener<Order> {

    public OrderConfirmDialog(Application application, String title, Order order) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(order);

        cache().addOnOrdersChangedListener(this);

        checkAndUpdate();
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (currentPanel.equals(TAB_ORDER_DETAILS)) {
            switch(order.getOrderState()) {
                case Statics.ItemOrderStates.PLANNED: doOrder(); break;
                case Statics.ItemOrderStates.ORDERED: setReceived(); break;
                case Statics.ItemOrderStates.RECEIVED: super.onOK(); break;
                default: break;
            }
            updateComponents(order);
            updateEnabledComponents();
            updateVisibleComponents();
        } else {
            selectNext();
        }
    }

    @Override
    protected void onNeutral() {
        selectNext();
    }

    @Override
    protected void onCancel() {
        if (order != null && originalOrder != null) {
            originalOrder.createCopy(order);
            order.setCanBeSaved(true);
        }

        super.onCancel();
    }

    @Override
    protected void setFocusTab(String focusTab) {
        if (focusTab.equals(TAB_ORDER_DETAILS) && parseSucces) {
            selectNext();
        }
    }

    //
    // Methods
    //
    private void doOrder() {
        // Do order
        order.setDateOrdered(new Date(Calendar.getInstance().getTimeInMillis()));
        application.beginWait();
        try {
            order.updateItemStates();
        } finally {
            application.endWait();
        }
        order.save();
        originalOrder = order.createCopy();

        // Go to website
        copyToClipboard();
        browseOrderPage();
    }

    private void setReceived() {
        // Do receive
        order.setDateReceived(new Date(Calendar.getInstance().getTimeInMillis()));
        application.beginWait();
        try {
            order.updateItemStates();
            order.updateItemAmounts();
        } finally {
            application.endWait();
        }
        order.save();
        originalOrder = order.createCopy();
    }

    private void checkOrderedItemsLocations(Order order) {
        if (order.isReceived()) {
            // Find items without location
            List<Item> itemsWithoutLocation = new ArrayList<>();
            for (OrderItem oi : order.getOrderItems()) {
                if (oi.getItem().getLocationId() <= DbObject.UNKNOWN_ID) {
                    itemsWithoutLocation.add(oi.getItem());
                }
            }

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
                    EditReceivedItemsLocationDialog dialog = new EditReceivedItemsLocationDialog(application, "Set location", itemsWithoutLocation);
                    dialog.showDialog();
                }
            }
        }
    }

    private void selectNext() {
        cardLayout.next(cardPanel);
        if (currentPanel.equals(TAB_ORDER_FILE)) {
            currentPanel = TAB_ORDER_DETAILS;
        } else {
            currentPanel = TAB_ORDER_FILE;
        }
        stepList.setSelectedValue(currentPanel, true);
        updateEnabledComponents();
        updateVisibleComponents();
    }

    private void checkAndUpdate() {
        List<String> errorList = checkOrder(order);
        if (errorList.size() == 0) {
            fillTableData();

            fileOkLbl.setIcon(imageResource.readImage("OrderConfirm.Check", 16));
            parseSucces = true;
        } else {
            showErrors(errorList);
            parseSucces = false;
        }

        updateEnabledComponents();
        updateVisibleComponents();
    }

    private void showErrors(List<String> errorList) {
        StringBuilder builder = new StringBuilder();
        builder.append("Creation of order file failed with next ").append(errorList.size()).append("error(s): ").append("\n");
        for (String error : errorList) {
            builder.append(error).append("\n");
        }

        JOptionPane.showMessageDialog(this,
                builder.toString(),
                "Order file errors",
                JOptionPane.ERROR_MESSAGE);
    }

    private void copyToClipboard() {
        String orderText = createOrderText(order);
        StringSelection selection = new StringSelection(orderText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private String createOrderText(Order order) {
        StringBuilder builder = new StringBuilder();
        for (OrderItem orderItem : order.getOrderItems()) {
            builder.append(orderItem.getDistributorPartLink().getItemRef());
            builder.append(order.getDistributor().getOrderFileFormat().getSeparator());
            builder.append(orderItem.getAmount());
            builder.append("\n");
        }
        return builder.toString();
    }

    private void viewParsed() {
        JOptionPane.showMessageDialog(this,
                createOrderText(order),
                "Order file",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void browseDistributor() {
        try {
            OpenUtils.browseLink(order.getDistributor().getWebsite());
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(OrderConfirmDialog.this,
                    "Unable to browse: " + order.getDistributor().getWebsite(),
                    "Browse error",
                    JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
    }

    private void browseOrderPage() {
        try {
            OpenUtils.browseLink(order.getDistributor().getOrderLink());
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(OrderConfirmDialog.this,
                    "Unable to browse: " + order.getDistributor().getOrderLink(),
                    "Browse error",
                    JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
    }

    //
    // Db edited listener
    //
    @Override
    public void onInserted(Order order) {
        if (order.isReceived()) {
            checkOrderedItemsLocations(order);
        }
    }

    @Override
    public void onUpdated(Order order) {
        if (order.isReceived()) {
            checkOrderedItemsLocations(order);
        }
    }

    @Override
    public void onDeleted(Order order) {
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
            if (order != null && order.getDistributor() != null && order.getDistributor().getOrderFileFormat() != null) {
                copyToClipboard();
            } else {
                JOptionPane.showMessageDialog(OrderConfirmDialog.this,
                        "Could not copy to clipboard..",
                        "Error copying",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(parseBtn)) {
            checkAndUpdate();
        } else if (e.getSource().equals(viewParsedBtn)) {
            if (order != null && order.getDistributor() != null && order.getDistributor().getOrderFileFormat() != null) {
                viewParsed();
            } else {
                JOptionPane.showMessageDialog(OrderConfirmDialog.this,
                        "Could not copy to clipboard..",
                        "Error copying",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(distributorsBrowseBtn)) {
            if ((order.getDistributor() != null) && !(order.getDistributor().getWebsite().isEmpty())) {
                browseDistributor();
            } else {
                JOptionPane.showMessageDialog(OrderConfirmDialog.this,
                        "Could browse website..",
                        "Error browsing",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(orderUrlBrowseBtn)) {
            if ((order.getDistributor() != null) && !(order.getDistributor().getOrderLink().isEmpty())) {
                browseOrderPage();
            } else {
                JOptionPane.showMessageDialog(OrderConfirmDialog.this,
                        "Could browse order page..",
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
            return order;
        }
        return null;
    }
}
