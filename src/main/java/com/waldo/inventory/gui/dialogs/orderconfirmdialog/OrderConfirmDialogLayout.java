package com.waldo.inventory.gui.dialogs.orderconfirmdialog;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
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

public abstract class OrderConfirmDialogLayout extends IDialog implements ActionListener, IEditedListener {

    static final String TAB_ORDER_FILE = "Order file ";
    public static final String TAB_ORDER_DETAILS = "Order details";

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    JList<String> stepList;

    JPanel cardPanel;
    CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel filePanel;
    private JPanel detailPanel;

    JButton parseBtn;
    JButton copyToClipboardBtn;
    JButton viewParsedBtn;
    JButton distributorsBrowseBtn;
    JButton orderUrlBrowseBtn;

    private ILabel orderByLbl;
    ILabel fileOkLbl;

    // Order file panel
    private JTable orderFileTable;
    private DefaultTableModel orderFileTableModel;

    // Order panel
    private ITextField referenceTf;
    private ITextField trackingNrTf;
    private ITextField dateOrderedTf;
    private ITextField dateReceivedTf;
    private ITextField dateModifiedTf;
    private ITextField itemsTf;
    private ITextField totalPriceTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Order order, originalOrder;
    String currentPanel = TAB_ORDER_FILE;
    boolean parseSucces = false;

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
                                errorList.add(" \t * " + oi.getItem().getName());
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
                references[i] = orderItem.getDistributorPartLink().getItemRef();
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

    void updateEnabledComponents() {
        getButtonOK().setEnabled(parseSucces);
        viewParsedBtn.setEnabled(parseSucces);
        copyToClipboardBtn.setEnabled(parseSucces);
    }

    void updateVisibleComponents() {
        switch (currentPanel) {
            case TAB_ORDER_FILE:
                getButtonOK().setText("next");
                getButtonNeutral().setVisible(false);

                parseBtn.setVisible(true);
                distributorsBrowseBtn.setVisible(false);
                orderUrlBrowseBtn.setVisible(false);
                break;
            case TAB_ORDER_DETAILS:
                switch (order.getOrderState()) {
                    case Statics.ItemOrderStates.PLANNED:
                        getButtonOK().setText("order");
                        break;
                    case Statics.ItemOrderStates.ORDERED:
                        getButtonOK().setText("received");
                        break;
                    case Statics.ItemOrderStates.RECEIVED:
                        getButtonOK().setText("ok");
                        break;
                    default:
                        break;
                }
                getButtonNeutral().setVisible(true);
                getButtonNeutral().setText("back");

                parseBtn.setVisible(false);
                distributorsBrowseBtn.setVisible(true);
                orderUrlBrowseBtn.setVisible(true);
                break;
            default:
                break;
        }
    }

    private void createFilePanel() {
        filePanel.add(new JScrollPane(orderFileTable), BorderLayout.CENTER);
    }

    private void createDetailPanel() {
        JPanel datePanel = new JPanel(new GridBagLayout());
        JPanel numberPanel = new JPanel(new GridBagLayout());
        JPanel refPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        // Date panel
        // - Labels
        gbc.gridx = 0;gbc.weightx = 0;
        gbc.gridy = 0;gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        datePanel.add(new ILabel("Date modified: "), gbc);
        gbc.gridy++;
        datePanel.add(new ILabel("Date ordered: "), gbc);
        gbc.gridy++;
        datePanel.add(new ILabel("Date received: "), gbc);
        gbc.gridy++;

        // - Fields
        gbc.gridx = 1;gbc.weightx = 1;
        gbc.gridy = 0;gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;

        datePanel.add(dateModifiedTf, gbc);
        gbc.gridy++;
        datePanel.add(dateOrderedTf, gbc);
        gbc.gridy++;
        datePanel.add(dateReceivedTf, gbc);
        gbc.gridy++;

        TitledBorder dateBorder = PanelUtils.createTitleBorder("Dates");
        datePanel.setBorder(dateBorder);

        // Number panel
        // - Labels
        gbc.gridx = 0;gbc.weightx = 0;
        gbc.gridy = 0;gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        numberPanel.add(new ILabel("Ordered items: "), gbc);
        gbc.gridy++;
        numberPanel.add(new ILabel("Total price: "), gbc);

        // - Fields
        gbc.gridx = 1;gbc.weightx = 1;
        gbc.gridy = 0;gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;

        numberPanel.add(itemsTf, gbc);
        gbc.gridy++;
        numberPanel.add(totalPriceTf, gbc);

        TitledBorder numberBorder = PanelUtils.createTitleBorder("Dates");
        numberPanel.setBorder(numberBorder);


        // Ref panel
        // - Labels
        gbc.gridx = 0;gbc.weightx = 0;
        gbc.gridy = 0;gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        refPanel.add(new ILabel("Distributor ref: "), gbc);
        gbc.gridy++;
        refPanel.add(new ILabel("Tracking number: "), gbc);

        // - Fields
        gbc.gridx = 1;gbc.weightx = 1;
        gbc.gridy = 0;gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;

        refPanel.add(referenceTf, gbc);
        gbc.gridy++;
        refPanel.add(trackingNrTf, gbc);

        TitledBorder refBorder = PanelUtils.createTitleBorder("References");
        refPanel.setBorder(refBorder);

        // Add together
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.add(datePanel);
        detailPanel.add(numberPanel);
        detailPanel.add(refPanel);
    }

    private void createMainContainer() {
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel southPanel = new JPanel(new BorderLayout());

        northPanel.add(orderByLbl, BorderLayout.WEST);
        northPanel.add(fileOkLbl, BorderLayout.EAST);

        centerPanel.add(cardPanel);

        JPanel southExtra = new JPanel();
        southExtra.add(distributorsBrowseBtn);
        southExtra.add(orderUrlBrowseBtn);
        southExtra.add(viewParsedBtn);
        southExtra.add(copyToClipboardBtn);
        southExtra.add(parseBtn);
        southPanel.add(southExtra, BorderLayout.EAST);

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        TitledBorder mainBorder = PanelUtils.createTitleBorder(TAB_ORDER_FILE);
        mainPanel.setBorder(mainBorder);
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
        cardLayout = new CardLayout(5, 5);
        cardPanel = new JPanel(cardLayout);

        DefaultListModel<String> stepListModel = new DefaultListModel<>();
        stepListModel.addElement(TAB_ORDER_FILE);
        stepListModel.addElement(TAB_ORDER_DETAILS);
        stepList = new JList<>(stepListModel);
        stepList.setEnabled(false);
        stepList.setSelectedIndex(0);

        mainPanel = new JPanel(new BorderLayout());
        filePanel = new JPanel(new BorderLayout());
        detailPanel = new JPanel(new BorderLayout());

        // File panel
        orderByLbl = new ILabel();
        fileOkLbl = new ILabel(imageResource.readImage("Orders.Confirm.Error"));

        orderFileTableModel = new DefaultTableModel();
        orderFileTable = new JTable(orderFileTableModel);

        parseBtn = new JButton(imageResource.readImage("Orders.Confirm.Refresh"));
        parseBtn.setToolTipText("Parse again");
        parseBtn.addActionListener(this);
        copyToClipboardBtn = new JButton(imageResource.readImage("Orders.Confirm.Copy"));
        copyToClipboardBtn.setToolTipText("Copy to clipboard");
        copyToClipboardBtn.addActionListener(this);
        viewParsedBtn = new JButton(imageResource.readImage("Orders.Confirm.View"));
        viewParsedBtn.setToolTipText("View parsed file");
        viewParsedBtn.addActionListener(this);
        distributorsBrowseBtn = new JButton(imageResource.readImage("Orders.Confirm.BrowseDistributor"));
        distributorsBrowseBtn.setToolTipText("Browse distributor website");
        distributorsBrowseBtn.addActionListener(this);
        orderUrlBrowseBtn = new JButton(imageResource.readImage("Orders.Confirm.BrowseOrder"));
        orderUrlBrowseBtn.setToolTipText("Go to order page");
        orderUrlBrowseBtn.addActionListener(this);

        // Detail panel
        referenceTf = new ITextField("Distributor order reference");
        referenceTf.addEditedListener(this, "orderReference");
        trackingNrTf = new ITextField("Tracking number");
        trackingNrTf.addEditedListener(this, "trackingNumber");
        dateOrderedTf = new ITextField("Date ordered");
        dateOrderedTf.setEnabled(false);
        dateReceivedTf = new ITextField("Date received");
        dateReceivedTf.setEnabled(false);
        dateModifiedTf = new ITextField("Date modified");
        dateModifiedTf.setEnabled(false);
        itemsTf = new ITextField("# items");
        itemsTf.setEnabled(false);
        totalPriceTf = new ITextField("Total price");
        totalPriceTf.setEnabled(false);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        createFilePanel();
        createDetailPanel();
        createMainContainer();

        cardPanel.add(TAB_ORDER_FILE, filePanel);
        cardPanel.add(TAB_ORDER_DETAILS, detailPanel);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(new JScrollPane(stepList), BorderLayout.CENTER);
        listPanel.setBorder(BorderFactory.createEmptyBorder(16, 2, 2, 2));

        getContentPanel().add(listPanel, BorderLayout.WEST);
        getContentPanel().add(mainPanel, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            order = (Order) object[0];
            originalOrder = order.createCopy();

            // File
            if (order.getDistributor() != null) {
                orderByLbl.setText("Order by: " + order.getDistributor().getName());
            }

            // Details
            referenceTf.setText(order.getOrderReference());
            trackingNrTf.setText(order.getTrackingNumber());
            if (order.getDateModified() != null) {
                dateModifiedTf.setText(DateUtils.formatDateTimeLong(order.getDateModified()));
            } else {
                dateModifiedTf.setText("Not modified");
            }
            if (order.getDateOrdered() != null) {
                dateOrderedTf.setText(DateUtils.formatDateLong(order.getDateOrdered()));
            } else {
                dateOrderedTf.setText("Not ordered");
            }
            if (order.getDateReceived() != null) {
                dateReceivedTf.setText(DateUtils.formatDateLong(order.getDateReceived()));
            } else {
                dateReceivedTf.setText("Not received");
            }
            itemsTf.setText(String.valueOf(order.getOrderItems().size()));
            totalPriceTf.setText(String.valueOf(order.getTotalPrice()));

        }
    }
}