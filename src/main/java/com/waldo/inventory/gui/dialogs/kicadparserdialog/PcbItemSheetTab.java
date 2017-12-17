package com.waldo.inventory.gui.dialogs.kicadparserdialog;

import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.tablemodels.IPcbItemModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PcbItemSheetTab extends JPanel implements GuiInterface, ListSelectionListener {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IPcbItemModel pcbItemTableModel;
    private ITable<PcbItemProjectLink> pcbItemTable;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final Application application;
    private PcbItemProjectLink selectedPcbItem;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public PcbItemSheetTab(Application application) {
        this.application = application;
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
            selectedPcbItem = (PcbItemProjectLink) pcbItemTableModel.getValueAt(row, -1);
            pcbItemTable.selectItem(selectedPcbItem);
        }
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
        pcbItemTable.setExactColumnWidth(3, 20);
        pcbItemTable.setExactColumnWidth(4, 20);
        pcbItemTable.setExactColumnWidth(5, 20);

        pcbItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {
                    //TODO#24
//                    if (pcbItemListener != null) {
//                        tableSelectItem(pcbItemTable.rowAtPoint(e.getPoint()));
//                        if (selectedPcbItem != null) {
//                            PcbItemProjectLink link = pcbItemListener.onGetProjectLink(selectedPcbItem);
//
//                            if (link != null) {
//                                PcbItemPopup popup = new PcbItemPopup(link) {
//                                    @Override
//                                    public void onEditItem(Item item) {
//                                        EditItemDialog dialog = new EditItemDialog(application, "Item", item);
//                                        dialog.showDialog();
//                                    }
//
//                                    @Override
//                                    public void onPcbItemEdit(PcbItemProjectLink pcbItemProjectLink) {
//                                        PcbItemDetailsDialog dialog = new PcbItemDetailsDialog(application, link.getPcbItem().getPrettyName(), link);
//                                        dialog.showDialog();
//                                    }
//                                };
//                                popup.show(e.getComponent(), e.getX(), e.getY());
//                            }
//                        }
//                    }
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
            selectedPcbItem = pcbItemTable.getSelectedItem();
        }
    }
}