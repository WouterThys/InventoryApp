package com.waldo.inventory.gui.dialogs.projectusedpcbitemsdialog;

import com.waldo.inventory.classes.PcbItem;
import com.waldo.inventory.classes.PcbItemProjectLink;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITableEditors;
import com.waldo.inventory.gui.components.tablemodels.ILinkedPcbItemTableModel;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.gui.Application.colorResource;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.tablemodels.ILinkedPcbItemTableModel.AmountType;
import static com.waldo.inventory.gui.components.tablemodels.ILinkedPcbItemTableModel.PcbItemTableModelListener;

class UsedPcbItemPanel extends JPanel implements GuiInterface, PcbItemTableModelListener {

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
    private ILabel usedSizeLbl;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private PcbItemListener pcbItemListener;
    private HashMap<Long, PcbItemProjectLink> linkMap;

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
        PcbItemProjectLink link = getLink(selectedItem);

        boolean selected = selectedItem != null && link != null && !link.isUsed();
        int usedSize = usedSize();
        boolean hasUsed = usedSize > 0;

        addOneAa.setEnabled(selected);
        remOneAa.setEnabled(selected);

        addToUsedBtn.setEnabled(hasUsed);
        usedSizeLbl.setText(String.valueOf(usedSize));
    }

    //
    // Pcb item table
    //
    void pcbTableInit(List<PcbItem> pcbItemList) {
        if (pcbItemList != null) {
            linkedPcbItemModel.setItemList(pcbItemList);
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

    PcbItemProjectLink getLink(PcbItem pcbItem) {
        if (pcbItem != null && linkMap != null) {
            return linkMap.get(pcbItem.getId());
        }
        return null;
    }

    List<PcbItemProjectLink> getAllLinks() {
        return new ArrayList<>(linkMap.values());
    }

    //
    // Actions
    //
    private void onAddOne(PcbItem pcbItem) {
        if (pcbItem != null) {
            PcbItemProjectLink link = getLink(pcbItem);
            if (link != null) {
                link.setUsedCount(link.getUsedCount() + 1);
                pcbTableUpdate();
            }
        }
    }

    private void onRemOne(PcbItem pcbItem) {
        if (pcbItem != null) {
            PcbItemProjectLink link = getLink(pcbItem);
            if (link != null && link.getUsedCount() > 0) {
                link.setUsedCount(link.getUsedCount() - 1);
                pcbTableUpdate();
            }
        }
    }

    private void onRemAll() {
        for (PcbItem item : pcbTableGetItemList()) {
            PcbItemProjectLink link = getLink(item);
            if (link != null && !link.isUsed()) {
                link.setUsedCount(0);
                pcbTableUpdate();
            }
        }
        pcbTableUpdate();
    }

    private void onAddAll() {
        for (PcbItem item : pcbTableGetItemList()) {
            PcbItemProjectLink link = getLink(item);
            if (link != null && !link.isUsed()) {
                link.setUsedCount(item.getReferences().size());
            }
        }
        pcbTableUpdate();
    }

    private void onAddToUsed() {
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

    private int usedSize() {
        int size = 0;
        for (PcbItemProjectLink link : linkMap.values()) {
            if (link.getUsedCount() > 0 && !link.isUsed()) {
                size++;
            }
        }
        return size;
    }

    void createUsedLinks() {
        for (PcbItemProjectLink link : linkMap.values()) {
            if (link.getUsedCount() > 0 && !link.isUsed()) {
                link.setUsed(true);
            }
        }
        pcbTableUpdate();
        updateEnabledComponents();
    }

    private HashMap<Long, PcbItemProjectLink> createLinkMap(ProjectPcb pcb, List<PcbItem> linkedItems) {
        HashMap<Long, PcbItemProjectLink> map = new HashMap<>();

        for (PcbItem pcbItem : linkedItems) {
            PcbItemProjectLink link = SearchManager.sm().findPcbItemProjectLink(pcb.getId(), pcbItem.getId());
            if (link != null) {
                map.put(pcbItem.getId(), link);
            }
        }

        return map;
    }

    private JToolBar createPcbToolBar() {
        JToolBar pcbToolBar = new JToolBar(JToolBar.HORIZONTAL);
        pcbToolBar.setFloatable(false);
        pcbToolBar.add(addOneAa);
        pcbToolBar.add(remOneAa);
        pcbToolBar.addSeparator();
        pcbToolBar.add(addAllAa);
        pcbToolBar.add(remAllAa);
        return pcbToolBar;
    }

    @Override
    public PcbItemProjectLink onGetLink(PcbItem pcbItem) {
        return getLink(pcbItem);
    }

    /*
         *                  LISTENERS
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Table
        linkedPcbItemModel = new ILinkedPcbItemTableModel(AmountType.UsedAmount, this);
        linkedPcbItemTable = new ITable<PcbItem>(linkedPcbItemModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component =  super.prepareRenderer(renderer, row, column);
                PcbItem p = getValueAtRow(row);
                PcbItemProjectLink link = getLink(p);

                if (link != null) {
                    if (!isRowSelected(row)) {
                        component.setBackground(getBackground());
                        if (link.isUsed()) {
                            component.setBackground(colorResource.readColor("Green.Light"));
                        } else {
                            if (link.getUsedCount() > 0) {
                                component.setBackground(colorResource.readColor("Blue.Light"));
                            }
                        }
                    }

                    if (link.isUsed()) {
                        component.setForeground(Color.gray);
                    } else {
                        component.setForeground(Color.black);
                    }
                }

                return component;
            }

            @Override
            public String getToolTipText(MouseEvent event) {
                String tip = null;
                java.awt.Point p = event.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);

                if (realColumnIndex == 1) { //Sport column
                    tip = getValueAtRow(rowIndex).getReferenceString();
                }
                return tip;
            }
        };
        linkedPcbItemTable.getSelectionModel().addListSelectionListener(e -> updateEnabledComponents());
        TableColumn tableColumn = linkedPcbItemTable.getColumnModel().getColumn(0);
        tableColumn.setCellEditor(new ITableEditors.SpinnerEditor() {
            @Override
            public void onValueSet(int value) {
                PcbItem pcbItem = linkedPcbItemTable.getSelectedItem();
                if (pcbItem != null) {
                    PcbItemProjectLink link = getLink(pcbItem);
                    if (link != null) {
                        link.setUsedCount(value);
                    }
                }
            }
        });
        linkedPcbItemTable.setExactColumnWidth(0, 60);

        // Actions
        addOneAa = new AbstractAction("AddOne", imageResource.readImage("Projects.Order.AddOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddOne(pcbTableGetSelected());
                updateEnabledComponents();
            }
        };
        remOneAa = new AbstractAction("RemOne", imageResource.readImage("Projects.Order.RemOne")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemOne(pcbTableGetSelected());
                updateEnabledComponents();
            }
        };
        addAllAa = new AbstractAction("AddAll", imageResource.readImage("Projects.Order.AddAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddAll();
                updateEnabledComponents();
            }
        };
        remAllAa = new AbstractAction("RemAll", imageResource.readImage("Projects.Order.RemAll")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemAll();
                updateEnabledComponents();
            }
        };


        addOneAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Add one");
        remOneAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Remove one");
        addAllAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Add all, with total quantity on pcb");
        remAllAa.putValue(AbstractAction.SHORT_DESCRIPTION, "Remove all");

        // Button
        addToUsedBtn = new JButton(imageResource.readImage("Projects.Used.AddToUsed"));
        addToUsedBtn.addActionListener(e -> onAddToUsed());

        // Used size
        usedSizeLbl = new ILabel();
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        // Extra
        JScrollPane pane = new JScrollPane(linkedPcbItemTable);
        pane.setPreferredSize(new Dimension(600, 300));
        JPanel addToUsedPnl = new JPanel(new BorderLayout());
        JPanel sizePanel = new JPanel(new BorderLayout());

        sizePanel.add(new ILabel("# items too used: ", ILabel.RIGHT), BorderLayout.WEST);
        sizePanel.add(usedSizeLbl, BorderLayout.CENTER);

        addToUsedPnl.add(addToUsedBtn, BorderLayout.EAST);
        addToUsedPnl.add(sizePanel, BorderLayout.CENTER);

        add(createPcbToolBar(), BorderLayout.PAGE_START);
        add(pane, BorderLayout.CENTER);
        add(addToUsedPnl, BorderLayout.PAGE_END);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            ProjectPcb pcb = (ProjectPcb) args[0];

            List<PcbItem> linkedItems = getLinkedPcbItems(pcb);
            linkMap = createLinkMap(pcb, linkedItems);
            pcbTableInit(linkedItems);
            updateEnabledComponents();
        }
    }
}