package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.PcbItem;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.popups.PcbItemPopup;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PcbItemSheetTab extends JPanel implements GuiInterface, ListSelectionListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IPcbItemModel pcbItemTableModel;
    private ITable<PcbItem> pcbItemTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private final IPcbItemModel.PcbItemListener pcbItemListener;

    private PcbItem selectedPcbItem;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public PcbItemSheetTab(Application application, IPcbItemModel.PcbItemListener pcbItemListener) {
        this.application = application;
        this.pcbItemListener = pcbItemListener;
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
            selectedPcbItem = (PcbItem) pcbItemTableModel.getValueAt(row, -1);
            pcbItemTable.selectItem(selectedPcbItem);
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        pcbItemTableModel = new IPcbItemModel(pcbItemListener);
        pcbItemTable = new ITable<>(pcbItemTableModel);
        pcbItemTable.getSelectionModel().addListSelectionListener(this);
        pcbItemTable.setExactColumnWidth(3, 20);
        pcbItemTable.setExactColumnWidth(4, 20);
        pcbItemTable.setExactColumnWidth(5, 20);

        pcbItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {
                    if (pcbItemListener != null) {
                        tableSelectItem(pcbItemTable.rowAtPoint(e.getPoint()));
                        if (selectedPcbItem != null) {
                            PcbItemProjectLink link = pcbItemListener.onGetProjectLink(selectedPcbItem);

                            if (link != null) {
                                PcbItemPopup popup = new PcbItemPopup(link) {
                                    @Override
                                    public void onEditItem(Item item) {
                                        EditItemDialog dialog = new EditItemDialog(application, "Item", item);
                                        dialog.showDialog();
                                    }
                                };
                                popup.show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    }
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
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            java.util.List<PcbItem> components = (java.util.List<PcbItem>) object[0];
            pcbItemTableModel.setItemList(components);
        }
    }

    //
    // Table item selected
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedPcbItem = pcbItemTable.getSelectedItem();
        }
    }
}