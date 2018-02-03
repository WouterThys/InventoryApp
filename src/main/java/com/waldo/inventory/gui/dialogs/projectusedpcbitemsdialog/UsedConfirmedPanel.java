package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemUsedTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

class UsedConfirmedPanel extends JPanel implements GuiUtils.GuiInterface {

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
    private final UsedListener usedListener;

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
        PcbItemProjectLink link = usedTableGetSelectedItem();
        boolean selected = link != null;
        boolean hasItemsToProcess = getItemsToProcess().size() > 0;

        addOneAa.setEnabled(selected && !link.isProcessed());
        remOneAa.setEnabled(selected && !link.isProcessed());

        refreshAa.setEnabled(hasItemsToProcess);
        remAllAa.setEnabled(hasItemsToProcess);
        doSetUsedBtn.setEnabled(hasItemsToProcess);
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
        if (link != null && !link.isProcessed()) {
            link.setUsedCount(link.getUsedCount() + 1);
        }
    }

    private void onRemOne(PcbItemProjectLink link) {
        if (link != null) {
            if (link.getUsedCount() > 0 && !link.isProcessed()) {
                link.setUsedCount(link.getUsedCount() - 1);
            }
        }
    }

    private void onRemAll() {
        for (PcbItemProjectLink link : usedTableModel.getItemList()) {
            if (!link.isProcessed()) {
                link.setUsedCount(0);
            }
        }
    }

    private void onRefresh() {
        List<PcbItemProjectLink> linkList = new ArrayList<>(usedTableGetItemList());
        for (PcbItemProjectLink link : linkList) {
            if (!link.isProcessed()) {
                if (link.isUsed() && link.getUsedCount() == 0) {
                    link.setUsed(false);
                    link.setUsedCount(0);
                    usedTableModel.removeItem(link);
                }
            }
        }
    }

    List<PcbItemProjectLink> getItemsToProcess() {
        List<PcbItemProjectLink> toProcess = new ArrayList<>();
        for (PcbItemProjectLink link : usedTableGetItemList()) {
            if (!link.isProcessed() && link.getUsedCount() > 0) {
                toProcess.add(link);
            }
        }
        return toProcess;
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
        updateEnabledComponents();
    }

    List<PcbItemProjectLink> usedTableGetItemList() {
        return usedTableModel.getItemList();
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
        usedTable = new ITable<PcbItemProjectLink>(usedTableModel){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component =  super.prepareRenderer(renderer, row, column);
                PcbItemProjectLink link = getValueAtRow(row);

                if (link != null) {
                    if (link.isProcessed()) {
                        component.setForeground(Color.gray);
                    } else {
                        component.setForeground(Color.black);
                    }
                }

                return component;
            }
        };
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