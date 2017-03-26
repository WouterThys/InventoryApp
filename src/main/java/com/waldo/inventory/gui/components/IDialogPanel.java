package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.Error;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.Utils.RoundedButtonBorder;
import com.waldo.inventory.gui.Application;
import sun.awt.WindowClosingListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.net.URL;

import static javax.swing.SpringLayout.*;

public abstract class IDialogPanel extends JPanel {

    protected static JDialog dialog;
    protected static Application application;

    private JPanel contentPanel;
    private JPanel buttonsPanel;

    protected JButton positiveButton;
    protected JButton neutralButton;
    protected JButton negativeButton;

    protected ResourceManager resourceManager;

    public IDialogPanel() {
        super(new BorderLayout());
        contentPanel = new JPanel();
        add(contentPanel, BorderLayout.CENTER);
        buttonsPanel = createButtonPanel();
        buttonsPanel.setVisible(false);
        add(buttonsPanel, BorderLayout.SOUTH);

        URL url = Error.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
    }

    protected void close() {
        if (dialog != null) {
            dialog.setVisible(false);
            dialog.dispose();
        }
    }

    protected JPanel getContentPanel() {
        return contentPanel;
    }

    protected JButton setNegativeButton(String text) {
        negativeButton.setVisible(true);
        negativeButton.setText(text);
        buttonsPanel.setVisible(true);
        return negativeButton;
    }

    protected JButton setNeutralButton(String text) {
        neutralButton.setVisible(true);
        buttonsPanel.setVisible(true);
        neutralButton.setText(text);
        return neutralButton;
    }

    protected JButton setPositiveButton(String text) {
        positiveButton.setVisible(true);
        buttonsPanel.setVisible(true);
        positiveButton.setText(text);
        return positiveButton;
    }

    private JPanel createButtonPanel() {
        positiveButton = new JButton("Ok");
        neutralButton = new JButton("Meh");
        negativeButton = new JButton("Cancel");

        positiveButton.setVisible(false);
        neutralButton.setVisible(false);
        negativeButton.setVisible(false);

        positiveButton.setBorder(new RoundedButtonBorder(5));
        neutralButton.setBorder(new RoundedButtonBorder(5));
        negativeButton.setBorder(new RoundedButtonBorder(5));

        positiveButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        negativeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        neutralButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        };

        positiveButton.addActionListener(action);
        negativeButton.addActionListener(action);
        neutralButton.addActionListener(action);

        positiveButton.setPreferredSize(new Dimension(90,25));
        neutralButton.setPreferredSize(new Dimension(90,25));
        negativeButton.setPreferredSize(new Dimension(90,25));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5,5));
        panel.add(negativeButton);
        panel.add(neutralButton);
        panel.add(positiveButton);

        return panel;
    }


}
