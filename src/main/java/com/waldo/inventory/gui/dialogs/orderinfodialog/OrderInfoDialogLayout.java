package com.waldo.inventory.gui.dialogs.orderinfodialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.OrderFile;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextArea;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.waldo.inventory.gui.Application.imageResource;


public abstract class OrderInfoDialogLayout extends IDialog implements GuiInterface {

    private OrderFile orderFile;

    private ITextField orderNameTf;
    private ITextArea orderInfoTf;
    private ITextField orderFilePathTf;
    private ITextField orderFileNameTf;
    ITextField orderDistributorUrlTf;
    JButton orderUrlBrowseBtn;

    OrderInfoDialogLayout(Application application,String title) {
        super(application, title);
        showTitlePanel(false);
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
        orderUrlBrowseBtn = new JButton(imageResource.readImage("Common.BrowseWebSiteIcon"));
    }

    @Override
    public void initializeLayouts() {

        JPanel browsePanel = PanelUtils.createBrowsePanel(orderDistributorUrlTf, orderUrlBrowseBtn);

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
            orderFile = (OrderFile) object;

            orderNameTf.setText(orderFile.getOrder().getName());
            orderFileNameTf.setText(orderFile.getOrderFile().getName());
            orderFilePathTf.setText(orderFile.getOrderFile().getAbsolutePath());
            orderInfoTf.setEnabled(orderFile.getOrderType() == 2);

            switch (orderFile.getOrderType()) {
                case 2: // Mouser
                    orderDistributorUrlTf.setText(application.stringResource.readString("OrderInfo.MouserUrl"));
                    String txt = createMouserOrderText();
                    orderInfoTf.setText(txt);
                    break;
                case 3: // Farnell
                    orderDistributorUrlTf.setText(application.stringResource.readString("OrderInfo.FarnellUrl"));
                    orderInfoTf.setText(application.stringResource.readString("OrderInfo.Farnell"));
                    break;
            }
        }
    }

    private String createMouserOrderText() {
        String result = "";
        if (orderFile != null) {
            File order = orderFile.getOrderFile();
            if (order.exists()) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(order));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] parts = line.split(OrderFile.SEPARATOR);
                        result += parts[0] + " " + parts[1] + "\n";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return result;
    }
}
