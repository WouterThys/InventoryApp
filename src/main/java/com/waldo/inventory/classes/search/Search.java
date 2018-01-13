package com.waldo.inventory.classes.search;

import com.waldo.inventory.classes.dbclasses.DbObject;

import java.util.List;

public class Search {

    private Search() {}

    public interface SearchListener<dbo extends DbObject> {
        void onObjectsFound(List<dbo> foundObjects);
        void onSearchCleared();
        void onNextSearchObject(dbo next);
        void onPreviousSearchObject(dbo previous);
    }

    public static class DbObjectSearch <T extends DbObject> {

        private final DbObjectSearchClass<T> searchClass;

        public DbObjectSearch(List<T> searchList, SearchListener<T> searchListener) {
            searchClass = new DbObjectSearchClass<>(searchList, searchListener);
        }

        public void setSearchList(List<T> searchList) {
            searchClass.setSearchList(searchList);
        }

        public void search(String searchWord) {
            searchClass.search(searchWord);
        }

        public void search(DbObject dbObject) {
            searchClass.search(dbObject);
        }

        public void clearSearch() {
            searchClass.clearSearch();
        }

        public void setSearched(boolean searched) {
            searchClass.setSearched(searched);
        }

        public boolean isSearched() {
            return searchClass.isSearched();
        }

        public void findPreviousObject() {
            searchClass.findPreviousObject();
        }

        public void findNextObject() {
            searchClass.findNextObject();
        }

        public boolean hasSearchResults() {
            return searchClass.hasSearchResults();
        }
    }
}
