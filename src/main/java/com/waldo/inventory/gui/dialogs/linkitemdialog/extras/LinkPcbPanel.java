package com.waldo.inventory.gui.dialogs.linkitemdialog.extras;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.classes.dbclasses.ProjectPcb;
import com.waldo.inventory.gui.components.tablemodels.ILinkPcbItemTableModel;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LinkPcbPanel extends JPanel implements GuiUtils.GuiInterface {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkPcbItemTableModel tableModel;
    private ITable itemTable;

    private ITextField referencesTf;
    private ITextField footprintTf;
    private final int type;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public LinkPcbPanel(int type) {
        this.type = type;

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void addListSelectionListener(ListSelectionListener listSelectionListener) {
        itemTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    public void setItemList(java.util.List<PcbItem> componentList) {
        if (componentList != null) {
            tableModel.setItemList(componentList);
        }
    }

    public void updateSelectedValueData(PcbItem component) {
        if (component != null) {
            //TODO#24 referencesTf.setText(component.getReferenceString());
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

    public void updateTable(List<PcbItemProjectLink> pcbItemProjectLinks) {
        List<PcbItem> pcbItemsList = new ArrayList<>();
        for (PcbItemProjectLink link : pcbItemProjectLinks) {
            pcbItemsList.add(link.getPcbItem());
        }
        tableModel.setItemList(pcbItemsList);
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new GridBagLayout());

        GuiUtils.GridBagHelper gbh = new GuiUtils.GridBagHelper(southPanel);
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
        tableModel = new ILinkPcbItemTableModel(type);
        itemTable = new ITable<>(tableModel);
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
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            ProjectPcb projectPcb = (ProjectPcb) object[0];
            updateTable(projectPcb.getPcbItemList());
        } else {
            tableModel.clearItemList();
        }
    }
}