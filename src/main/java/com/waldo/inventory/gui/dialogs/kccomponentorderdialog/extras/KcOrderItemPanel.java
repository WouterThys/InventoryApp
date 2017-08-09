package com.waldo.inventory.gui.dialogs.kccomponentorderdialog.extras;

import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.tablemodels.IKcOrderItemTableModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KcOrderItemPanel extends JPanel implements GuiInterface {

    public interface AmountChangeListener {
        void onAmountChanged(int amount);
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IKcOrderItemTableModel tableModel;
    private ITable<OrderItem> itemTable;

    private ITextField descriptionTf;
    private ITextField footprintTf;



    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Application application;
    private AmountChangeListener amountChangeListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public KcOrderItemPanel(Application application) {
        this.application = application;

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        itemTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void addOnAmountChangedListener(AmountChangeListener listener) {
        amountChangeListener = listener;
    }

    public void addOrderItem(OrderItem orderItem, boolean incrementIfKnown) {
        if (tableModel.getItemList().contains(orderItem)) {
            if (incrementIfKnown) {
                int ndx = tableModel.getItemList().indexOf(orderItem);
                OrderItem item = tableModel.getItemList().get(ndx);
                item.setAmount(item.getAmount() + 1);
                tableModel.updateTable();
            }
        } else {
            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem);
            tableModel.addItems(orderItems);
        }
    }

    public void removeOrderItem(OrderItem orderItem) {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        tableModel.removeItems(orderItems);
    }

    public boolean hasOrderItems() {
        return tableModel.getItemList().size() > 0;
    }

    public List<OrderItem> getOrderItems() {
        return tableModel.getItemList();
    }

    public void updateTable() {
        tableModel.updateTable();
    }

    public void setSelectedOrderItem(OrderItem orderItem) {
        itemTable.selectItem(orderItem);
    }

    public OrderItem getSelectedOrderItem() {
        int row = itemTable.getSelectedRow();
        if (row >= 0) {
            return (OrderItem) itemTable.getValueAtRow(row);
        }
        return null;
    }

    public void updateSelectedValueData(OrderItem orderItem) {
        if (orderItem != null) {
            Item item = orderItem.getItem();
            descriptionTf.setText(item.getDescription());
            if (item.getDimensionType() != null) {
                footprintTf.setText(item.getDimensionType().getName());
            } else {
                if (item.getPackage() != null && item.getPackage().getPackageType() != null) {
                    footprintTf.setText(item.getPackage().getPackageType().getName());
                } else {
                    footprintTf.setText("");
                }
            }
        } else {
            descriptionTf.clearText();
            footprintTf.clearText();
        }
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // - Description
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        southPanel.add(new ILabel("Description: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        southPanel.add(descriptionTf, gbc);

        // - FootPrint
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        southPanel.add(new ILabel("Foot print: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        southPanel.add(footprintTf, gbc);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 1),
                BorderFactory.createEmptyBorder(2,5,2,5)
        ));

        return southPanel;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        tableModel = new IKcOrderItemTableModel();
        itemTable = new ITable<>(tableModel);

        TableColumn tableColumn = itemTable.getColumnModel().getColumn(1);
        tableColumn.setCellEditor(new ITableEditors.SpinnerEditor() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (amountChangeListener != null) {
                    JSpinner spinner = (JSpinner) e.getSource();
                    amountChangeListener.onAmountChanged((int) spinner.getValue());
                }
            }
        });
        itemTable.getColumnModel().getColumn(1).setMinWidth(60);
        itemTable.getColumnModel().getColumn(1).setMaxWidth(60);

        // Text fields
        descriptionTf = new ITextField();
        footprintTf = new ITextField();

        descriptionTf.setEnabled(false);
        footprintTf.setEnabled(false);

    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JScrollPane pane = new JScrollPane(itemTable);
        pane.setPreferredSize(new Dimension(300, 300));

        // Add
        add(pane, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object object) {

    }
}