package com.waldo.inventory.gui.dialogs.orderdetailsdialog;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.orderinfodialog.OrderInfoDialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class OrderDetailsDialog extends OrderDetailsDialogLayout {

    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd");

    private boolean canClose = true;
    private boolean initialized = false;

    public OrderDetailsDialog(Application application, String title, Order order) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        initActions();
        updateComponents(order);

    }

    private void initActions() {
        distributorsBrowseBtn.addActionListener(e -> {
            if ((order.getDistributor() != null) && (order.getDistributor().getWebsite().isEmpty())) {
                try {
                    OpenUtils.browseLink(order.getDistributor().getWebsite());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(OrderDetailsDialog.this, "Unable to browse: " + order.getDistributor().getWebsite(), "Browse error", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        });
    }

    private void showSaveDialog(boolean closeAfter) {

    }

    private boolean verify() {
        return false;
    }

    private boolean checkChange() {
        return  (order != null) && !(order.equals(originalOrder));
    }

    //
    // Update
    //
    @Override
    public void updateComponents(Object object) {
        try {
            initialized = false;
            application.beginWait();

            order = (Order) object;
            if (order != null) {
                originalOrder = order.createCopy();

                // Title
                setTitleName(order.getName());

                // Ref and track
                orderReferenceTf.setText(order.getOrderReference());
                trackingNrTf.setText(order.getTrackingNumber());

                // Distributor
                distributorCb.setSelectedItem(order.getDistributor());
                distributorWebsiteTf.setText(order.getDistributor().getWebsite());

                // Dates
                if (order.isOrdered()) {
                    distributorCb.setEnabled(false);
                    dateOrderedTf.setText(dateFormatShort.format(order.getDateOrdered()));
                } else {
                    dateOrderedTf.setText("Not ordered");
                }
                if (order.isReceived()) {
                    distributorCb.setEnabled(false);
                    dateReceivedTf.setText(dateFormatShort.format(order.getDateReceived()));
                } else {
                    dateReceivedTf.setText("Not received");
                }

                dateModifiedTf.setText(dateFormatShort.format(order.getDateModified()));

            } else {
                originalOrder = null;
            }
        } finally {
            application.endWait();
            initialized = true;
        }

    }

    //
    // Values changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        if (initialized) {
            return order;
        } else {
            return null;
        }
    }
}
