package com.waldo.inventory.gui.dialogs.editorderinvoicedialog;

import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.inventory.gui.components.tablemodels.IInvoiceOrderLineTableModel;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITextField;
import com.waldo.utils.icomponents.ITextPane;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

abstract class EditOrderInvoiceDialogLayout extends iDialog implements IEditedListener<AbstractOrder> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Order
    private ITextField vatTf;
    private ITextField priceIncTf;
    private ITextField priceExcTf;

    // Lines
    private IInvoiceOrderLineTableModel tableModel;
    private ITable<AbstractOrderLine> lineTable;

    // Order text
    private ITextPane orderTextPane;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AbstractOrder selectedOrder;
    AbstractOrder originalOrder;
    AbstractOrderLine selectedOrderLine;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditOrderInvoiceDialogLayout(Window window, AbstractOrder order, AbstractOrderLine orderLine) {
        super(window, "Invoice");

        this.originalOrder = order;
        this.selectedOrder = (AbstractOrder) order.createCopy();
        this.selectedOrderLine = orderLine;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void updatePriceFields() {
        priceExcTf.setText(selectedOrder.getTotalPriceExc().toString());
        priceIncTf.setText(selectedOrder.getTotalPriceInc().toString());
    }


    private void initTable() {
        if (selectedOrder != null) {
            tableModel.setItemList(selectedOrder.getOrderLines());
        }
    }


    private JPanel createOrderPanel() {
        JPanel panel = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.addLine("VAT: ", vatTf);
        gbc.addLine("Total (exc): ", priceExcTf);
        gbc.addLine("Total (inc): ", priceIncTf);

        return panel;
    }

    private JPanel createOrderTextPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(orderTextPane);
        scrollPane.setMinimumSize(new Dimension(400, 300));
        scrollPane.setPreferredSize(new Dimension(400, 300));

        panel.add(scrollPane);

        return panel;
    }

    private JPanel createOrderLinePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(lineTable);
        scrollPane.setMinimumSize(new Dimension(400, 300));
        scrollPane.setPreferredSize(new Dimension(400, 300));

        panel.add(scrollPane);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

        vatTf = new ITextField();
        vatTf.addEditedListener(this, "VAT", double.class);
        priceIncTf = new ITextField(false);
        priceExcTf = new ITextField(false);

        tableModel = new IInvoiceOrderLineTableModel();
        lineTable = new ITable<>(tableModel);

        orderTextPane = new ITextPane();
    }

    @Override
    public void initializeLayouts() {

        JPanel firstTab = new JPanel(new BorderLayout());
        JPanel orderPnl = createOrderPanel();
        JPanel linePnl = createOrderLinePanel();

        orderPnl.setBorder(GuiUtils.createInlineTitleBorder("Order"));
        linePnl.setBorder(GuiUtils.createInlineTitleBorder("Lines"));

        firstTab.add(orderPnl, BorderLayout.NORTH);
        firstTab.add(linePnl, BorderLayout.CENTER);


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Order", imageResource.readIcon("Order.SS"), firstTab);
        tabbedPane.addTab("File", createOrderTextPanel());


        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(tabbedPane);

        //..
        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (selectedOrder != null) {
            updatePriceFields();
            initTable();
        }
    }
}