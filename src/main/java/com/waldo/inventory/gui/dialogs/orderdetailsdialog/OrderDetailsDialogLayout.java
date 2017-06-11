package com.waldo.inventory.gui.dialogs.orderdetailsdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

public abstract class OrderDetailsDialogLayout extends IDialog implements
        GuiInterface,
        IEditedListener {

    Order order;
    Order originalOrder;

    JTabbedPane tabbedPane;

    ITextField dateOrderedTf;
    ITextField dateReceivedTf;
    ITextField dateModifiedTf;

    ITextField orderReferenceTf;
    ITextField trackingNrTf;

    IComboBox<Distributor> distributorCb;
    ITextField distributorWebsiteTf;
    JButton distributorsBrowseBtn;

    ITextArea orderFileTa;
    ITextField orderUrlTf;
    JButton orderUrlBrowseBtn;

    OrderDetailsDialogLayout(Application application, String title) {
        super(application, title);
    }

    void updateEnabledComponents() {

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

        return orderFilePanel;
    }

    @Override
    public void initializeComponents() {
        // Title and save button
        setTitleIcon(resourceManager.readImage("OrdersDialog.TitleIcon"));
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

        // Distributor
        DefaultComboBoxModel<Distributor> model = new DefaultComboBoxModel<>();
        for (Distributor d : DbManager.db().getDistributors()) {
            model.addElement(d);
        }
        distributorCb = new IComboBox<>(model);
        distributorCb.addEditedListener(this, "distributor");
        distributorWebsiteTf = new ITextField("Web site");
        distributorWebsiteTf.setEnabled(false);
        distributorsBrowseBtn = new JButton(resourceManager.readImage("Common.BrowseWebSiteIcon"));

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
