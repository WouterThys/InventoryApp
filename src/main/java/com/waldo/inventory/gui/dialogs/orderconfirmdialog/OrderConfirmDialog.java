package com.waldo.inventory.gui.dialogs.orderconfirmdialog;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
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
        selectNext();
    }

    @Override
    protected void onNeutral() {
        selectNext();
    }

    private void selectNext() {
        cardLayout.next(cardPanel);
        if (currentPanel.equals(STEP_ORDER_FILE)) {
            currentPanel = STEP_ORDER_DETAILS;
            getButtonOK().setText("finish");
            getButtonNeutral().setVisible(true);
            getButtonNeutral().setText("back");
        } else {
            currentPanel = STEP_ORDER_FILE;
            getButtonOK().setText("next");
            getButtonNeutral().setVisible(false);
        }
        stepList.setSelectedValue(currentPanel, true);
    }

    private void checkAndUpdate() {
        List<String> errorList = checkOrder(order);
        if (errorList.size() == 0) {
            fillTableData();

            fileOkLbl.setIcon(imageResource.readImage("OrderConfirm.Check", 16));
            getButtonOK().setEnabled(true);

        } else {
            showErrors(errorList);
        }
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
            }else {
                JOptionPane.showMessageDialog(OrderConfirmDialog.this,
                        "Could not copy to clipboard..",
                        "Error copying",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
