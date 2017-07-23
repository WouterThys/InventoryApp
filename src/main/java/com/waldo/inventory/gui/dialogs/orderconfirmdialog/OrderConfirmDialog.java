package com.waldo.inventory.gui.dialogs.orderconfirmdialog;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.orderdetailsdialog.OrderDetailsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class OrderConfirmDialog extends OrderConfirmDialogLayout {

    public OrderConfirmDialog(Application application, String title, Order order) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(order);

        checkAndUpdate();
    }

    @Override
    protected void onOK() {
        if (currentPanel.equals(STEP_ORDER_DETAILS)) {
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

    private void selectNext() {
        cardLayout.next(cardPanel);
        if (currentPanel.equals(STEP_ORDER_FILE)) {
            currentPanel = STEP_ORDER_DETAILS;
        } else {
            currentPanel = STEP_ORDER_FILE;
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
            builder.append(orderItem.getDistributorPart().getItemRef());
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
        return order;
    }
}
