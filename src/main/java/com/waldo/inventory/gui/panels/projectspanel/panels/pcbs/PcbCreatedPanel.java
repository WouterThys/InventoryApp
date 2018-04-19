package com.waldo.inventory.gui.panels.projectspanel.panels.pcbs;

import com.waldo.inventory.classes.dbclasses.CreatedPcb;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.components.tablemodels.ICreatedPcbTableModel;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class PcbCreatedPanel extends IPanel implements ListSelectionListener, CacheChangedListener<CreatedPcb> {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ICreatedPcbTableModel tableModel;
    private ITable<CreatedPcb> createdPcbTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ProjectPcb selectedPcb;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public PcbCreatedPanel() {
        super(new BorderLayout());
        initializeComponents();
        initializeLayouts();
    }

    public abstract void createPcb(CreatedPcb pcb);

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateEnabledComponents() {
        //...
    }

    private void tableInitialize(ProjectPcb projectPcb) {
        if (projectPcb != null) {
            tableModel.setItemList(projectPcb.getCreatedPcbs());
        } else {
            tableModel.clearItemList();
        }
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //
    // Table
    //


    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            //...
        }
    }

    //
    // Created pcb
    //
    @Override
    public void onInserted(CreatedPcb createdPcb) {
        if ((selectedPcb != null) && (selectedPcb.getId() == createdPcb.getProjectPcbId())) {

        }
    }

    @Override
    public void onUpdated(CreatedPcb createdPcb) {
        if ((selectedPcb != null) && (selectedPcb.getId() == createdPcb.getProjectPcbId())) {

        }
    }

    @Override
    public void onDeleted(CreatedPcb createdPcb) {
        if ((selectedPcb != null) && (selectedPcb.getId() == createdPcb.getProjectPcbId())) {

        }
    }

    @Override
    public void onCacheCleared() {

    }

    //
    // Gui
    //
    @Override
    public void initializeComponents() {
        tableModel = new ICreatedPcbTableModel();
        createdPcbTable = new ITable<>(tableModel);
        createdPcbTable.getSelectionModel().addListSelectionListener(this);
        createdPcbTable.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    createPcb(createdPcbTable.getSelectedItem());
                }
            }
        });
    }

    @Override
    public void initializeLayouts() {
        // Toolar?

        JScrollPane scrollPane = new JScrollPane(createdPcbTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            ProjectPcb pcb = (ProjectPcb) args[0];
            if (selectedPcb == null || !selectedPcb.equals(pcb)) {
                selectedPcb = pcb;
                tableInitialize(selectedPcb);
            }
        } else {
            selectedPcb = null;
            tableInitialize(null);
        }
        updateEnabledComponents();
    }
}