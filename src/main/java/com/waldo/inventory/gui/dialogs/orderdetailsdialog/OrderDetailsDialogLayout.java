package com.waldo.inventory.gui.dialogs.orderdetailsdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public abstract class OrderDetailsDialogLayout extends IDialog implements
        GuiInterface,
        IEditedListener {

    Order order;
    Order originalOrder;

    private JTabbedPane tabbedPane;

    ITextField dateOrderedTf;
    ITextField dateReceivedTf;
    ITextField dateModifiedTf;

    ITextField orderReferenceTf;
    ITextField trackingNrTf;

    ITextField itemsTf;
    ITextField totalPriceTf;

    IComboBox<Distributor> distributorCb;
    ITextField distributorWebsiteTf;
    JButton distributorsBrowseBtn;

    JPanel orderFileView;
    ITextArea orderFileTa;
    private DefaultTableModel tableModel;

    ITextField orderUrlTf;
    JButton orderUrlBrowseBtn;
    JButton copyToClipboardBtn;
    JToggleButton formattedBtn;

    OrderDetailsDialogLayout(Application application, String title) {
        super(application, title);
    }

    void fillTableData(DefaultTableModel model, String rawData) {
        try {
            String[] parts = rawData.split("\n");
            String[] references = new String[parts.length];
            String[] amounts = new String[parts.length];
            for (int i = 0; i < parts.length; i++) {
                String[] subParts = parts[i].split(""); // TODO get from OrderFileFormat
                references[i] = subParts[0];
                amounts[i] = subParts[1];
            }
            model.addColumn("Reference", references);
            model.addColumn("Amount", amounts);
            model.fireTableDataChanged();
        } catch (Exception e) {
            Status().setError("Error filling table data.", e);
        }
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JPanel browsePanel = PanelUtils.createBrowsePanel(distributorWebsiteTf, distributorsBrowseBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // - Dates
        detailsPanel.add(new ITitledEditPanel(
                "Dates",
                new String[] {"Ordered: ", "Received: ", "Modified: "},
                new JComponent[] {dateOrderedTf, dateReceivedTf, dateModifiedTf}
        ));

        // - Numbers
        detailsPanel.add(new ITitledEditPanel(
                "Numbers",
                new String[] {"Items: ", "Total price: "},
                new JComponent[] {itemsTf, totalPriceTf}
        ));

        // - Distributor
        detailsPanel.add(new ITitledEditPanel(
                "Distributor",
                new String[] {"Name: ", "Web site: "},
                new JComponent[]{distributorCb, browsePanel}
        ));

        // - Ref and track
        detailsPanel.add(new ITitledEditPanel(
                "References",
                new String[]{"Distributor: ", "Tracking: "},
                new JComponent[]{orderReferenceTf, trackingNrTf}
        ));

        return detailsPanel;
    }

    private JPanel createOrderFilePanel() {
        JPanel orderFilePanel = new JPanel(new GridBagLayout());

        // Labels
        ILabel browseLabel = new ILabel("Browse: ");
        browseLabel.setHorizontalAlignment(ILabel.RIGHT);
        browseLabel.setVerticalAlignment(ILabel.CENTER);

        // Extra
        JPanel browsePanel = PanelUtils.createBrowsePanel(orderUrlTf, orderUrlBrowseBtn);

        // Add to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        orderFilePanel.add(copyToClipboardBtn, gbc);

        gbc.gridx = 1; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        orderFilePanel.add(formattedBtn, gbc);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        orderFilePanel.add(orderFileView, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        orderFilePanel.add(browseLabel, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        orderFilePanel.add(browseLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        orderFilePanel.add(browsePanel, gbc);

        orderFilePanel.setBorder(BorderFactory.createEmptyBorder(5,2,5,2));


        return orderFilePanel;
    }

    @Override
    public void initializeComponents() {
        // Title and save button
        setTitleIcon(imageResource.readImage("OrdersDialog.TitleIcon"));
        getButtonNeutral().setVisible(true);
        getButtonNeutral().setText("Save");
        getButtonNeutral().setEnabled(false);

        // Tabs
        tabbedPane = new JTabbedPane();

        // Dates
        dateOrderedTf = new ITextField("Date ordered");
        dateOrderedTf.setEnabled(false);
        dateReceivedTf = new ITextField("Date received");
        dateReceivedTf.setEnabled(false);
        dateModifiedTf = new ITextField("Last modified");
        dateModifiedTf.setEnabled(false);

        // Order
        orderReferenceTf = new ITextField("Order reference");
        orderReferenceTf.addEditedListener(this, "orderReference");
        trackingNrTf = new ITextField("Tracking number");
        trackingNrTf.addEditedListener(this, "trackingNumber");
        itemsTf = new ITextField("Number of items");
        itemsTf.setEnabled(false);
        totalPriceTf = new ITextField("Total price");
        totalPriceTf.setEnabled(false);

        // Distributor
        DefaultComboBoxModel<Distributor> model = new DefaultComboBoxModel<>();
        for (Distributor d : DbManager.db().getDistributors()) {
            model.addElement(d);
        }
        distributorCb = new IComboBox<>(model);
        distributorCb.addEditedListener(this, "distributor");
        distributorWebsiteTf = new ITextField("Web site");
        distributorWebsiteTf.setEnabled(false);
        distributorsBrowseBtn = new JButton(imageResource.readImage("Common.BrowseWebSiteIcon"));

        // Order file
        orderFileTa = new ITextArea("Order file");
        orderFileTa.setEnabled(false);
        tableModel = new DefaultTableModel();
        JTable orderFileTable = new JTable(tableModel);
        orderFileTable.setEnabled(false);
        orderUrlTf = new ITextField("Order url");
        orderUrlTf.setEnabled(false);
        orderUrlBrowseBtn = new JButton(imageResource.readImage("Common.BrowseWebSiteIcon"));
        copyToClipboardBtn = new JButton(imageResource.readImage("Common.Copy"));
        copyToClipboardBtn.setToolTipText("Copy to clipboard");
        formattedBtn = new JToggleButton("Format", false);

        orderFileView = new JPanel(new CardLayout());
        orderFileView.add("TextField", new JScrollPane(orderFileTa));
        orderFileView.add("Table", new JScrollPane(orderFileTable));

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        tabbedPane.add("Details", createDetailsPanel());
        tabbedPane.add("Order file", createOrderFilePanel());

        getContentPanel().add(tabbedPane);
        pack();
    }
}
