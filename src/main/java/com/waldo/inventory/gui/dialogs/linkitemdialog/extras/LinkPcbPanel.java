package com.waldo.inventory.gui.dialogs.linkitemdialog.extras;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.tablemodels.ILinkKiCadTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LinkPcbPanel extends JPanel implements GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkKiCadTableModel tableModel;
    private ITable itemTable;

    private ITextField referencesTf;
    private ITextField footprintTf;
    private int type;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LinkPcbPanel(Application application, int type) {
        this.type = type;

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

    public void setItemList(java.util.List<PcbItem> componentList) {
        tableModel.setItemList(componentList);
    }

    public void updateSelectedValueData(PcbItem component) {
        if (component != null) {
            referencesTf.setText(component.getReferenceString());
            footprintTf.setText(component.getFootprint());
        } else {
            referencesTf.clearText();
            footprintTf.clearText();
        }
    }

    public PcbItem getSelectedComponent() {
        int row = itemTable.getSelectedRow();
        if (row >=0) {
            return (PcbItem) itemTable.getValueAtRow(row);
        }
        return null;
    }

    public void updateTable() {
        tableModel.updateTable();
    }

    public void updateTable(HashMap<String, List<PcbItem>> pcbItems) {
        List<PcbItem> pcbItemsList = new ArrayList<>();
        for (String sheet : pcbItems.keySet()) {
            pcbItemsList.addAll(pcbItems.get(sheet));
        }
        tableModel.setItemList(pcbItemsList);
    }

    public java.util.List<PcbItem> getKcComponentList() {
        return tableModel.getItemList();
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new GridBagLayout());

        PanelUtils.GridBagHelper gbh = new PanelUtils.GridBagHelper(southPanel);
        gbh.addLine("References: ", referencesTf);
        gbh.addLine("Footprint: ", footprintTf);

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
        tableModel = new ILinkKiCadTableModel(type);
        itemTable = new ITable<>(tableModel);
        itemTable.setDefaultRenderer(ILabel.class, new ITableEditors.PcbItemMatchRenderer());
        itemTable.getColumnModel().getColumn(0).setMaxWidth(30);
        itemTable.getColumnModel().getColumn(3).setMaxWidth(30);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Text fields
        referencesTf = new ITextField();
        footprintTf = new ITextField();

        referencesTf.setEnabled(false);
        footprintTf.setEnabled(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Extras
        JScrollPane pane = new JScrollPane(itemTable);
        pane.setPreferredSize(new Dimension(400, 300));

        // Add
        add(pane, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            ProjectPcb projectPcb = (ProjectPcb) object;
            updateTable(projectPcb.getPcbItemMap());
        } else {
            tableModel.clearItemList();
        }
    }
}