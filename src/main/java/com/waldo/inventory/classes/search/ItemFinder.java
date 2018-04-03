package com.waldo.inventory.classes.search;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.managers.SearchManager;

import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ItemFinder {

    public static final ItemFilter<Division> divisionFilter = new ItemFilter<>();
    public static final ItemFilter<Manufacturer> manufacturerFilter = new ItemFilter<>();
    public static final ItemFilter<Location> locationFilter = new ItemFilter<>();

    private static final List<Item> itemList = new ArrayList<>();


    private static List<Item> filterItems() {

        if (divisionFilter.isChanged() || manufacturerFilter.isChanged() || locationFilter.isChanged()) {
            itemList.clear();
            divisionFilter.setChanged(false);
            manufacturerFilter.setChanged(false);
            locationFilter.setChanged(false);

            // Division
            if (divisionFilter.hasFilter()) {
                for (Division d : divisionFilter.getFilters()) {
                    itemList.addAll(d.getItemList());
                }
            }

            // Manufacturer
            if (manufacturerFilter.hasFilter()) {
                // No division filter
                if (itemList.size() == 0) {
                    for (Manufacturer m : manufacturerFilter.getFilters()) {
                        itemList.addAll(SearchManager.sm().findItemsForManufacturer(m));
                    }
                } else {
                    for (int i = itemList.size() - 1; i >= 0; i--) {
                        Item item = itemList.get(i);
                        for (Manufacturer m : manufacturerFilter.getFilters()) {
                            if (item.getManufacturerId() != m.getId()) {
                                itemList.remove(i);
                                break;
                            }
                        }
                    }
                }
            }

            // Location
            if (locationFilter.hasFilter()) {
                // No previous filter
                if (itemList.size() == 0) {
                    for (Location l : locationFilter.getFilters()) {
                        itemList.addAll(SearchManager.sm().findItemsForLocation(l));
                    }
                } else {
                    for (int i = itemList.size() - 1; i >= 0; i--) {
                        Item item = itemList.get(i);
                        for (Location l : locationFilter.getFilters()) {
                            if (item.getLocationId() != l.getId()) {
                                itemList.remove(i);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Just add all..
        if (itemList.size() == 0) {
            itemList.addAll(cache().getItems());
        }

        return itemList;
    }

    public static List<ObjectMatch<Item>> searchByKeyWord(String keyWord) {
        List<ObjectMatch<Item>> objectMatches = new ArrayList<>();

        for (Item item : filterItems()) {
            List<SearchMatch> searchMatches = item.searchByKeyWord(keyWord);
            if (searchMatches.size() > 0) {
                objectMatches.add(new ObjectMatch<>(item, searchMatches));
            }
        }

        if (objectMatches.size() > 0) {
            if (objectMatches.size() == 1) {
                objectMatches.sort(new ComparatorUtils.FoundMatchComparator());
            }
        }

        return objectMatches;
    }

    public static List<ObjectMatch<Item>> searchByPcbItem(PcbItemProjectLink pcbItemProjectLink) {
        List<ObjectMatch<Item>> objectMatches = new ArrayList<>();

        if (pcbItemProjectLink != null) {
            PcbItem pcbItem = pcbItemProjectLink.getPcbItem();
            boolean divisionsFound = false;

            if (pcbItem != null) {
                List<Item> itemsToSearch = null;
                ParserItemLink link = SearchManager.sm().findParserItemLink(pcbItem);
                if (link != null) {
                    Division division = link.getDivision();
                    if (division != null) {
                        division.updateItemList();
                        itemsToSearch = new ArrayList<>(division.getItemList());
                    }
                }
                if (itemsToSearch == null || itemsToSearch.size() == 0) {
                    itemsToSearch = cache().getItems();
                } else {
                    divisionsFound = true;
                }

                for (Item item : itemsToSearch) {
                    List<SearchMatch> searchMatches = item.searchByPcbItem(pcbItemProjectLink, divisionsFound);
                    if (searchMatches.size() > 0) {
                        objectMatches.add(new ObjectMatch<>(item, searchMatches, 128));
                    }
                }

                if (objectMatches.size() > 1) {
                    objectMatches.sort(new ComparatorUtils.FoundMatchComparator());
                }
            }
        }

        return objectMatches;
    }

    public static class ItemFilter<T extends DbObject> {

        private final List<T> filterObjects = new ArrayList<>();
        private boolean changed;

        ItemFilter() {
            changed = true;
        }

        public List<T> getFilters() {
            return new ArrayList<>(filterObjects);
        }

        public void addFilter(T filter) {
            if (!filterObjects.contains(filter)) {
                filterObjects.add(filter);
                changed = true;
            }
        }

        public void setFilters(List<T> filterObjects) {
            if (filterObjects != null) {
                this.filterObjects.clear();
                this.filterObjects.addAll(filterObjects);
                changed = true;
            }
        }

        public void removeFilter(T filter) {
            changed = filterObjects.remove(filter);
        }

        public void clear() {
            filterObjects.clear();
            changed = true;
        }

        public boolean isChanged() {
            return changed;
        }

        public void setChanged(boolean changed) {
            this.changed = changed;
        }

        public boolean hasFilter() {
            return filterObjects.size() > 0;
        }

    }
}
