package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.ILinkedPcbItemTableModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class UsedPcbItemPanel extends JPanel implements GuiInterface {

    interface PcbItemListener {
        void onAdd();
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkedPcbItemTableModel linkedPcbItemModel;
    private ITable<PcbItem> linkedPcbItemTable;

    private AbstractAction addOneAa;
    private AbstractAction remOneAa;
    private AbstractAction addAllAa;
    private AbstractAction remAllAa;

    private JButton addToUsedBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private PcbItemListener pcbItemListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public UsedPcbItemPanel(PcbItemListener pcbItemListener) {

        this.pcbItemListener = pcbItemListener;

        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void updateEnabledComponents() {
        PcbItem selectedItem = pcbTableGetSelected();
        //boolean selected = selectedItem != null &&
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

    public PcbItem pcbTableGetSelected() {
        return linkedPcbItemTable.getSelectedItem();
    }

    public List<PcbItem> pcbTableGetAllSelected() {
        return linkedPcbItemTable.getSelectedItems();
    }

    public List<PcbItem> pcbTableGetItemList() {
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
        for (PcbItem item : pcbTableGetItemList()) {
            if (!item.isOrdered()) {
                item.setOrderAmount(0);
            }
        }
        pcbTableUpdate();
    }

    private void onAddAll() {
        for (PcbItem item : pcbTableGetItemList()) {
            if (!item.isOrdered()) {
                if (item.getMatchedItemLink().isSetItem()) {
                    item.setOrderAmount(1);
                } else {
                    item.setOrderAmount(item.getReferences().size());
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
    private List<PcbItem> getLinkedPcbItems(ProjectPcb pcb) {
        List<PcbItem> linkedItems = new ArrayList<>();
        List<Long> containedItems = new ArrayList<>();

        for (String sheet : pcb.getPcbItemMap().keySet()) {
            for (PcbItem pcbItem : pcb.getPcbItemMap().get(sheet)) {
                if (pcbItem.hasMatch()) {
                    if (!containedItems.contains(pcbItem.getMatchedItemLink().getItemId())) {
                        linkedItems.add(pcbItem);
                        containedItems.add(pcbItem.getMatchedItemLink().getItemId());
                    }
                }
            }
        }

        return linkedItems;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

    }

    @Override
    public void updateComponents(Object... args) {

    }
}