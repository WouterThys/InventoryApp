package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.PcbItemItemLink;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.components.popups.PcbItemPopup;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.pcbitemdetails.PcbItemDetailsDialog;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PcbItemSheetTab extends JPanel implements GuiUtils.GuiInterface, ListSelectionListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IPcbItemModel pcbItemTableModel;
    private ITable<PcbItemProjectLink> pcbItemTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Window parent;
    private PcbItemProjectLink pcbItemProjectLink;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public PcbItemSheetTab(Window parent) {
        this.parent = parent;
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

    private void tableSelectItem(int row) {
        if (row >= 0) {
            pcbItemProjectLink = (PcbItemProjectLink) pcbItemTableModel.getValueAt(row, -1);
            pcbItemTable.selectItem(pcbItemProjectLink);
        }
    }

    private void tableUpdate() {
        pcbItemTableModel.updateTable();
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        pcbItemTableModel = new IPcbItemModel();
        pcbItemTable = new ITable<>(pcbItemTableModel);
        pcbItemTable.getSelectionModel().addListSelectionListener(this);
        pcbItemTable.setExactColumnWidth(3, 24);
        pcbItemTable.setExactColumnWidth(4, 24);
        pcbItemTable.setExactColumnWidth(5, 24);

        pcbItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {

                        tableSelectItem(pcbItemTable.rowAtPoint(e.getPoint()));
                        if (pcbItemProjectLink != null) {

                                PcbItemPopup popup = new PcbItemPopup(pcbItemProjectLink) {
                                    @Override
                                    public void onEditItem(PcbItemItemLink itemLink) {
                                        EditItemDialog<Item> dialog = new EditItemDialog<>(parent, "Item", itemLink.getItem());
                                        dialog.showDialog();
                                    }

                                    @Override
                                    public void onPcbItemEdit(PcbItemProjectLink pcbItemProjectLink) {
                                        PcbItemDetailsDialog dialog = new PcbItemDetailsDialog(
                                                parent,
                                                pcbItemProjectLink.getPrettyName(),
                                                pcbItemProjectLink);
                                        dialog.showDialog();
                                        tableUpdate();
                                    }
                                };
                                popup.show(e.getComponent(), e.getX(), e.getY());

                        }

                } else if (e.getClickCount() == 2) {
                    PcbItemDetailsDialog dialog = new PcbItemDetailsDialog(
                            parent,
                            pcbItemProjectLink.getPrettyName(),
                            pcbItemProjectLink);
                    dialog.showDialog();
                    tableUpdate();
                }
            }
        });
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
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            java.util.List<PcbItemProjectLink> components = new ArrayList<>();
            for (Object o : args) {
                components.add((PcbItemProjectLink)o);
            }
            pcbItemTableModel.setItemList(components);
        }
    }

    //
    // Table item selected
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            pcbItemProjectLink = pcbItemTable.getSelectedItem();
        }
    }
}