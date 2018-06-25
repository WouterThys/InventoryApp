package com.waldo.inventory.gui.dialogs.orderdetailsdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

abstract class OrderDetailsCacheDialogLayout extends ICacheDialog implements ListSelectionListener, ActionListener, IEditedListener {

    private static final String TAB_ORDER_FILE = "ItemOrder file ";
    public static final String TAB_ORDER_DETAILS = "ItemOrder details";

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

    // ItemOrder file panel
    private JTable orderFileTable;
    private DefaultTableModel orderFileTableModel;

    // ItemOrder panel
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
    ItemOrder itemOrder, originalItemOrder;
    String currentPanel = TAB_ORDER_FILE;
    boolean parseSuccess = false;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderDetailsCacheDialogLayout(Application application, String title) {
        super(application, title);
        showTitlePanel(false);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    List<String> checkOrder(ItemOrder itemOrder) {
        List<String> errorList = new ArrayList<>();

        if (itemOrder == null) {
            errorList.add(" - No itemOrder selected..");
        } else {
            if (itemOrder.getDistributor() == null) {
                errorList.add(" - ItemOrder had no distributor..");
            } else {
                if (itemOrder.getDistributor().getOrderFileFormat() == null || itemOrder.getDistributor().getOrderFileFormat().isUnknown()) {
                    errorList.add(" - ItemOrder's distributor had no selected file format");
                } else {
//                    if (itemOrder.getItemOrderLines().size() < 1) {
//                        errorList.add(" - ItemOrder has no items..");
//                    } else {
//                        List<ItemOrderLine> errorItems = itemOrder.missingOrderReferences();
//                        if (errorItems.size() > 0) {
//                            errorList.add(" - Next itemOrder items have no reference: ");
//                            for (ItemOrderLine oi : errorItems) {
//                                errorList.add(" \t * " + oi.getName());
//                            }
//                        }
//                    }
                }
            }
        }


        return errorList;
    }

    void fillTableData() {
//        List<ItemOrderLine> orderItemList = itemOrder.getItemOrderLines();
//        try {
//            String[] references = new String[orderItemList.size()];
//            String[] amounts = new String[orderItemList.size()];
//            for (int i = 0; i < references.length; i++) {
//                ItemOrderLine orderItem = orderItemList.get(i);
//                references[i] = orderItem.getDistributorPartLink().getReference();
//                amounts[i] = String.valueOf(orderItem.getAmount());
//            }
//            orderFileTableModel = new DefaultTableModel();
//            orderFileTable.setModel(orderFileTableModel);
//            orderFileTableModel.addColumn("Reference", references);
//            orderFileTableModel.addColumn("Amount", amounts);
//            orderFileTableModel.fireTableDataChanged();
//        } catch (Exception e) {
//            Status().setError("Error filling table data.", e);
//        }
    }

    void updateEnabledComponents() {
        //getButtonOK().setEnabled(parseSuccess);
        viewParsedBtn.setEnabled(parseSuccess);
        copyToClipboardBtn.setEnabled(parseSuccess);
    }

    void updateVisibleComponents() {
        switch (currentPanel) {
            case TAB_ORDER_FILE:
                //getButtonOK().setText("next");
                //getButtonNeutral().setVisible(false);

                parseBtn.setVisible(true);
                distributorsBrowseBtn.setVisible(false);
                orderUrlBrowseBtn.setVisible(false);
                break;
            case TAB_ORDER_DETAILS:
//                switch (itemOrder.getOrderState()) {
//                    case Planned:
//                        getButtonOK().setText("itemOrder");
//                        break;
//                    case Ordered:
//                        getButtonOK().setText("received");
//                        break;
//                    case Received:
//                        getButtonOK().setText("ok");
//                        break;
//                    default:
//                        break;
//                }
//                getButtonNeutral().setVisible(true);
//                getButtonNeutral().setText("back");

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

        TitledBorder dateBorder = GuiUtils.createTitleBorder("Dates");
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

        TitledBorder numberBorder = GuiUtils.createTitleBorder("Dates");
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

        TitledBorder refBorder = GuiUtils.createTitleBorder("References");
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

        TitledBorder mainBorder = GuiUtils.createTitleBorder(TAB_ORDER_FILE);
        mainPanel.setBorder(mainBorder);
    }


    /*
    *                  LISTENERS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Main layout
        cardLayout = new CardLayout(5, 5);
        cardPanel = new JPanel(cardLayout);

        DefaultListModel<String> stepListModel = new DefaultListModel<>();
        stepListModel.addElement(TAB_ORDER_FILE);
        stepListModel.addElement(TAB_ORDER_DETAILS);
        stepList = new JList<>(stepListModel);
        stepList.setSelectedIndex(0);
        stepList.addListSelectionListener(this);

        mainPanel = new JPanel(new BorderLayout());
        filePanel = new JPanel(new BorderLayout());
        detailPanel = new JPanel(new BorderLayout());

        // File panel
        orderByLbl = new ILabel();
        fileOkLbl = new ILabel(imageResource.readIcon("Orders.Confirm.Error"));

        orderFileTableModel = new DefaultTableModel();
        orderFileTable = new JTable(orderFileTableModel);

        parseBtn = new JButton(imageResource.readIcon("Orders.Confirm.Refresh"));
        parseBtn.setToolTipText("Parse again");
        parseBtn.addActionListener(this);
        copyToClipboardBtn = new JButton(imageResource.readIcon("Orders.Confirm.Copy"));
        copyToClipboardBtn.setToolTipText("Copy to clipboard");
        copyToClipboardBtn.addActionListener(this);
        viewParsedBtn = new JButton(imageResource.readIcon("Orders.Confirm.View"));
        viewParsedBtn.setToolTipText("View parsed file");
        viewParsedBtn.addActionListener(this);
        distributorsBrowseBtn = new JButton(imageResource.readIcon("Orders.Confirm.BrowseDistributor"));
        distributorsBrowseBtn.setToolTipText("Browse distributor website");
        distributorsBrowseBtn.addActionListener(this);
        orderUrlBrowseBtn = new JButton(imageResource.readIcon("Orders.Confirm.BrowseOrder"));
        orderUrlBrowseBtn.setToolTipText("Go to itemOrder page");
        orderUrlBrowseBtn.addActionListener(this);

        // Detail panel
        referenceTf = new ITextField("Distributor itemOrder reference");
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
            itemOrder = (ItemOrder) object[0];
            originalItemOrder = itemOrder.createCopy();

            // File
            if (itemOrder.getDistributor() != null) {
                orderByLbl.setText("ItemOrder by: " + itemOrder.getDistributor().getName());
            }

            // Details
            referenceTf.setText(itemOrder.getOrderReference());
            trackingNrTf.setText(itemOrder.getTrackingNumber());
            if (itemOrder.getDateModified() != null) {
                dateModifiedTf.setText(DateUtils.formatDateTimeLong(itemOrder.getDateModified()));
            } else {
                dateModifiedTf.setText("Not modified");
            }
            if (itemOrder.getDateOrdered() != null) {
                dateOrderedTf.setText(DateUtils.formatDateLong(itemOrder.getDateOrdered()));
            } else {
                dateOrderedTf.setText("Not ordered");
            }
            if (itemOrder.getDateReceived() != null) {
                dateReceivedTf.setText(DateUtils.formatDateLong(itemOrder.getDateReceived()));
            } else {
                dateReceivedTf.setText("Not received");
            }
//            itemsTf.setText(String.valueOf(itemOrder.getItemOrderLines().size()));
//            totalPriceTf.setText(String.valueOf(itemOrder.getTotalPrice()));

        }
    }
}