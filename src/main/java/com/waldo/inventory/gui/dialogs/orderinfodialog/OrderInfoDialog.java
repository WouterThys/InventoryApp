package com.waldo.inventory.gui.dialogs.orderinfodialog;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.waldo.inventory.Utils.OpenUtils;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.io.IOException;

public class OrderInfoDialog extends OrderInfoDialogLayout {

    public OrderInfoDialog(Application application, String title, Order order) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        initActions();
        updateComponents(order);
    }

    private void initActions() {
        orderUrlBrowseBtn.addActionListener(e -> {
            if (!orderDistributorUrlTf.getText().isEmpty()) {
//                try {
//                    superBrowse(orderDistributorUrlTf.getText());
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
                try {
                    OpenUtils.browseLink(orderDistributorUrlTf.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(OrderInfoDialog.this, "Unable to browse: " + orderDistributorUrlTf.getText(), "Browse error", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        });
    }

    private void superBrowse(String url) throws IOException {
        WebClient webClient = null;
        try {
            webClient = new WebClient();
            final HtmlPage page = webClient.getPage("https://be.farnell.com");
            final HtmlForm form = page.getFormByName("Logon");

            final HtmlTextInput logonIdField = form.getInputByName("logonId");
            final HtmlPasswordInput logonPwField = form.getInputByName("logonPassword");

        } finally {
            if (webClient != null) {
                webClient.closeAllWindows();
            }
        }
    }
}
