package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ITablePanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.actions.IAbstractAction;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.popups.TableOptionsPopup;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.components.trees.IDivisionTree;
import com.waldo.inventory.gui.components.trees.ISetTree;
import com.waldo.inventory.gui.panels.mainpanel.preview.DivisionPreviewPanel;
import com.waldo.inventory.gui.panels.mainpanel.preview.ItemPreviewPanel;
import com.waldo.inventory.gui.panels.mainpanel.preview.SetPreviewPanel;
import com.waldo.inventory.gui.panels.mainpanel.preview.itemdetailpanel.ItemDetailPanel;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.classes.dbclasses.DbObject.UNKNOWN_ID;
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

    DivisionPreviewPanel divisionPreviewPanel;
    IDivisionTree divisionTree;
    JToolBar divisionTb;
    IActions.ClearAction clearDivisionAa;
    IActions.ShowSelectionAction showDivisionAa;

    SetPreviewPanel setPreviewPanel;
    ISetTree setTree;

    AbstractDetailPanel itemDetailPanel;

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
        showDivisionAa.setEnabled(enabled && selectedItem.getDivisionId() > UNKNOWN_ID);
        clearDivisionAa.setEnabled(selectedDivision != null);
    }

    void updateDetails() {
        itemDetailPanel.updateComponents(selectedItem);
        divisionPreviewPanel.updateComponents(selectedDivision);
        setPreviewPanel.updateComponents(selectedSet);
        updateEnabledComponents();
    }

    abstract void onTreeRowClick(MouseEvent e);
    abstract void onTableRowClicked(MouseEvent e);
    abstract void onClearDivision();
    abstract void onShowDivision();

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
            this.selectedItem = itemTable.getItemAtRow(row);
            itemTable.selectItem(selectedItem);
        }
    }

    //
    // Tree stuff
    //
    void treeSelectDivisionForItem(Item item) {
        if (item != null) {
            divisionTree.setSelectedItem(item.getDivision());
        }
    }

    void treeSelectDivision(Division division) {
        divisionTree.setSelectedItem(division);
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
                if (ndx == 1){
                    selectedTreeTab = TREE_SETS;
                    if (selectedSet != null) {
                        setItemTableList(selectedSet.getSetItems(), false, true);
                    }
                } else {
                    selectedTreeTab = TREE_ITEMS;
                    if (selectedDivision != null) {
                        setItemTableList(selectedDivision.getItemList());
                    }
                }
                updateDetails();
                updateEnabledComponents();
            }
        });

        JPanel divisionPanel = new JPanel(new BorderLayout());
        JScrollPane pane = new JScrollPane(divisionTree);
        pane.setPreferredSize(new Dimension(300, 400));
        divisionPanel.add(divisionTb, BorderLayout.NORTH);
        divisionPanel.add(pane, BorderLayout.CENTER);
        divisionPanel.add(divisionPreviewPanel, BorderLayout.SOUTH);
        divisionPanel.setMinimumSize(new Dimension(200,200));

        JPanel setPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(setTree);
        pane.setPreferredSize(new Dimension(300, 400));
        setPanel.add(scrollPane, BorderLayout.CENTER);
        setPanel.add(setPreviewPanel, BorderLayout.SOUTH);
        setPanel.setMinimumSize(new Dimension(200, 200));

        tabbedPane.addTab(TREE_ITEMS, imageResource.readIcon("Component.SS"), divisionPanel);
        tabbedPane.addTab(TREE_SETS, imageResource.readIcon("Components.SS"), setPanel);

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
                if (SwingUtilities.isRightMouseButton(e) || e.getClickCount() >= 2) {
                    int row = divisionTree.getClosestRowForLocation(e.getX(), e.getY());
                    divisionTree.setSelectionRow(row);
                    onTreeRowClick(e);
                }
            }
        });
        divisionPreviewPanel = new DivisionPreviewPanel(application, rootDivision);
        selectedDivision = rootDivision;

        // Division toolbar
        clearDivisionAa = new IActions.ClearAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClearDivision();
            }
        };
        showDivisionAa = new IActions.ShowSelectionAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onShowDivision();
            }
        };

        divisionTb = GuiUtils.createNewToolbar(clearDivisionAa, showDivisionAa);

        // Sets
        List<Set> rootSets = cache().getSets();
        rootSets.sort(new ComparatorUtils.DbObjectNameComparator<>());
        Set rootSet = Set.createDummySet("Dummy", rootSets);

        setTree = new ISetTree(rootSet, false, false);
        setTree.addTreeSelectionListener(this);
        setTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || e.getClickCount() >= 2) {
                    int row = setTree.getClosestRowForLocation(e.getX(), e.getY());
                    setTree.setSelectionRow(row);
                    onTreeRowClick(e);
                }
            }
        });
        selectedSet = setTree.getRootSet();
        setPreviewPanel = new SetPreviewPanel(application);

        // Preview
        boolean vertical = settings().getGeneralSettings().getGuiDetailsView() == Statics.GuiDetailsView.VerticalSplit;
        if (vertical) {
            itemDetailPanel = new ItemPreviewPanel(this) {
                @Override
                public void onToolBarDelete(IdBToolBar source) {
                    MainPanelLayout.this.onToolBarDelete(source);
                }

                @Override
                public void onToolBarEdit(IdBToolBar source) {
                    MainPanelLayout.this.onToolBarEdit(source);
                }
            };
            itemDetailPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, -1, -1, -1),
                    BorderFactory.createLineBorder(Color.lightGray, 1)
            ));
        }

        // Items
        tableModel = new IItemTableModel();
        itemTable = new ITablePanel<>(tableModel, itemDetailPanel, this, true);
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
            itemDetailPanel = new ItemDetailPanel(this);
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
            centerPanel.add(itemDetailPanel, BorderLayout.SOUTH);
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
                    if (selectedDivision == null || !selectedDivision.equals(args[0]) || !selectedDivision.canBeSaved()) {
                        selectedDivision = (Division) args[0];
                        if (selectedDivision != null) {
                            setItemTableList(selectedDivision.getItemList());
                        }
                    }
                } else if (args[0] instanceof Set ) {
                    if (selectedSet == null || !selectedSet.equals(args[0]) || !selectedSet.canBeSaved()) {
                        selectedSet = (Set) args[0];
                        if (selectedSet != null) {
                            setItemTableList(selectedSet.getSetItems(), false, true);
                        }
                    }
                }
            }

            // Enabled components
            divisionPreviewPanel.updateComponents(selectedDivision);
            setPreviewPanel.updateComponents(selectedSet);
            updateEnabledComponents();

            // Update detail panel
            itemDetailPanel.updateComponents(selectedItem);
        } finally {
            Application.endWait(MainPanelLayout.this);
        }
    }
}
