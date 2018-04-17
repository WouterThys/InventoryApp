package com.waldo.inventory.gui.dialogs.editdistributororderflowdialog;

import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.DistributorOrderFlow;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.components.IdBToolBar;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

import static com.waldo.inventory.managers.CacheManager.cache;

public class EditDistributorOrderflowDialog extends EditDistributorOrderflowDialogLayout
        implements CacheChangedListener<DistributorOrderFlow> {


    public EditDistributorOrderflowDialog(Window window, Distributor distributor) {
        super(window);

        this.distributor = distributor;

        cache().addListener(DistributorOrderFlow.class,this);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    //
    // Table
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedOrderFlow = orderFlowTable.getSelectedItem();

            updateEnabledComponents();
        }
    }

    //
    // Cache listener
    //
    @Override
    public void onInserted(DistributorOrderFlow flow) {
        tableAddFlow(flow);
        orderFlowTable.selectItem(flow);
    }

    @Override
    public void onUpdated(DistributorOrderFlow flow) {
        orderFlowTable.selectItem(flow);
    }

    @Override
    public void onDeleted(DistributorOrderFlow flow) {
        tableDeleteFlow(flow);
        selectedOrderFlow = null;
        updateEnabledComponents();
    }

    @Override
    public void onCacheCleared() {
        // Don't care
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        tableInitialize(distributor.getOrderFlowTemplate());
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        int seqNum = 1;
        if (selectedOrderFlow != null) {
            seqNum = selectedOrderFlow.getSequenceNumber() + 1;
        }
        DistributorOrderFlow flow = new DistributorOrderFlow(distributor, seqNum);
        EditFlowDialog dialog = new EditFlowDialog(this, flow);
        dialog.showDialog();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedOrderFlow != null) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete " + selectedOrderFlow.toString() + "?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                selectedOrderFlow.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedOrderFlow != null) {
            EditFlowDialog dialog = new EditFlowDialog(this, selectedOrderFlow);
            dialog.showDialog();
        }
    }
}