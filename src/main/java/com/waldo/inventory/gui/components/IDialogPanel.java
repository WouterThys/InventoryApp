package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IDialogPanel extends JPanel {

    private JPanel contentPanel;
    private JPanel buttonsPanel;

    protected ResourceManager resourceManager;

    public IDialogPanel() {
        super(new BorderLayout());
        contentPanel = new JPanel();
        add(contentPanel, BorderLayout.CENTER);
        buttonsPanel = new JPanel(new GridBagLayout());
        add(buttonsPanel, BorderLayout.SOUTH);

        URL url = Error.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
    }

//    @Override
//    public Component add(Component comp) {
//        return contentPanel.add(comp);
//    }

//    @Override
//    public void setLayout(LayoutManager mgr) {
//        if (contentPanel == null) {
//            super.setLayout(mgr);
//        } else {
//            contentPanel.setLayout(mgr);
//        }
//    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setNegativeButton(JButton button) {
        buttonsPanel.add(button, PanelUtils.createButtonConstraints(0,0));
    }

    public void setNeutralButton(JButton button) {
        buttonsPanel.add(button, PanelUtils.createButtonConstraints(1,0));
    }

    public void setPositiveButton(JButton button) {
        buttonsPanel.add(button, PanelUtils.createButtonConstraints(2,0));
    }
}
