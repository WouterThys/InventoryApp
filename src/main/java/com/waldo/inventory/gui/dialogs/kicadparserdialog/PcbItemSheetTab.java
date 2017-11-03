package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class PcbItemSheetTab extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IPcbItemModel pcbItemTableModel;
    private ITable pcbItemTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ListSelectionListener listSelectionListener;
    private IPcbItemModel.PcbItemListener pcbItemListener;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public PcbItemSheetTab(ListSelectionListener listSelectionListener, IPcbItemModel.PcbItemListener pcbItemListener) {
        this.pcbItemListener = pcbItemListener;
        this.listSelectionListener = listSelectionListener;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public IPcbItemModel getTableModel() {
        return pcbItemTableModel;
    }

    public ITable getTable() {
        return pcbItemTable;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        pcbItemTableModel = new IPcbItemModel(pcbItemListener);
        pcbItemTable = new ITable<>(pcbItemTableModel);
        pcbItemTable.getSelectionModel().addListSelectionListener(listSelectionListener);
        pcbItemTable.setDefaultRenderer(ILabel.class, new ITableEditors.PcbItemMatchRenderer());
        pcbItemTable.setExactColumnWidth(0, 30);
        pcbItemTable.setExactColumnWidth(3, 20);
        pcbItemTable.setExactColumnWidth(4, 20);
        pcbItemTable.setExactColumnWidth(5, 20);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        JScrollPane pane = new JScrollPane(pcbItemTable);
        pane.setPreferredSize(new Dimension(600, 400));
        add(pane);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            java.util.List<PcbItem> components = (java.util.List<PcbItem>) object[0];
            pcbItemTableModel.setItemList(components);
        }
    }
}