package com.waldo.inventory.gui.dialogs.orderdetailsdialog;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.waldo.inventory.gui.Application.stringResource;

public class OrderDetailsDialog extends OrderDetailsDialogLayout {

    private static final SimpleDateFormat dateFormatShort = new SimpleDateFormat("MMM d, yyyy");

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
            if ((order.getDistributor() != null) && !(order.getDistributor().getWebsite().isEmpty())) {
                try {
                    OpenUtils.browseLink(order.getDistributor().getWebsite());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(OrderDetailsDialog.this, "Unable to browse: " + order.getDistributor().getWebsite(), "Browse error", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        });
        orderUrlBrowseBtn.addActionListener(e -> {
            if (!orderUrlTf.getText().isEmpty()) {
                try {
                    OpenUtils.browseLink(orderUrlTf.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(OrderDetailsDialog.this, "Unable to browse: " + orderUrlTf.getText(), "Browse error", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        });
        formattedBtn.addActionListener(e -> {
            AbstractButton btn = (AbstractButton) e.getSource();
            setOrderfileFormatted(btn.getModel().isSelected());
        });
        copyToClipboardBtn.addActionListener(e -> {
            if (order != null && order.getOrderFile() != null) {
                StringSelection selection = new StringSelection(order.getOrderFile().getRawOrderString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });
    }

    private void showSaveDialog(boolean closeAfter) {
        if (order != null) {
            String msg = order.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    order.save();
                    originalOrder = order.createCopy();
                    if (closeAfter) {
                        dialogResult = OK;
                        dispose();
                    }
                }
            }
        } else {
            if (closeAfter) {
                dialogResult = OK;
                dispose();
            }
        }
        canClose = true;
    }

    private boolean verify() {
        return true;
    }

    private boolean checkChange() {
        return  (order != null) && !(order.equals(originalOrder));
    }

    private void setOrderfileFormatted(boolean formatted) {
        CardLayout cardLayout = (CardLayout) orderFileView.getLayout();
        if (formatted) {
            cardLayout.show(orderFileView, "Table");
        } else {
            cardLayout.show(orderFileView, "TextField");
        }
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog(true);
        }

        if (canClose) {
            dialogResult = OK;
            dispose();
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            order.save();
            originalOrder = order.createCopy();
            getButtonNeutral().setEnabled(false);
        }

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

                // Numbers
                itemsTf.setText(String.valueOf(order.getOrderItems().size()));
                totalPriceTf.setText(String.valueOf(order.getTotalPrice()));

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

                // Order file stuff
                if (order.getOrderFile() != null) {
                    orderFileTa.setText(order.getOrderFile().getRawOrderString());
                }
                // Table for formatted order file
                if (order.getDistributor() != null) {
                    if (order.getDistributor().getId() == 2) { // Mouser
                        orderUrlTf.setText(stringResource.readString("OrderInfo.MouserUrl"));
                    }
                    if (order.getDistributor().getId() == 3) { // Farnell
                        orderUrlTf.setText(stringResource.readString("OrderInfo.FarnellUrl"));
                    }
                }

                if (order.getOrderFile() != null) {
                    fillTableData(tableModel, order.getOrderFile().getRawOrderString());
                }


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
