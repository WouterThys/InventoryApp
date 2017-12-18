package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.tablemodels.ILinkedPcbItemTableModel;
import com.waldo.inventory.gui.components.tablemodels.ILinkedPcbItemTableModel.AmountType;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

class OrderPcbItemPanel extends JPanel implements GuiInterface {

    interface PcbItemListener {
        void onAdd();
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkedPcbItemTableModel linkedPcbItemModel;
    private ITable<PcbItemProjectLink> linkedPcbItemTable;

    private AbstractAction addOneAa;
    private AbstractAction remOneAa;
    private AbstractAction addAllAa;
    private AbstractAction remAllAa;
    private AbstractAction calculateAa;

    private JButton addToOrderBtn;
    private ILabel orderSizeLbl;



    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final PcbItemListener pcbItemListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderPcbItemPanel(PcbItemListener pcbItemListener) {

        this.pcbItemListener = pcbItemListener;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void updateEnabledComponents() {
        PcbItem selectedItem = pcbTableGetSelected().getPcbItem();

        boolean selected = selectedItem != null && !selectedItem.isOrdered();
        int orderSize = orderSize();
        boolean hasOrder = orderSize() > 0;

        addOneAa.setEnabled(selected);
        remOneAa.setEnabled(selected);

        addToOrderBtn.setEnabled(hasOrder);
        orderSizeLbl.setText(String.valueOf(orderSize));
    }

    //
    // Pcb item table
    //
    public void pcbTableInit(ProjectPcb pcb) {
        if (pcb != null) {
            linkedPcbItemModel.setItemList(getLinkedPcbItems(pcb));
            linkedPcbItemTable.setRowSelectionInterval(0,0);
        }
    }

    public void pcbTableUpdate() {
        linkedPcbItemModel.updateTable();
    }

    public PcbItemProjectLink pcbTableGetSelected() {
        return linkedPcbItemTable.getSelectedItem();
    }

    public List<PcbItemProjectLink> pcbTableGetAllSelected() {
        return linkedPcbItemTable.getSelectedItems();
    }

    public List<PcbItemProjectLink> pcbTableGetItemList() {
        return linkedPcbItemModel.getItemList();
    }

    //
    // Actions
    //
    private void onAddOne(PcbItem pcbItem) {
        if (pcbItem != null && !pcbItem.isOrdered()) {
            pcbItem.setOrderAmount(pcbItem.getOrderAmount() + 1);
            pcbTableUpdate();
        }
    }

    private void onRemOne(PcbItem pcbItem) {
        if (pcbItem != null) {
            if (!pcbItem.isOrdered() && pcbItem.getOrderAmount() > 0) {
                pcbItem.setOrderAmount(pcbItem.getOrderAmount() - 1);
                pcbTableUpdate();
            }
        }
    }

    private void onRemAll() {
        for (PcbItemProjectLink link : pcbTableGetItemList()) {
            PcbItem item = link.getPcbItem();
            if (!item.isOrdered()) {
                item.setOrderAmount(0);
            }
        }
        pcbTableUpdate();
    }

    private void onAddAll() {
        for (PcbItemProjectLink link : pcbTableGetItemList()) {
            PcbItem item = link.getPcbItem();
            if (!item.isOrdered()) {
                if (item.getMatchedItemLink().isSetItem()) {
                    item.setOrderAmount(1);
                } else {
                    item.setOrderAmount(link.getNumberOfItems());
                }
            }
        }
        pcbTableUpdate();
    }

    private void onCalculate() {

    }

    private void onAddToOrder() {
        if (pcbItemListener != null) {
            pcbItemListener.onAdd();
        }
    }


    //
    // Methods
    //

    public int orderSize() {
        int size = 0;
        for (PcbItemProjectLink link : pcbTableGetItemList()) {
            PcbItem item = link.getPcbItem();
            if (item.getOrderAmount() > 0 && !item.isOrdered()) {
                size++;
            }
        }
        return size;
    }

    public List<PcbItem> getPcbItemsToOrder() {
        List<PcbItem> toOrder = new ArrayList<>();
        for (PcbItemProjectLink link : pcbTableGetItemList()) {
            PcbItem item = link.getPcbItem();
            if (!item.isOrdered() && item.getOrderAmount() > 0) {
                toOrder.add(item);
            }
        }
        return toOrder;
    }

    void createOrderItems(Order order) {
        if (order != null)  {
            for (PcbItem item : getPcbItemsToOrder()) {
                OrderItem orderItem = new OrderItem(order.getId(), item.getMatchedItemLink().getItemId(), item.getOrderAmount());
                order.addItemToTempList(orderItem);

                item.setOrderItem(orderItem);
            }
        }
        pcbTableUpdate();
        updateEnabledComponents();
    }

    private List<PcbItemProjectLink> getLinkedPcbItems(ProjectPcb pcb) {
        List<PcbItemProjectLink> linkedItems = new ArrayList<>();
        List<Long> containedItems = new ArrayList<>();

        for (PcbItemProjectLink link : pcb.getPcbItemMap()) {
            PcbItem pcbItem = link.getPcbItem();
            if (pcbItem.hasMatchedItem()) {
                if (!containedItems.contains(pcbItem.getMatchedItemLink().getItemId())) {
                    linkedItems.add(link);
                    containedItems.add(pcbItem.getMatchedItemLink().getItemId());
                }
            }
        }

        return linkedItems;
    }

    private JToolBar createPcbToolBar() {
        JToolBar pcbToolBar = new JToolBar(JToolBar.HORIZONTAL);
        pcbToolBar.setFloatable(false);
        pcbToolBar.add(addOneAa);
        pcbToolBar.add(remOneAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(addAllAa);
        pcbToolBar.add(remAllAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(calculateAa);

        return pcbToolBar;
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        linkedPcbItemModel = new ILinkedPcbItemTableModel(AmountType.OrderAmount, false);
        linkedPcbItemTable = new ITable<>(linkedPcbItemModel);
        linkedPcbItemTable.getSelectionModel().addListSelectionListener(e -> updateEnabledComponents());
        TableColumn tableColumn = linkedPcbItemTable.getColumnModel().getColumn(0);
        tableColumn.setCellEditor(new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int value) {
                PcbItemProjectLink link = linkedPcbItemTable.getSelectedItem();
                PcbItem pcbItem = link.getPcbItem();
                if (pcbItem != null) {
                    pcbItem.setOrderAmount(value);
                }
            }
        });
        linkedPcbItemTable.setExactColumnWidth(0, 60);

        // Actions
        addOneAa = new AbstractAction("AddOne", imageResource.readImage("Projects.Order.AddOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddOne(pcbTableGetSelected().getPcbItem());
                updateEnabledComponents();
            }
        };
        remOneAa = new AbstractAction("RemOne", imageResource.readImage("Projects.Order.RemOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemOne(pcbTableGetSelected().getPcbItem());
                updateEnabledComponents();
            }
        };
        addAllAa = new AbstractAction("AddAll", imageResource.readImage("Projects.Order.AddAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddAll();
                updateEnabledComponents();
            }
        };
        remAllAa = new AbstractAction("RemAll", imageResource.readImage("Projects.Order.RemAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemAll();
                updateEnabledComponents();
            }
        };
        calculateAa = new AbstractAction("Calculate", imageResource.readImage("Projects.Order.Calculate")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCalculate();
                updateEnabledComponents();
            }
        };

        addOneAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Add one");
        remOneAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Remove one");
        addAllAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Add all, with total quantity on pcb");
        remAllAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Remove all");
        calculateAa.putValue(AbstractAction.SHORT_DESCRIPTION, "All all, calculate with current stock");

        // Button
        addToOrderBtn = new JButton(imageResource.readImage("Projects.Order.AddToBtn"));
        addToOrderBtn.addActionListener(e -> onAddToOrder());

        // Order size
        orderSizeLbl = new ILabel();


    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Extra
        JScrollPane pane = new JScrollPane(linkedPcbItemTable);
        pane.setPreferredSize(new Dimension(600, 300));
        JPanel addToOrderPnl = new JPanel(new BorderLayout());
        JPanel sizePanel = new JPanel(new BorderLayout());

        sizePanel.add(new ILabel("# items to order: ", ILabel.RIGHT), BorderLayout.WEST);
        sizePanel.add(orderSizeLbl, BorderLayout.CENTER);

        addToOrderPnl.add(addToOrderBtn, BorderLayout.EAST);
        addToOrderPnl.add(sizePanel, BorderLayout.CENTER);

        add(createPcbToolBar(), BorderLayout.PAGE_START);
        add(pane, BorderLayout.CENTER);
        add(addToOrderPnl, BorderLayout.PAGE_END);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            ProjectPcb pcb = (ProjectPcb) args[0];

            pcbTableInit(pcb);
            updateEnabledComponents();
        }
    }
}