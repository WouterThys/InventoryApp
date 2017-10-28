package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.tablemodels.ILinkedPcbItemTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.colorResource;
import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OrderPcbItemDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILinkedPcbItemTableModel linkedPcbItemModel;
    private ITable<PcbItem> linkedPcbItemTable;

    private AbstractAction addOneAa;
    private AbstractAction remOneAa;
    private AbstractAction addAllAa;
    private AbstractAction remAllAa;
    private AbstractAction calculateAa;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderPcbItemDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onAddAll();
    abstract void onRemAll();
    abstract void onAddOne(PcbItem pcbItem);
    abstract void onRemOne(PcbItem pcbItem);
    abstract void onCalculate();

    //
    // Pcb item table
    //
    void pcbTableInit(ProjectPcb pcb) {
        if (pcb != null) {
            linkedPcbItemModel.setItemList(getLinkedPcbItems(pcb));
            linkedPcbItemTable.setRowSelectionInterval(0,0);
        }
    }

    void pcbTableUpdate() {
        linkedPcbItemModel.updateTable();
    }

    PcbItem pcbTableGetSelected() {
        return linkedPcbItemTable.getSelectedItem();
    }

    List<PcbItem> pcbTableGetAllSelected() {
        return linkedPcbItemTable.getSelectedItems();
    }

    List<PcbItem> pcbTableGetItemList() {
        return linkedPcbItemModel.getItemList();
    }


    //
    // Order item table
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

    private JToolBar createPcbToolBar() {
        JToolBar pcbToolBar = new JToolBar(JToolBar.HORIZONTAL);
        pcbToolBar.setFloatable(false);
        pcbToolBar.add(addOneAa);
        pcbToolBar.add(remOneAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(addAllAa);
        pcbToolBar.add(remAllAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(calculateAa);

        return pcbToolBar;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setResizable(true);
        setTitleIcon(imageResource.readImage("Projects.Order.Title"));
        setTitleName(getTitle());
        // TODO: subtitle
        getButtonOK().setToolTipText("Order");
        getButtonOK().setEnabled(false);

        // Table
        linkedPcbItemModel = new ILinkedPcbItemTableModel();
        linkedPcbItemTable = new ITable<PcbItem>(linkedPcbItemModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                PcbItem p = (PcbItem) getValueAtRow(row);

                if (!isRowSelected(row)) {
                    component.setBackground(getBackground());
                    if (p.isOrdered()) {
                        component.setBackground(colorResource.readColor("Green.Light"));
                    } else {
                        if (p.getOrderAmount() > 0) {
                            component.setBackground(colorResource.readColor("Blue.Light"));
                        }
                    }
                }

                return component;
            }
        };

        TableColumn tableColumn = linkedPcbItemTable.getColumnModel().getColumn(0);
        tableColumn.setCellEditor(new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int value) {
                PcbItem pcbItem = linkedPcbItemTable.getSelectedItem();
                if (pcbItem != null) {
                    pcbItem.setOrderAmount(value);
                }
            }
        });

        linkedPcbItemTable.getColumnModel().getColumn(0).setMinWidth(60);
        linkedPcbItemTable.getColumnModel().getColumn(0).setMaxWidth(60);

        // Actions
        addOneAa = new AbstractAction("AddOne", imageResource.readImage("Projects.Order.AddOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddOne(pcbTableGetSelected());
            }
        };
        remOneAa = new AbstractAction("RemOne", imageResource.readImage("Projects.Order.RemOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemOne(pcbTableGetSelected());
            }
        };
        addAllAa = new AbstractAction("AddAll", imageResource.readImage("Projects.Order.AddAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddAll();
            }
        };
        remAllAa = new AbstractAction("RemAll", imageResource.readImage("Projects.Order.RemAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemAll();
            }
        };
        calculateAa = new AbstractAction("Calculate", imageResource.readImage("Projects.Order.Calculate")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCalculate();
            }
        };

        addOneAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Add one");
        remOneAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Remove one");
        addAllAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Add all, with total quantity on pcb");
        remAllAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Remove all");
        calculateAa.putValue(AbstractAction.SHORT_DESCRIPTION, "All all, calculate with current stock");

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        // Extra
        JScrollPane pane = new JScrollPane(linkedPcbItemTable);
        pane.setPreferredSize(new Dimension(600, 300));

        getContentPanel().add(createPcbToolBar(), BorderLayout.PAGE_START);
        getContentPanel().add(pane, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            ProjectPcb pcb = (ProjectPcb) args[0];

            pcbTableInit(pcb);
        }
    }


}