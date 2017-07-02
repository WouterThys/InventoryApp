package com.waldo.inventory.gui.dialogs.orderconfirmdialog;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;

public abstract class OrderConfirmDialogLayout extends IDialog implements GuiInterface {

    Order order;

    ITextField referenceTf;
    ITextField trackingNrTf;

    OrderConfirmDialogLayout(Application application, String title) {
        super(application, title);
        showTitlePanel(false);
    }


    @Override
    public void initializeComponents() {
        referenceTf = new ITextField("Distributor order reference");
        trackingNrTf = new ITextField("Tracking number");
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());

        // Labels
        ILabel referenceLabel = new ILabel("Reference: ");
        referenceLabel.setHorizontalAlignment(ILabel.RIGHT);
        referenceLabel.setVerticalAlignment(ILabel.CENTER);
        ILabel trackingLabel = new ILabel("Tracking: ");
        trackingLabel.setHorizontalAlignment(ILabel.RIGHT);
        trackingLabel.setVerticalAlignment(ILabel.CENTER);

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // - Reference Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(referenceLabel, gbc);

        // - Reference field
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(referenceTf, gbc);

        // - Tracking Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(trackingLabel, gbc);

        // - Tracking field
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(trackingNrTf, gbc);

        // - Border
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            order = (Order) object;

            referenceTf.setText(order.getOrderReference());
            trackingNrTf.setText(order.getTrackingNumber());
        }
    }
}
