package com.waldo.inventory.gui.dialogs.orderinfodialog;

import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.OrderFile;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextArea;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.dialogs.manufacturerdialog.ManufacturersDialogLayout;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialogLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;


public class OrderInfoDialogLayout extends IDialog implements GuiInterface {


    private ITextField orderNameTf;
    private ITextArea orderInfoTf;
    private ITextField orderFilePathTf;
    private ITextField orderFileNameTf;
    private ITextField orderDistributorUrlTf;
    private JButton orderUrlBrowseBtn;

    private ResourceManager stringResource;

    OrderInfoDialogLayout(Application application,String title) {
        super(application, title);
        showTitlePanel(false);

        URL url = OrdersDialogLayout.class.getResource("/settings/Strings.properties");
        stringResource = new ResourceManager(url.getPath());
    }


    @Override
    public void initializeComponents() {
        orderNameTf = new ITextField();
        orderNameTf.setEnabled(false);
        orderInfoTf = new ITextArea("", 4,15);
        orderInfoTf.setEnabled(false);
        orderFilePathTf = new ITextField();
        orderFilePathTf.setEditable(false);
        orderFileNameTf = new ITextField();
        orderFileNameTf.setEditable(false);
        orderDistributorUrlTf = new ITextField();
        orderDistributorUrlTf.setEditable(false);
        orderUrlBrowseBtn = new JButton(resourceManager.readImage("Common.BrowseWebSiteIcon"));
        orderUrlBrowseBtn.addActionListener(e -> {
            if (!orderDistributorUrlTf.getText().isEmpty())
                try {
                    OpenUtils.browseLink(orderDistributorUrlTf.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(OrderInfoDialogLayout.this, "Unable to browse: " + orderDistributorUrlTf.getText(), "Browse error", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
        });
    }

    @Override
    public void initializeLayouts() {

        JPanel browsePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; constraints.weightx = 1;
        constraints.gridy = 0; constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        browsePanel.add(orderDistributorUrlTf, constraints);
        constraints.gridx = 1; constraints.weightx = 0;
        constraints.gridy = 0; constraints.weighty = 0;
        constraints.fill = GridBagConstraints.VERTICAL;
        browsePanel.add(orderUrlBrowseBtn, constraints);

        ILabel[] labels = {
                new ILabel("Name: "),
                new ILabel("File name: "),
                new ILabel("File path: "),
                new ILabel("Info: "),
                new ILabel("URL: ")
        };

        Component[] components = {
                orderNameTf,
                orderFileNameTf,
                orderFilePathTf,
                orderInfoTf,
                browsePanel
        };

        getContentPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,8,2);

        for (int i = 0; i < labels.length; i++) {
            ILabel label = labels[i];
            label.setHorizontalAlignment(ILabel.RIGHT);
            label.setVerticalAlignment(ILabel.CENTER);
            // Label
            gbc.gridx = 0; gbc.weightx = 0;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            getContentPanel().add(label, gbc);

            // Component
            gbc.gridx = 1; gbc.weightx = 1;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            getContentPanel().add(components[i], gbc);

        }

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            OrderFile orderFile = (OrderFile) object;

            orderNameTf.setText(orderFile.getOrder().getName());
            orderFileNameTf.setText(orderFile.getOrderFile().getName());
            orderFilePathTf.setText(orderFile.getOrderFile().getAbsolutePath());
            orderDistributorUrlTf.setText(stringResource.readString("OrderInfo.FarnellUrl"));

            switch (orderFile.getOrderType()) {
                case 2: // Mouser
                    break;
                case 3: // Farnell
                    orderInfoTf.setText(stringResource.readString("OrderInfo.Farnell"));
                    break;
            }
        }
    }
}
