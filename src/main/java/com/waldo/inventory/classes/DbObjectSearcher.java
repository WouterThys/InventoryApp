package com.waldo.inventory.classes;

import com.waldo.inventory.classes.dbclasses.DbObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class DbObjectSearcher<T extends DbObject> {

    public interface SearchListener<dbo extends DbObject> {
        void onObjectsFound(List<dbo> foundObjects);
        void onSearchCleared();
        void onNextSearchObject(dbo next);
        void onPreviousSearchObject(dbo previous);
    }

    private boolean searched = false;
    private boolean inAdvanced = false;
    private boolean hasAdvancedSearchOption;
    private int[] searchOptions;
    private List<T> searchList;
    private List<T> resultList = new ArrayList<>();
    private int currentResultNdx = 0;

    private SearchListener<T> searchListener;

    public DbObjectSearcher(List<T> searchList) {
        this(searchList, null);
    }

    public DbObjectSearcher(List<T> searchList, SearchListener<T> searchListener) {
        this.searchList = searchList;
        this.searchListener = searchListener;
    }

    public void search(String searchWord) {
        resultList.clear();
        SwingUtilities.invokeLater(() -> {
            // Search list
            if (searchOptions == null || searchOptions.length == 0) {
                resultList = searchForObject(searchList, searchWord);
            } else {
                resultList = searchForObject(searchList /*, searchOptions*/, searchWord);
            }

            currentResultNdx = 0;

            if (searchListener != null) {
                searchListener.onObjectsFound(resultList);
            }
        });
    }

    public void clearSearch() {
        resultList.clear();
        currentResultNdx = 0;

        if (searchListener != null) {
            searchListener.onSearchCleared();
        }
    }

    public boolean hasSearchResults() {
        return resultList != null && resultList.size() > 0;
    }

    public void findNextObject() {
        if (searchListener != null) {
            currentResultNdx++;
            if (currentResultNdx >= resultList.size()) {
                currentResultNdx = 0;
            }
            searchListener.onNextSearchObject(resultList.get(currentResultNdx));
        }
    }

    public void findPreviousObject() {
        if (searchListener != null) {
            currentResultNdx--;
            if (currentResultNdx < 0) {
                currentResultNdx = resultList.size()-1;
            }
            searchListener.onPreviousSearchObject(resultList.get(currentResultNdx));
        }
    }

    private List<T> searchForObject(List<T> listToSearch, String searchWord) {
        List<T> foundList = new ArrayList<>();
        if (listToSearch == null || listToSearch.size() == 0) {
            return foundList;
        }

        searchWord = searchWord.toUpperCase();

        for (T dbo : listToSearch) {
            if (dbo.hasMatch(searchWord)) {
                foundList.add(dbo);
            }
        }

        return foundList;
    }

    public boolean isSearched() {
        return searched;
    }

    public void setSearched(boolean searched) {
        this.searched = searched;
    }

    public boolean isInAdvanced() {
        return inAdvanced;
    }

    public void setInAdvanced(boolean inAdvanced) {
        this.inAdvanced = inAdvanced;
    }

    public boolean isHasAdvancedSearchOption() {
        return hasAdvancedSearchOption;
    }

    public void setHasAdvancedSearchOption(boolean hasAdvancedSearchOption) {
        this.hasAdvancedSearchOption = hasAdvancedSearchOption;
    }

    public void setSearchOptions(int[] searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void setSearchList(List<T> searchList) {
        this.searchList = searchList;
    }

    public List<T> getResultList() {
        return resultList;
    }

}
