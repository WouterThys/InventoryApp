package com.waldo.inventory.gui.dialogs.orderconfirmdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class OrderConfirmDialogLayout extends IDialog implements ActionListener {

    static final String STEP_ORDER_FILE = "Order file ";
    static final String STEP_ORDER_DETAILS = "Order details";

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<String> stepList;
    DefaultListModel<String> stepListModel;

    JPanel cardPanel;
    CardLayout cardLayout;
    JPanel filePanel;
    JPanel detailPanel;

    // Order file panel
    ILabel orderByLbl;
    ILabel fileOkLbl;

    JTable orderFileTable;
    DefaultTableModel orderFileTableModel;
    JButton parseBtn;
    JButton copyToClipboardBtn;
    JButton viewParsedBtn;


    // Order panel
    ITextField referenceTf;
    ITextField trackingNrTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Order order;
    String currentPanel = STEP_ORDER_FILE;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderConfirmDialogLayout(Application application, String title) {
        super(application, title);
        showTitlePanel(false);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    List<String> checkOrder(Order order) {
        List<String> errorList = new ArrayList<>();

        if (order == null) {
            errorList.add(" - No order selected..");
        } else {
            if (order.getDistributor() == null) {
                errorList.add(" - Order had no distributor..");
            } else {
                if (order.getDistributor().getOrderFileFormat() == null || order.getDistributor().getOrderFileFormat().isUnknown()) {
                    errorList.add(" - Order's distributor had no selected file format");
                } else {
                    if (order.getOrderItems().size() < 1) {
                        errorList.add(" - Order has no items..");
                    } else {
                        List<OrderItem> errorItems = order.missingOrderReferences();
                        if (errorItems.size() > 0) {
                            errorList.add(" - Next order items have no reference: ");
                            for (OrderItem oi : errorItems) {
                                errorList.add(" \t * " + oi.getName());
                            }
                        }
                    }
                }
            }
        }


        return errorList;
    }

    void fillTableData() {
        List<OrderItem> orderItemList = order.getOrderItems();
        try {
            String[] references = new String[orderItemList.size()];
            String[] amounts = new String[orderItemList.size()];
            for (int i = 0; i < references.length; i++) {
                OrderItem orderItem = orderItemList.get(i);
                references[i] = orderItem.getDistributorPart().getItemRef();
                amounts[i] = String.valueOf(orderItem.getAmount());
            }
            orderFileTableModel = new DefaultTableModel();
            orderFileTable.setModel(orderFileTableModel);
            orderFileTableModel.addColumn("Reference", references);
            orderFileTableModel.addColumn("Amount", amounts);
            orderFileTableModel.fireTableDataChanged();
        } catch (Exception e) {
            Status().setError("Error filling table data.", e);
        }
    }


    private void createFilePanel() {
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel southPanel = new JPanel(new BorderLayout());

        northPanel.add(orderByLbl, BorderLayout.WEST);
        northPanel.add(fileOkLbl, BorderLayout.EAST);

        centerPanel.add(new JScrollPane(orderFileTable), BorderLayout.CENTER);

        JPanel southExtra = new JPanel();
        southExtra.add(viewParsedBtn);
        southExtra.add(copyToClipboardBtn);
        southExtra.add(parseBtn);
        southPanel.add(southExtra, BorderLayout.EAST);

        filePanel.setLayout(new BorderLayout());
        filePanel.add(northPanel, BorderLayout.NORTH);
        filePanel.add(centerPanel, BorderLayout.CENTER);
        filePanel.add(southPanel, BorderLayout.SOUTH);

        TitledBorder border = PanelUtils.createTitleBorder(STEP_ORDER_FILE);
        filePanel.setBorder(border);
    }

    private void createDetailPanel() {
        detailPanel.add(new ILabel("Detail"));
    }


    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog stuff
        getButtonOK().setText("next");
        getButtonOK().setEnabled(false);

        // Main layout
        cardLayout = new CardLayout(5,5);
        cardPanel = new JPanel(cardLayout);

        stepListModel = new DefaultListModel<>();
        stepListModel.addElement(STEP_ORDER_FILE);
        stepListModel.addElement(STEP_ORDER_DETAILS);
        stepList = new JList<>(stepListModel);
        stepList.setEnabled(false);
        stepList.setSelectedIndex(0);

        filePanel = new JPanel();
        detailPanel = new JPanel();

        // File panel
        orderByLbl = new ILabel();
        fileOkLbl = new ILabel(imageResource.readImage("OrderConfirm.Error", 16));

        orderFileTableModel = new DefaultTableModel();
        orderFileTable = new JTable(orderFileTableModel);

        parseBtn = new JButton(imageResource.readImage("Common.Refresh", 24));
        parseBtn.setToolTipText("Parse again");
        parseBtn.addActionListener(this);
        copyToClipboardBtn = new JButton(imageResource.readImage("Common.Copy", 24));
        copyToClipboardBtn.setToolTipText("Copy to clipboard");
        copyToClipboardBtn.addActionListener(this);
        viewParsedBtn = new JButton(imageResource.readImage("Common.View", 24));
        viewParsedBtn.setToolTipText("View parsed file");
        viewParsedBtn.addActionListener(this);

        // Detail panel
        referenceTf = new ITextField("Distributor order reference");
        trackingNrTf = new ITextField("Tracking number");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        createFilePanel();
        createDetailPanel();

        cardPanel.add(STEP_ORDER_FILE, filePanel);
        cardPanel.add(STEP_ORDER_DETAILS, detailPanel);

        getContentPanel().add(new JScrollPane(stepList), BorderLayout.WEST);
        getContentPanel().add(cardPanel, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            order = (Order) object;

            // File
            if (order.getDistributor() != null) {
                orderByLbl.setText("Order by: " + order.getDistributor().getName());
            }

            // Details
            referenceTf.setText(order.getOrderReference());
            trackingNrTf.setText(order.getTrackingNumber());
        }
    }
}


//    getContentPanel().setLayout(new GridBagLayout());
//
//        // Labels
//        ILabel referenceLabel = new ILabel("Reference: ");
//        referenceLabel.setHorizontalAlignment(ILabel.RIGHT);
//        referenceLabel.setVerticalAlignment(ILabel.CENTER);
//        ILabel trackingLabel = new ILabel("Tracking: ");
//        trackingLabel.setHorizontalAlignment(ILabel.RIGHT);
//        trackingLabel.setVerticalAlignment(ILabel.CENTER);
//
//        // Layout
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(2,2,2,2);
//
//        // - Reference Label
//        gbc.gridx = 0; gbc.weightx = 0;
//        gbc.gridy = 0; gbc.weighty = 0;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        getContentPanel().add(referenceLabel, gbc);
//
//        // - Reference field
//        gbc.gridx = 1; gbc.weightx = 1;
//        gbc.gridy = 0; gbc.weighty = 0;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        getContentPanel().add(referenceTf, gbc);
//
//        // - Tracking Label
//        gbc.gridx = 0; gbc.weightx = 0;
//        gbc.gridy = 1; gbc.weighty = 0;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        getContentPanel().add(trackingLabel, gbc);
//
//        // - Tracking field
//        gbc.gridx = 1; gbc.weightx = 1;
//        gbc.gridy = 1; gbc.weighty = 0;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        getContentPanel().add(trackingNrTf, gbc);
//
//        // - Border
//        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
