package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.classes.PcbItemProjectLink;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemUsedTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

class UsedConfirmedPanel extends JPanel implements GuiInterface {

    interface UsedListener {
        void onSetUsed();
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IPcbItemUsedTableModel usedTableModel;
    private ITable<PcbItemProjectLink> usedTable;

    private JButton doSetUsedBtn;

    private AbstractAction addOneAa;
    private AbstractAction remOneAa;
    private AbstractAction remAllAa;
    private AbstractAction refreshAa;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private UsedListener usedListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    UsedConfirmedPanel(UsedListener usedListener) {

        this.usedListener = usedListener;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean selected = usedTableGetSelectedItem() != null;

        addOneAa.setEnabled(selected);
        remOneAa.setEnabled(selected);
    }

    void usedTableUpdate() {
        usedTableModel.updateTable();
    }

    PcbItemProjectLink usedTableGetSelectedItem() {
        return usedTable.getSelectedItem();
    }

    //
    // Actions
    //
    private void onAddOne(PcbItemProjectLink link) {
        if (link != null) {
            link.setUsedCount(link.getUsedCount() + 1);
        }
    }

    private void onRemOne(PcbItemProjectLink link) {
        if (link != null) {
            if (link.getUsedCount() > 0) {
                link.setUsedCount(link.getUsedCount() - 1);
            }
        }
    }

    private void onRemAll() {
        for (PcbItemProjectLink link : usedTableModel.getItemList()) {
            link.setUsedCount(0);
        }
    }

    private void onRefresh() {
//        List<PcbItemProjectLink> orders = new ArrayList<>(orderList);
//        for (PcbItemProjectLink order : orders) {
//            // Check items
//            List<PcbItemProjectLink> itemList = new ArrayList<>(order.getTempOrderItems());
//            for (PcbItemProjectLink item : itemList ) {
//                if (item.getId() < DbObject.UNKNOWN_ID && item.getAmount() == 0) {
//                    order.removeItemFromList(item);
//                }
//            }
//
//            // Check order
//            if (order.getTempOrderItems().size() == 0) {
//                orderList.remove(order);
//            }
//        }
    }

    //
    // Table
    //
    void usedTableInit(List<PcbItemProjectLink> linkList) {
        List<PcbItemProjectLink> filtered = new ArrayList<>();
        for (PcbItemProjectLink link : linkList) {
            if (link.isUsed()) {
                filtered.add(link);
            }
        }
        usedTableModel.setItemList(filtered);
    }

    //
    // Methods
    //
    private JToolBar createOrderToolBar() {
        JToolBar pcbToolBar = new JToolBar(JToolBar.HORIZONTAL);
        pcbToolBar.setFloatable(false);
        pcbToolBar.add(addOneAa);
        pcbToolBar.add(remOneAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(remAllAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(refreshAa);

        return pcbToolBar;
    }

    private void onSetUsed() {
        if (usedListener != null) {
            usedListener.onSetUsed();
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
// Actions
        addOneAa = new AbstractAction("AddOne", imageResource.readImage("Projects.Order.AddOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddOne(usedTable.getSelectedItem());
                usedTableUpdate();
                updateEnabledComponents();
            }
        };
        remOneAa = new AbstractAction("RemOne", imageResource.readImage("Projects.Order.RemOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemOne(usedTable.getSelectedItem());
                usedTableUpdate();
                updateEnabledComponents();
            }
        };
        remAllAa = new AbstractAction("RemAll", imageResource.readImage("Projects.Order.RemAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemAll();
                usedTableUpdate();
                updateEnabledComponents();
            }
        };
        refreshAa = new AbstractAction("Refresh", imageResource.readImage("Projects.Order.Refresh")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRefresh();
                usedTableUpdate();
                updateEnabledComponents();
            }
        };

        // Table
        usedTableModel = new IPcbItemUsedTableModel();
        usedTable = new ITable<>(usedTableModel);
        usedTable.getSelectionModel().addListSelectionListener(e -> updateEnabledComponents());

        TableColumn tableColumn = usedTable.getColumnModel().getColumn(2);
        tableColumn.setCellEditor(new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int value) {
                PcbItemProjectLink link = usedTable.getSelectedItem();
                if (link != null) {
                    link.setUsedCount(value);
                }
            }
        });
        usedTable.setExactColumnWidth(2, 60);

        // Button
        doSetUsedBtn = new JButton(imageResource.readImage("Projects.Used.DoSetUsed"));
        doSetUsedBtn.addActionListener(e -> onSetUsed());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Extra
        JScrollPane pane = new JScrollPane(usedTable);
        pane.setPreferredSize(new Dimension(600, 300));
        JPanel removeOrderPnl = new JPanel(new BorderLayout());
        removeOrderPnl.add(doSetUsedBtn, BorderLayout.EAST);

        add(createOrderToolBar(), BorderLayout.PAGE_START);
        add(pane, BorderLayout.CENTER);
        add(removeOrderPnl, BorderLayout.PAGE_END);
    }

    @Override
    public void updateComponents(Object... args) {
        updateEnabledComponents();
    }
}