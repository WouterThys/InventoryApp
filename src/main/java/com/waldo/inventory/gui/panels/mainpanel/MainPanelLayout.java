package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDivisionTree;
import com.waldo.inventory.gui.components.ISetTree;
import com.waldo.inventory.gui.components.ITablePanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.popups.TableOptionsPopup;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.panels.mainpanel.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.gui.panels.mainpanel.itemlisteners.ItemDetailListener;
import com.waldo.inventory.gui.panels.mainpanel.itempreviewpanel.ItemPreviewPanel;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.settings.SettingsManager.settings;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class MainPanelLayout extends JPanel implements
        GuiUtils.GuiInterface,
        TreeSelectionListener,
        ListSelectionListener,
        IdBToolBar.IdbToolBarListener,
        ItemDetailListener,
        TableOptionsPopup.TableOptionsListener {

    private static final String TREE_ITEMS = "Items ";
    private static final String TREE_SETS = "Sets ";

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITablePanel<Item> itemTable;
    IItemTableModel tableModel;

    IDivisionTree divisionTree;
    IdBToolBar divisionTb;

    ISetTree setTree;
    IdBToolBar setsTb;

    AbstractDetailPanel detailPanel;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final Application application;

    Item selectedItem;
    Set selectedSet;
    Division selectedDivision;
    private String selectedTreeTab = TREE_ITEMS;

    boolean showSets = true;
    boolean showSetItems = false;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    MainPanelLayout(Application application) {
        this.application = application;
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean enabled =  !(selectedItem == null || selectedItem.isUnknown() || !selectedItem.canBeSaved());
        itemTable.setDbToolBarEditDeleteEnabled(enabled);

        // Divisions
        enabled = selectedDivision != null && selectedDivision.canBeSaved();
        divisionTb.setEditActionEnabled(enabled);
        divisionTb.setDeleteActionEnabled(enabled);

        // Sets
        enabled = selectedSet != null && selectedSet.canBeSaved();
        setsTb.setEditActionEnabled(enabled);
        setsTb.setDeleteActionEnabled(enabled);
    }

    void updateDetails() {
        detailPanel.updateComponents(selectedItem);
    }

    abstract void onTreeRightClick(MouseEvent e);
    abstract void onTableRowClicked(MouseEvent e);

    //
    // Table stuff
    //

    void setItemTableList(List<Item> itemList) {
        tableModel.setItemList(filterItems(itemList, showSets, showSetItems));
    }

    void setItemTableList(List<Item> itemList, boolean showSets, boolean showSetItems) {
        tableModel.setItemList(filterItems(itemList, showSets, showSetItems));
    }

    private List<Item> filterItems(List<Item> itemList, boolean showSets, boolean showSetItems) {
        List<Item> filtered = new ArrayList<>();
        if (itemList != null && itemList.size() > 0) {
            if (showSets && showSetItems) { // Both true
                filtered = new ArrayList<>(itemList);
            } else if (!showSets && !showSetItems){ // Both false
                for (Item item : itemList) {
                    if (!item.isSet() && !item.isSetItem()) {
                        filtered.add(item);
                    }
                }
            } else { // One is true
                if (!showSets) {
                    for (Item item : itemList) {
                        if (!item.isSet()) {
                            filtered.add(item);
                        }
                    }
                }
                if (!showSetItems) {
                    for (Item item : itemList) {
                        if (!item.isSetItem()) {
                            filtered.add(item);
                        }
                    }
                }
            }
        }
        return filtered;
    }

    long tableUpdate() {
        long itemId = -1;
        if (selectedItem != null) {
            itemId = selectedItem.getId();
        }
        tableModel.updateTable();
        return itemId;
    }

    void tableRemoveItem(Item item) {
        tableModel.removeItem(item);
    }

    void tableAddItem(Item item) {
        tableModel.addItem(item);
    }

    void tableSelectItem(Item item) {
        this.selectedItem = item;
        itemTable.selectItem(item);
    }

    void tableSelectItem(int row) {
        if (row >= 0) {
            this.selectedItem = (Item) tableModel.getValueAt(row, -1);
            itemTable.selectItem(selectedItem);
        }
    }

    //
    // Tree stuff
    //
    void treeSelectDivisionForItem(Item item) {
        if (item != null) {
            divisionTree.setSelectedDivision(item.getDivision());
        }
    }

    void treeSelectDivision(Division division) {
        divisionTree.setSelectedDivision(division);
    }

    // Other

    boolean setsSelected() {
        return (selectedTreeTab.equals(TREE_SETS));
    }

    private JPanel createItemDivisionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addChangeListener(e -> {
            if (!Application.isUpdating(application)) {
                int ndx = tabbedPane.getSelectedIndex();
                if (ndx == 1) selectedTreeTab = TREE_SETS;
                else selectedTreeTab = TREE_ITEMS;
            }
        });

        JPanel divisionPanel = new JPanel(new BorderLayout());
        JScrollPane pane = new JScrollPane(divisionTree);
        pane.setPreferredSize(new Dimension(300, 400));
        divisionPanel.add(pane, BorderLayout.CENTER);
        divisionPanel.add(divisionTb, BorderLayout.PAGE_END);
        divisionPanel.setMinimumSize(new Dimension(200,200));

        JPanel setPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(setTree);
        pane.setPreferredSize(new Dimension(300, 400));
        setPanel.add(scrollPane, BorderLayout.CENTER);
        setPanel.add(setsTb, BorderLayout.PAGE_END);
        setPanel.setMinimumSize(new Dimension(200, 200));

        tabbedPane.addTab(TREE_ITEMS, imageResource.readIcon("Items.Tree.Item"), divisionPanel);
        tabbedPane.addTab(TREE_SETS, imageResource.readIcon("Items.Tree.Set"), setPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Divisions
        List<Division> rootDivisions = SearchManager.sm().findDivisionsWithoutParent();
        rootDivisions.sort(new ComparatorUtils.DbObjectNameComparator<>());
        Division rootDivision = Division.createDummyDivision("Dummy", rootDivisions);

        divisionTree = new IDivisionTree(rootDivision, false);
        divisionTree.addTreeSelectionListener(this);
        divisionTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = divisionTree.getClosestRowForLocation(e.getX(), e.getY());
                    divisionTree.setSelectionRow(row);
                    onTreeRightClick(e);
                }
            }
        });
        divisionTb = new IdBToolBar(this);

        // Sets
        List<Set> sets = cache().getSets();
        sets.sort(new ComparatorUtils.DbObjectNameComparator<>());
        Set rootSet = Set.createDummySet("Dummy", sets);
        setTree = new ISetTree(rootSet, false);
        setTree.addTreeSelectionListener(this);
        setTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = setTree.getClosestRowForLocation(e.getX(), e.getY());
                    setTree.setSelectionRow(row);
                    onTreeRightClick(e);
                }
            }
        });
        setsTb = new IdBToolBar(this);

        // Preview
        boolean vertical = settings().getGeneralSettings().getGuiDetailsView() == Statics.GuiDetailsView.VerticalSplit;
        if (vertical) {
            detailPanel = new ItemPreviewPanel(this, null) {
                @Override
                public void onToolBarDelete(IdBToolBar source) {
                    MainPanelLayout.this.onToolBarDelete(source);
                }

                @Override
                public void onToolBarEdit(IdBToolBar source) {
                    //ResistorDialog dialog = new ResistorDialog(application, "resistor");
                    //dialog.showDialog();
                    MainPanelLayout.this.onToolBarEdit(source);
                }
            };
            detailPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, -1, -1, -1),
                    BorderFactory.createLineBorder(Color.lightGray, 1)
            ));
        }

        // Items
        tableModel = new IItemTableModel();
        itemTable = new ITablePanel<>(tableModel, detailPanel, this, true);
        itemTable.setDbToolBar(this, true, true, false, false);
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableRowClicked(e);
            }
        });
        itemTable.addSortOption(new ComparatorUtils.ItemDivisionComparator(), null);
        itemTable.addSortOption(new ComparatorUtils.DbObjectNameComparator(), null);
        itemTable.addSortOption(new ComparatorUtils.ItemManufacturerComparator(), null);
        itemTable.addSortOption(new ComparatorUtils.ItemLocationComparator(), null);
        itemTable.addTableOptionsListener(this);
        setItemTableList(rootDivision.getItemList());

        // Details
        if (!vertical) {
            detailPanel = new ItemDetailPanel(this);
        }
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel divisionPanel = createItemDivisionPanel();

        // Panel them together
        centerPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        boolean vertical = settings().getGeneralSettings().getGuiDetailsView() == Statics.GuiDetailsView.VerticalSplit;
        if (!vertical) {
            centerPanel.add(detailPanel, BorderLayout.SOUTH);
        }

        // Add
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, divisionPanel, centerPanel);
        centerSplitPane.setOneTouchExpandable(true);
        add(centerSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        Application.beginWait(MainPanelLayout.this);
        try {
            // Update table if needed
            if (args.length != 0 && args[0] != null) {
                if (args[0] instanceof Division) {
                    if (selectedDivision == null || !selectedDivision.equals(args[0])) {
                        selectedDivision = (Division) args[0];
                        if (selectedDivision != null) {
                            setItemTableList(selectedDivision.getItemList());
                        }
                    }
                } else if (args[0] instanceof Set ) {
                    if (selectedSet == null || !selectedSet.equals(args[0])) {
                        selectedSet = (Set) args[0];
                        if (selectedSet != null) {
                            setItemTableList(selectedSet.getSetItems(), false, true);
                        }
                    }
                }
            }

            // Enabled components
            updateEnabledComponents();

            // Update detail panel
            detailPanel.updateComponents(selectedItem);
        } finally {
            Application.endWait(MainPanelLayout.this);
        }
    }
}
