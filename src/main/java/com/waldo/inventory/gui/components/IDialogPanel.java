package com.waldo.inventory.gui.components;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.Utils.RoundedButtonBorder;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.VERTICAL_CENTER;

public abstract class IDialogPanel extends JPanel {

    public static final int OK = 1;
    public static final int NEUTRAL = 0;
    public static final int CANCEL = -1;
    protected static int returnValue = -1;

    protected JDialog dialog;
    protected Application application;

    private JPanel titlePanel;
    private JPanel contentPanel;
    private JPanel buttonsPanel;

    private ILabel titleIconLabel;
    private ILabel titleNameLabel;

    protected JButton positiveButton;
    protected JButton neutralButton;
    protected JButton negativeButton;

    protected ResourceManager resourceManager;

    public IDialogPanel(Application application, JDialog dialog, boolean showTitlePanel) {
        super(new BorderLayout());
        this.application = application;
        this.dialog = dialog;

        URL url = IDialogPanel.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());

        titlePanel = createTitlePanel();
        titlePanel.setVisible(showTitlePanel);
        add(titlePanel, BorderLayout.NORTH);
        contentPanel = new JPanel();
        add(contentPanel, BorderLayout.CENTER);
        buttonsPanel = createButtonPanel();
        buttonsPanel.setVisible(false);
        add(buttonsPanel, BorderLayout.SOUTH);
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

    protected void setTitleName(String name) {
        titleNameLabel.setText(name);
    }

    protected void setTitleIcon(Icon icon) {
        titleIconLabel.setIcon(icon);
    }

    protected ILabel getTitleNameLabel() {
        return titleNameLabel;
    }

    protected ILabel getTitleIconLabel() {
        return titleIconLabel;
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

        positiveButton.addActionListener(e -> {
            returnValue = OK;
            close();
        });
        negativeButton.addActionListener(e -> {
            returnValue = CANCEL;
            close();
        });
        neutralButton.addActionListener(e -> {
            returnValue = NEUTRAL;
            close();
        });

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

    private JPanel createTitlePanel() {
        // Components
        titleIconLabel = new ILabel(resourceManager.readImage("Common.UnknownIcon48"));
        titleIconLabel.setPreferredSize(new Dimension(48,48));
        titleNameLabel = new ILabel("?");
        titleNameLabel.setFontSize(36);

        // Panel
        JPanel titlePanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        layout.putConstraint(WEST, titleIconLabel, 5, WEST, titlePanel);
        layout.putConstraint(NORTH, titleIconLabel, 5, NORTH, titlePanel);
        layout.putConstraint(SOUTH, titleIconLabel, -5, SOUTH, titlePanel);

        layout.putConstraint(HORIZONTAL_CENTER, titleNameLabel, 0, HORIZONTAL_CENTER, titlePanel);
        layout.putConstraint(VERTICAL_CENTER, titleNameLabel, 0, VERTICAL_CENTER, titlePanel);

        titlePanel.add(titleIconLabel, BorderLayout.WEST);
        titlePanel.add(titleNameLabel, BorderLayout.CENTER);
        titlePanel.setPreferredSize(new Dimension(200, 60));
        titlePanel.setLayout(layout);

        return titlePanel;
    }
}
