package com.waldo.inventory.classes.search;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.managers.CacheManager;
import com.waldo.inventory.managers.SearchManager;

import java.util.ArrayList;
import java.util.List;

public class ItemFinder {

    //
    // TODO: add paramaters to filter list (ie don't always search whole cache().getItems list
    public static List<ObjectMatch<Item>> searchByKeyWord(String keyWord) {
        List<ObjectMatch<Item>> objectMatches = new ArrayList<>();
        for (Item item : CacheManager.cache().getItems()) {
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
                    itemsToSearch = CacheManager.cache().getItems();
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
}
