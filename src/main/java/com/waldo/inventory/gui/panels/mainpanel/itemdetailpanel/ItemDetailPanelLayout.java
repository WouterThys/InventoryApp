package com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel;

import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;

import javax.swing.*;
import java.awt.*;

public abstract class ItemDetailPanelLayout extends JPanel implements GuiInterface {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ILabel iconLabel;

    ITextField nameTextField;
    ITextArea divisionTa;
    ITextField manufacturerTextField;
    ITextArea descriptionTextArea;

    IStarRater starRater;
    ICheckBox  discourageOrder;
    ITextArea  remarksTa;

    JButton dataSheetButton;
    JButton orderButton;
    JButton historyButton;

    JPanel remarksPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Application application;

    /*
     *                  CONSTRUCTORS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ItemDetailPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private JPanel createIconPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(iconLabel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return panel;
    }

    private JPanel createComponentInfoPanel() {
        JPanel componentPanel = new JPanel(new GridBagLayout());

        ILabel nameLabel = new ILabel("Name: ", ILabel.RIGHT);
        nameLabel.setStatusInfo("Item name");
        ILabel divisionLabel = new ILabel("Division: ", ILabel.RIGHT);
        divisionLabel.setStatusInfo("Item division");
        ILabel manufacturerLabel = new ILabel("Manufacturer: ", ILabel.RIGHT);
        manufacturerLabel.setStatusInfo("Item manufacturer");
        ILabel descriptionLabel = new ILabel("Description: ", ILabel.RIGHT);
        descriptionLabel.setStatusInfo("Item description");

        // Helping lists
        JComponent[] labels = new JComponent[] {nameLabel, divisionLabel, manufacturerLabel};
        JComponent[] fields = new JComponent[] {nameTextField, new JScrollPane(divisionTa), manufacturerTextField};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        for (int i = 0; i < labels.length; i++) {
            //  - Label
            gbc.gridx = 0; gbc.weightx = 0;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            componentPanel.add(labels[i], gbc);
            //  - Field
            gbc.gridx = 1; gbc.weightx = 1;
            gbc.gridy = i; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            componentPanel.add(fields[i], gbc);
        }

        // Description
        //  - Label
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy++; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        componentPanel.add(descriptionLabel, gbc);
        //  - Field
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        componentPanel.add(new JScrollPane(descriptionTextArea), gbc);

        return componentPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        buttonsPanel.add(dataSheetButton, gbc);

        gbc.gridy++;
        buttonsPanel.add(orderButton, gbc);

        gbc.gridy++;
        buttonsPanel.add(historyButton, gbc);

        return buttonsPanel;
    }

    private JPanel createRemarksPanel() {
        remarksPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());

        northPanel.add(starRater, BorderLayout.WEST);
        northPanel.add(discourageOrder, BorderLayout.EAST);

        remarksPanel.add(northPanel, BorderLayout.NORTH);
        remarksPanel.add(new JScrollPane(remarksTa), BorderLayout.CENTER);
        remarksPanel.setBorder(BorderFactory.createEmptyBorder(5,10,2,10));

        return remarksPanel;

    }


     /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        iconLabel = new ILabel();
        iconLabel.setHorizontalAlignment(ILabel.CENTER);
        iconLabel.setVerticalAlignment(ILabel.CENTER);
        iconLabel.setPreferredSize(new Dimension(150,150));

        nameTextField = new ITextField();
        nameTextField.setEnabled(false);
        divisionTa = new ITextArea();
        divisionTa.setLineWrap(true);
        divisionTa.setWrapStyleWord(true);
        divisionTa.setEnabled(false);
        manufacturerTextField = new ITextField();
        manufacturerTextField.setEnabled(false);
        descriptionTextArea= new ITextArea();
        descriptionTextArea.setEnabled(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);

        starRater = new IStarRater(5);
        starRater.setEnabled(false);
        discourageOrder = new ICheckBox("Discourage order ");
        discourageOrder.setEnabled(false);
        discourageOrder.setHorizontalAlignment(SwingConstants.RIGHT);
        remarksTa = new ITextArea();
        remarksTa.setEnabled(false);
        remarksTa.setLineWrap(true);
        remarksTa.setWrapStyleWord(true);

        dataSheetButton = new JButton("Data sheet");
        orderButton = new JButton("Order");
        historyButton = new JButton("History");
        remarksPanel = new JPanel(new BorderLayout());

    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel helper = new JPanel(new BorderLayout());
        helper.add(createComponentInfoPanel(), BorderLayout.CENTER);
        helper.add(createRemarksPanel(), BorderLayout.EAST);


        add(createIconPanel(), BorderLayout.WEST);
        add(helper, BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.EAST);
    }
}
