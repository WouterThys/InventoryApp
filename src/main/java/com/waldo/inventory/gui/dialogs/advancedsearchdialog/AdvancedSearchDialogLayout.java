package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.classes.search.ObjectMatch;
import com.waldo.inventory.gui.components.IObjectSearchPanel;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.components.tablemodels.IFoundItemsTableModel;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITable;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public abstract class AdvancedSearchDialogLayout extends IDialog implements ListSelectionListener, IObjectSearchPanel.SearchListener<Item> {

    private static final ImageIcon openIcon = imageResource.readIcon("Search.Next");
    private static final ImageIcon closeIcon = imageResource.readIcon("Search.Previous");

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    IObjectSearchPanel<Item> itemSearchPnl;

    FilterPanel<Manufacturer> manufacturerFilterPanel;
    FilterPanel<Division> divisionFilterPanel;
    FilterPanel<Location> locationFilterPanel;

    private IFoundItemsTableModel tableModel;
    private ITable<ObjectMatch<Item>> foundItemTable;

    private JPanel optionsPnl;
    private IActions.UseAction showOptionsAction;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     private boolean allowMultiSelect;
     private DbObject searchObject = null;
     private boolean searchDbObject = false;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AdvancedSearchDialogLayout(Window parent, boolean allowMultiSelect) {
        super(parent, "Advanced item search");
        this.allowMultiSelect = allowMultiSelect;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract List<ObjectMatch<Item>> onSearch(String searchWord);
    abstract List<ObjectMatch<Item>> onSearch(DbObject dbObject);
    abstract void onMouseClicked(MouseEvent e);

    public void searchPcbItem(PcbItemProjectLink searchPcbItem) {
        this.searchObject = searchPcbItem;
        if (searchPcbItem != null && searchPcbItem.getPcbItem() != null) {
            searchDbObject = true;

            ParserItemLink link = SearchManager.sm().findParserItemLink(searchPcbItem.getPcbItem());
            if (link != null) {
                Division division = link.getDivision();
                if (division != null) {
                    division.updateItemList();

                    Division parent = division.getParentDivision();
                    while (parent != null) {
                        division = parent;
                        parent = division.getParentDivision();
                    }

                    divisionFilterPanel.setSelected(division);
                }
            }

            itemSearchPnl.setSearchText(searchPcbItem.getPcbItem().toString(), true);
        }
    }

    void updateEnabledComponents() {
        boolean hasSelected = tableGetSelected() != null;

        getButtonOK().setEnabled(hasSelected);
    }

    void addResults(List<ObjectMatch<Item>> results) {
        if (results != null) {
            tableModel.setItemList(results);
        }
    }

    void tableUpdate() {
        tableModel.updateTable();
    }

    void tableClear() {
        tableModel.clearItemList();
    }

    Item tableGetSelected() {
        ObjectMatch<Item> selected = foundItemTable.getSelectedItem();
        if (selected != null) {
            return selected.getFoundObject();
        }
        return null;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(GuiUtils.createTitleBorder("Filters"));

        optionsPnl.setLayout(new BoxLayout(optionsPnl, BoxLayout.X_AXIS));
        optionsPnl.add(divisionFilterPanel);
        optionsPnl.add(manufacturerFilterPanel);
        optionsPnl.add(locationFilterPanel);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(GuiUtils.createComponentWithActions(new ILabel("Options "), showOptionsAction), BorderLayout.EAST);

        filterPanel.add(northPanel, BorderLayout.NORTH);
        filterPanel.add(optionsPnl, BorderLayout.CENTER);

        return filterPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readIcon("Search.Title"));
        setTitleName(getTitle());
        getButtonOK().setText("Select");
        getButtonOK().setEnabled(false);
        setResizable(true);

        itemSearchPnl = new IObjectSearchPanel<Item>(cache().getItems(), this, true) {
            @Override
            protected List<ObjectMatch<Item>> doSearch(String searchWord) {
                if (searchDbObject) {
                    return onSearch(searchObject);
                } else {
                    return onSearch(searchWord);
                }
            }
        };

        optionsPnl = new JPanel();
        showOptionsAction = new IActions.UseAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (optionsPnl.isVisible()) {
                    optionsPnl.setVisible(false);
                    showOptionsAction.setIcon(openIcon);
                } else {
                    optionsPnl.setVisible(true);
                    showOptionsAction.setIcon(closeIcon);
                }
            }
        };
        manufacturerFilterPanel = new FilterPanel<>("Manufacturers", cache().getManufacturers());
        divisionFilterPanel = new FilterPanel<>("Divisions", SearchManager.sm().findDivisionsWithoutParent());
        locationFilterPanel = new FilterPanel<>("Locations", cache().getLocations().subList(0, 20));

        tableModel = new IFoundItemsTableModel();
        foundItemTable = new ITable<>(tableModel);
        if (!allowMultiSelect) {
            foundItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        foundItemTable.getSelectionModel().addListSelectionListener(this);
        foundItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClicked(e);
            }
        });

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JScrollPane scrollPane = new JScrollPane(foundItemTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        JPanel resultPnl = new JPanel(new BorderLayout());
        resultPnl.add(scrollPane, BorderLayout.CENTER);

        JPanel searchWordPnl = new JPanel(new BorderLayout());
        searchWordPnl.add(itemSearchPnl, BorderLayout.CENTER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(searchWordPnl, BorderLayout.NORTH);
        northPanel.add(createFilterPanel(), BorderLayout.CENTER);

        getContentPanel().add(northPanel, BorderLayout.NORTH);
        getContentPanel().add(resultPnl, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
//        switch (searchType) {
//            case SearchWord:
//                if (searchWord != null && !searchWord.isEmpty()) {
//                    searchTf.setText(searchWord);
//                    onSearch(searchWord);
//                }
//                break;
//            case PcbItem:
//                if (searchPcbItem != null) {
//                    onSearch(searchPcbItem);
//                }
//                break;
//        }
    }
}