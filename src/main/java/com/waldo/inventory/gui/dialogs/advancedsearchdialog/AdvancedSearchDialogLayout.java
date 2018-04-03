package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.gui.components.tablemodels.IFoundItemsTableModel;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public abstract class AdvancedSearchDialogLayout extends IDialog implements ListSelectionListener {


    public enum SearchType {
        SearchWord,
        PcbItem
    }

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField searchTf;
    private ILabel resultLbl;

    private FilterPanel<Manufacturer> manufacturerFilterPanel;
    private FilterPanel<Division> divisionFilterPanel;
    private FilterPanel<Location> locationFilterPanel;

    private IFoundItemsTableModel tableModel;
    private ITable<Item> foundItemTable;

    private JToolBar nextPrevTb;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     private SearchType searchType;
     private boolean allowMultiSelect;

     private String searchWord = "";
     private PcbItemProjectLink searchPcbItem = null;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    AdvancedSearchDialogLayout(Window parent, String title, SearchType searchType, Object... args) {
        super(parent, title);
        this.searchType = searchType;
        switch (searchType) {
            case SearchWord:
                allowMultiSelect = true;
                if (args.length > 0) searchWord = String.valueOf(args[0]);
                break;
            case PcbItem:
                allowMultiSelect = false;
                if (args.length > 0) searchPcbItem = (PcbItemProjectLink) args[0];
            break;
        }
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    abstract void onSearch(String searchWord);
    abstract void onSearch(DbObject dbObject);
    abstract void onNext();
    abstract void onPrevious();
    abstract void onMouseClicked(MouseEvent e);

    void updateEnabledComponents() {
        boolean hasSelected = tableGetSelected() != null;

        getButtonOK().setEnabled(hasSelected);

        nextPrevTb.setEnabled(tableModel.getRowCount() > 1);
    }

//    void tableInitialize(List<Item> foundItems) {
//        switch (searchType) {
//            case PcbItem:
//                foundItems.sort(new ComparatorUtils.ItemMatchComparator());
//                break;
//        }
//        tableModel.setItemList(foundItems);
//    }

    void addResults(List<Item> results) {
        // TODO #1 tableModel.addItems(results);
    }

    void tableUpdate() {
        tableModel.updateTable();
    }

    void tableClear() {
        tableModel.clearItemList();
    }

    void tableSelect(Item item) {
        foundItemTable.selectItem(item);
    }

    void tableSelect(int ndx) {
        // TODO #1 foundItemTable.selectItem(tableModel.getItemList().get(ndx));
    }

    Item tableGetSelected() {
        return foundItemTable.getSelectedItem();
    }

    void setError(String error) {
        resultLbl.setForeground(Color.RED);
        resultLbl.setText(error);
    }

    void setInfo(String info) {
        resultLbl.setForeground(Color.BLACK);
        resultLbl.setText(info);
    }

    void clearResultText() {
        resultLbl.setText("");
    }


    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();

        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
        filterPanel.setBorder(GuiUtils.createTitleBorder("Filters"));

        filterPanel.add(divisionFilterPanel);
        filterPanel.add(manufacturerFilterPanel);
        filterPanel.add(locationFilterPanel);

        return filterPanel;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Search.Title"));
        setTitleName(getTitle());
        getButtonOK().setText("Select");
        getButtonOK().setEnabled(false);
        setResizable(true);

        // This
        searchTf = new ITextField("Search");
        searchTf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                onSearch(searchTf.getText());
            }
        });

        resultLbl = new ILabel("Results: ");

        AbstractAction nextResultAction = new AbstractAction("Next", imageResource.readImage("Search.Next")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNext();
            }
        };
        AbstractAction prevResultAction = new AbstractAction("Previous", imageResource.readImage("Search.Previous")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPrevious();
            }
        };

        nextPrevTb = new JToolBar(JToolBar.HORIZONTAL);
        nextPrevTb.setFloatable(false);
        nextPrevTb.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        nextPrevTb.add(nextResultAction);
        nextPrevTb.add(prevResultAction);


        manufacturerFilterPanel = new FilterPanel<>("Manufacturers", cache().getManufacturers());
        divisionFilterPanel = new FilterPanel<>("Divisions", SearchManager.sm().findDivisionsWithoutParent());
        locationFilterPanel = new FilterPanel<>("Locations", cache().getLocations().subList(0, 20));

        // TODO #1
//        tableModel = new IFoundItemsTableModel(searchType, null);
//        foundItemTable = new ITable<>(tableModel);
//        if (!allowMultiSelect) {
//            foundItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        }
//        foundItemTable.getSelectionModel().addListSelectionListener(this);
//        foundItemTable.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                onMouseClicked(e);
//            }
//        });

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel searchPnl = new JPanel(new BorderLayout());
        searchPnl.add(new ILabel("Search word: "), BorderLayout.PAGE_START);
        searchPnl.add(searchTf, BorderLayout.CENTER);

        JPanel infoPnl = new JPanel(new BorderLayout());
        infoPnl.add(resultLbl, BorderLayout.WEST);
        infoPnl.add(nextPrevTb, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(foundItemTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        JPanel resultPnl = new JPanel(new BorderLayout());
        resultPnl.add(infoPnl, BorderLayout.PAGE_START);
        resultPnl.add(scrollPane, BorderLayout.CENTER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(searchPnl, BorderLayout.NORTH);
        northPanel.add(createFilterPanel(), BorderLayout.CENTER);

        getContentPanel().add(northPanel, BorderLayout.NORTH);
        getContentPanel().add(resultPnl, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        switch (searchType) {
            case SearchWord:
                if (searchWord != null && !searchWord.isEmpty()) {
                    searchTf.setText(searchWord);
                    onSearch(searchWord);
                }
                break;
            case PcbItem:
                if (searchPcbItem != null) {
                    onSearch(searchPcbItem);
                }
                break;
        }
    }
}