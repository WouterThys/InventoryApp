package com.waldo.inventory.classes;

import com.waldo.inventory.classes.dbclasses.DbObject;

import java.util.ArrayList;
import java.util.List;

public class DbObjectSearcher<T extends DbObject> {

    public interface SearchListener<dbo extends DbObject> {
        void onDbObjectFound(List<dbo> foundObjects);
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

    public void addSearchListener(SearchListener<T> searchListener) {
        this.searchListener = searchListener;
    }

    public List<T> search(String searchWord) {
        List<T> foundObjects;

        // Search list
        if (searchOptions == null || searchOptions.length == 0) {
            foundObjects = searchForObject(searchList, searchWord);
        } else {
            foundObjects = searchForObject(searchList /*, searchOptions*/, searchWord);
        }

        currentResultNdx = 0;
        resultList = foundObjects;

        if (searchListener != null) {
            searchListener.onDbObjectFound(foundObjects);
        }

        return foundObjects;
    }

    public void clearSearch() {
        resultList.clear();
        currentResultNdx = 0;

        if (searchListener != null) {
            searchListener.onSearchCleared();
        }
    }

    public void getNextFoundObject() {
        if (searchListener != null) {
            currentResultNdx++;
            if (currentResultNdx >= resultList.size()) {
                currentResultNdx = 0;
            }
            searchListener.onNextSearchObject(resultList.get(currentResultNdx));
        }
    }

    public void getPreviousFoundObject() {
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
