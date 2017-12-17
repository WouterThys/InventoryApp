package com.waldo.inventory.gui.dialogs.advancedsearchdialog;

import com.waldo.inventory.classes.DbObjectSearcher;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class AdvancedSearchDialog extends AdvancedSearchDialogLayout implements DbObjectSearcher.SearchListener<Item> {

    private DbObjectSearcher<Item> searcher;

    public AdvancedSearchDialog(Application application, String title, boolean allowMultiSelect) {
        super(application, title, allowMultiSelect);

        initializeComponents();
        initializeLayouts();
        updateComponents();

        searcher = new DbObjectSearcher<>(cache().getItems(), this);
    }

    public Item getSelectedItem() {
        return tableGetSelected();
    }

    @Override
    void onMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            EditItemDialog editItemDialog = new EditItemDialog(application, "Edit item", getSelectedItem());
            editItemDialog.showDialog();
            tableUpdate();
        }
    }

    @Override
    void onSearch(String searchWord) {
        if (searchWord != null && !searchWord.isEmpty()) {
            searcher.search(searchWord);
        } else {
            setError("Enter a search word");
            tableClear();
        }
    }

    @Override
    void onNext() {
        if (searcher.hasSearchResults()) {
            searcher.findNextObject();
        }
    }

    @Override
    void onPrevious() {
        if (searcher.hasSearchResults()) {
            searcher.findPreviousObject();
        }
    }

    //
    // Dialog
    //

    //
    // Table item selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateEnabledComponents();
    }

    //
    // Search
    //
    @Override
    public void onObjectsFound(List<Item> foundObjects) {
        int size = foundObjects.size();
        if (size == 0) {
            setInfo("No items found..");
            tableClear();
        } else if (size == 1) {
            setInfo("1 item found!");
            tableInitialize(foundObjects);
        } else  {
            setInfo(String.valueOf(size) + " results found!!");
            tableInitialize(foundObjects);
        }
        updateEnabledComponents();
    }

    @Override
    public void onSearchCleared() {
        tableClear();
        clearResultText();
        updateEnabledComponents();
    }

    @Override
    public void onNextSearchObject(Item next) {
        tableSelect(next);
    }

    @Override
    public void onPreviousSearchObject(Item previous) {
        tableSelect(previous);
    }
}
