package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderLine;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IOrderFlowPanel;
import com.waldo.inventory.gui.components.ITablePanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IOrderLineTableModel;
import com.waldo.inventory.gui.components.trees.IOrderTree;
import com.waldo.inventory.gui.panels.mainpanel.AbstractDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.OrderDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.preview.ItemPreviewPanel;
import com.waldo.inventory.gui.panels.mainpanel.preview.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.orderpanel.preview.OrderPreviewPanel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITableEditors;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;

abstract class OrderPanelLayout extends IPanel implements
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ItemDetailListener,
        OrderDetailListener {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITablePanel<OrderLine> orderLineTable;
    IOrderLineTableModel tableModel;

    IOrderTree ordersTree;
    AbstractDetailPanel detailPanel;
    OrderPreviewPanel previewPanel;

    private JPanel orderTbPanel;
    private IOrderFlowPanel tbOrderFlowPanel;
    private ILabel tbOrderNameLbl;

    private JPanel tbOrderFilePanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;

    OrderLine selectedOrderLine;
    Order selectedOrder;

    Order rootOrder;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    abstract void onTreeRightClick(MouseEvent e);
    abstract void onTableRowClicked(MouseEvent e);

    abstract void onMoveToOrdered(Order order);
    abstract void onMoveToReceived(Order order);
    abstract void onBackToOrdered(Order order);
    abstract void onBackToPlanned(Order order);

    abstract void onDeleteOrderItem(OrderLine orderItem);
    abstract void onEditItem(Item orderItem);


    Order getSelectedOrder() {
        return selectedOrder;
    }

    //
    // Tree stuff
    //

    void treeDeleteOrder(Order order) {
        ordersTree.removeOrder(order);
    }

    void treeReload() {
        ordersTree.structureChanged();
    }

    long treeUpdate() {
        long orderId = -1;
        if (selectedOrder != null) {
            orderId = selectedOrder.getId();
        }
        ordersTree.updateTree();
        ordersTree.expandAll();
        return orderId;
    }

    void treeSelectOrder(Order order) {
        ordersTree.setSelectedItem(order);
    }


    //
    // Table stuff
    //
    public void tableInitialize(Order order) {
        if (order != null && !order.getName().equals("All")) {
            tableModel.setItemList(order.getOrderLines());
            orderLineTable.resizeColumns();
        }
    }

    long tableUpdate() {
        long orderItemId = -1;
        if (selectedOrderLine != null) {
            orderItemId = selectedOrderLine.getId();
        }
        tableModel.updateTable();
        return orderItemId;
    }

    void tableClear() {
        tableModel.setItemList(new ArrayList<>());
    }

    private void tableAddOrderItems(List<OrderLine> orderItems) {
        tableModel.addItems(orderItems);
    }

    void tableAddOrderItem(OrderLine orderItem) {
        List<OrderLine> orderItems = new ArrayList<>(1);
        orderItems.add(orderItem);
        tableAddOrderItems(orderItems);
    }

    void tableSelectOrderItem(OrderLine orderItem) {
        orderLineTable.selectItem(orderItem);
    }

    OrderLine tableGetSelectedItem() {
        return orderLineTable.getSelectedItem();
    }

    List<OrderLine> tableGetAllSelectedOrderItems() {
        return orderLineTable.getAllSelectedItems();
    }


    //
    // Table tool bar stuff
    //
    void updateToolBar(Order order) {
        if (order != null) {
            tbOrderNameLbl.setText(order.getName());

            if (order.getOrderState() != Statics.ItemOrderStates.Planned && !order.isLocked()) {
                orderLineTable.setHeaderPanelBackground(Color.red);
            } else {
                orderLineTable.setHeaderPanelBackground(null);
            }
        } else {
            tbOrderNameLbl.setText("");
            orderLineTable.setHeaderPanelBackground(null);
        }
    }

    void updateEnabledComponents() {
        boolean orderSelected = (selectedOrder != null && !selectedOrder.isUnknown() && selectedOrder.canBeSaved());
        boolean itemSelected = (selectedOrderLine != null && !selectedOrderLine.isUnknown());
        boolean locked = orderSelected && selectedOrder.isLocked();

        if (orderSelected) {
            orderLineTable.setDbToolBarEnabled(true);
            orderLineTable.setDbToolBarEditDeleteEnabled(itemSelected && !locked);
        } else {
            orderLineTable.setDbToolBarEnabled(true);
        }

        tbOrderFlowPanel.updateComponents(selectedOrder);
        previewPanel.updateComponents(selectedOrder);
    }

    void updateVisibleComponents() {
        orderTbPanel.setVisible(true);
        tbOrderFilePanel.setVisible(true);
    }

    private JPanel createOrderToolbar() {
        tbOrderFilePanel = new JPanel(new GridBagLayout());

        // Distributor
        JToolBar toolBar = GuiUtils.createNewToolbar();
        toolBar.setOpaque(false);

        JPanel makeOrderPanel = new JPanel(new BorderLayout());
        makeOrderPanel.setOpaque(false);

        makeOrderPanel.add(tbOrderNameLbl, BorderLayout.CENTER);
        makeOrderPanel.add(toolBar, BorderLayout.EAST);

        // Create panel
        orderTbPanel = new JPanel();
        orderTbPanel.add(makeOrderPanel);
        orderTbPanel.setVisible(false);

        tbOrderFilePanel.setOpaque(false);
        orderTbPanel.setOpaque(false);

        return orderTbPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Sub division tree
        rootOrder = new Order("All");
        rootOrder.setCanBeSaved(false);

        ordersTree = new IOrderTree(rootOrder, false, false);
        ordersTree.addTreeSelectionListener(this);
        ordersTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = ordersTree.getClosestRowForLocation(e.getX(), e.getY());
                    ordersTree.setSelectionRow(row);
                    onTreeRightClick(e);
                }
            }
        });

        // Preview
        boolean vertical = settings().getGeneralSettings().getGuiDetailsView() == Statics.GuiDetailsView.VerticalSplit;
        if (vertical) {
            detailPanel = new ItemPreviewPanel(this, this) {
                @Override
                public void onToolBarDelete(IdBToolBar source) {
                    OrderPanelLayout.this.onToolBarDelete(source);
                }

                @Override
                public void onToolBarEdit(IdBToolBar source) {
                    OrderPanelLayout.this.onToolBarEdit(source);
                }
            };
            detailPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, -1, -1, -1),
                    BorderFactory.createLineBorder(Color.lightGray, 1)
            ));
        }

        previewPanel = new OrderPreviewPanel(application, rootOrder);

        // Item table
        tableModel = new IOrderLineTableModel();
        orderLineTable = new ITablePanel<>(tableModel, detailPanel, this, false);
        orderLineTable.setExactColumnWidth(1, 50); // Amount spinner
        orderLineTable.setDbToolBar(this, true, true, false, false);
        orderLineTable.setDbToolBarEnabled(false);
        orderLineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableRowClicked(e);
            }
        });
        orderLineTable.addColumnCellEditor(1, new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int value) {
                onSetOrderItemAmount(orderLineTable.getSelectedItem(), value);
            }
        });

        if (!vertical) {
            detailPanel = new ItemDetailPanel(this);
        }

        // Tool bar
        tbOrderNameLbl = new ILabel();
        Font f = tbOrderNameLbl.getFont();
        tbOrderNameLbl.setFont(new Font(f.getName(), Font.BOLD, 20));

        tbOrderFlowPanel = new IOrderFlowPanel() {
            @Override
            public void moveToOrdered(Order order) {
                onMoveToOrdered(order);
            }

            @Override
            public void moveToReceived(Order order) {
                onMoveToReceived(order);
            }

            @Override
            public void backToOrdered(Order order) {
                onBackToOrdered(order);
            }

            @Override
            public void backToPlanned(Order order) {
                onBackToPlanned(order);
            }
        };

    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Panel them together
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel tablePanel = new JPanel(new BorderLayout());
        JPanel detailPanels = new JPanel(new BorderLayout());
        JPanel westPanel = new JPanel(new BorderLayout());

        tablePanel.add(new JScrollPane(orderLineTable), BorderLayout.CENTER);

        centerPanel.add(tablePanel, BorderLayout.CENTER);

        boolean vertical = settings().getGeneralSettings().getGuiDetailsView() == Statics.GuiDetailsView.VerticalSplit;
        if (!vertical) {
            detailPanels.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 3, 2, 3),
                BorderFactory.createLineBorder(Color.GRAY, 1)
                ));
            centerPanel.add(detailPanel, BorderLayout.SOUTH);
        }

        centerPanel.add(detailPanels, BorderLayout.SOUTH);
        orderLineTable.getTitlePanel().add(createOrderToolbar(), BorderLayout.CENTER);

        JScrollPane pane = new JScrollPane(ordersTree);
        westPanel.add(tbOrderFlowPanel, BorderLayout.PAGE_START);
        westPanel.add(pane, BorderLayout.CENTER);
        westPanel.add(previewPanel, BorderLayout.SOUTH);
        westPanel.setMinimumSize(new Dimension(280, 200));

        // Add
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPanel, centerPanel);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... object) { // Has last selected order
        if (Application.isUpdating(OrderPanelLayout.this)) {
            return;
        }
        Application.beginWait(OrderPanelLayout.this);
        try {
            // Update table if needed
            if (object.length != 0 && object[0] != null) {
                if (selectedOrder == null || !selectedOrder.equals(object[0])) {
                    selectedOrder = (Order) object[0];
                    tableInitialize(selectedOrder);
                }
            }

            ordersTree.expandAll();
            updateToolBar(selectedOrder);

            // Update detail panel
            if (selectedOrderLine != null) {
                detailPanel.updateComponents(selectedOrderLine);
            } else {
                detailPanel.updateComponents();
            }
            updateVisibleComponents();
            updateEnabledComponents();
        } finally {
            Application.endWait(OrderPanelLayout.this);
        }
    }
}
