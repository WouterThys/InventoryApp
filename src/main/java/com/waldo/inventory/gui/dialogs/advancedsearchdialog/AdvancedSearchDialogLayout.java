package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.PcbItemProjectLink;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITable;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.tablemodels.IFoundItemsTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

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

    private IFoundItemsTableModel tableModel;
    private ITable<DbObject> foundItemTable;

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
    AdvancedSearchDialogLayout(Application application, String title, SearchType searchType, Object... args) {
        super(application, title);
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

    void addResults(List<DbObject> results) {
        tableModel.addItems(results);
    }

    void tableUpdate() {
        tableModel.updateTable();
    }

    void tableClear() {
        tableModel.clearItemList();
    }

    void tableSelect(DbObject item) {
        foundItemTable.selectItem(item);
    }

    DbObject tableGetSelected() {
        return foundItemTable.getSelectedItem();
    }

    List<DbObject> tableGetAllSelected() {
        return foundItemTable.getSelectedItems();
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

        tableModel = new IFoundItemsTableModel(searchType, new ComparatorUtils.ObjectMatchComparator());
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

        JPanel searchPnl = new JPanel(new BorderLayout());
        searchPnl.add(new ILabel("Search word: "), BorderLayout.PAGE_START);
        searchPnl.add(searchTf, BorderLayout.CENTER);

        JPanel infoPnl = new JPanel(new BorderLayout());
        infoPnl.add(resultLbl, BorderLayout.WEST);
        infoPnl.add(nextPrevTb, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(foundItemTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        JPanel resultPnl = new JPanel(new BorderLayout());
        resultPnl.add(infoPnl, BorderLayout.PAGE_START);
        resultPnl.add(scrollPane, BorderLayout.CENTER);

        getContentPanel().add(searchPnl, BorderLayout.NORTH);
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