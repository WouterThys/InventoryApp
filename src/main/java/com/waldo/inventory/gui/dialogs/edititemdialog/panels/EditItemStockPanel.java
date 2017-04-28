package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class EditItemStockPanel extends JPanel implements GuiInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPanel.class);
    private static final String[] amountTypes = {"", "Max", "Min", "Exact", "Approximate"};

    private Item newItem;

    private JSpinner amountSpinner;
    private JComboBox<String> amountTypeCb;

    public EditItemStockPanel(Item newItem) {
        this.newItem = newItem;
    }

    public void setComponentValues() {
        newItem.setAmount((int)amountSpinner.getValue());
        newItem.setAmountType(amountTypeCb.getSelectedIndex());
    }

    private JPanel createAmountPanel() {
        JPanel amountPanel = new JPanel(new GridBagLayout());

        // Border
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Amount");
        titledBorder.setTitleJustification(TitledBorder.RIGHT);
        titledBorder.setTitleColor(Color.gray);

        // Labels
        ILabel amountLabel = new ILabel("Amount: ");
        amountLabel.setHorizontalAlignment(ILabel.RIGHT);
        amountLabel.setVerticalAlignment(ILabel.CENTER);

        ILabel amountTypeLabel = new ILabel("Type: ");
        amountTypeLabel.setHorizontalAlignment(ILabel.RIGHT);
        amountTypeLabel.setVerticalAlignment(ILabel.CENTER);

        // Grid bags
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Amount type
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountPanel.add(amountTypeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountPanel.add(amountTypeCb, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountPanel.add(amountLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountPanel.add(amountSpinner, gbc);

        amountPanel.setBorder(titledBorder);

        return amountPanel;
    }

    @Override
    public void initializeComponents() {
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        amountSpinner = new JSpinner(spinnerModel);

        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>(amountTypes);
        amountTypeCb = new JComboBox<>(cbModel);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(createAmountPanel());

    }

    @Override
    public void updateComponents(Object object) {
        if (newItem != null) {
            amountTypeCb.setSelectedIndex(newItem.getAmountType());
            amountSpinner.setValue(newItem.getAmount());
        }
    }
}
