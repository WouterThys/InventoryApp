package com.waldo.inventory.classes.search;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.classes.dbclasses.Item;

import java.util.*;

public class ItemFinder {



    public static void searchByKeyWord(List<Item> itemList, String searchTerm) {

        List<ItemMatch> itemMatches = new ArrayList<>();

        if (itemList != null && itemList.size() > 0 && searchTerm != null && !searchTerm.isEmpty()) {
            searchTerm = searchTerm.toUpperCase();
            for (Item item : itemList) {
                List<Match> matchList = new ArrayList<>();
                Match m;

                m = Match.hasMatch(32, item.getName(), searchTerm);
                if (m != null) matchList.add(m);

                m = Match.hasMatch(16, item.getAlias(), searchTerm);
                if (m != null) matchList.add(m);

                m = Match.hasMatch(8, item.getValue().toString(), searchTerm);
                if (m != null) matchList.add(m);

                m = Match.hasMatch(4, item.getCategory(), searchTerm);
                if (m != null) matchList.add(m);

                m = Match.hasMatch(4, item.getProduct(), searchTerm);
                if (m != null) matchList.add(m);

                m = Match.hasMatch(4, item.getType(), searchTerm);
                if (m != null) matchList.add(m);

                m = Match.hasMatch(2, item.getManufacturer(), searchTerm);
                if (m != null) matchList.add(m);

                m = Match.hasMatch(2, item.getLocation(), searchTerm);
                if (m != null) matchList.add(m);

                m = Match.hasMatch(2, item.getPackageType(), searchTerm);
                if (m != null) matchList.add(m);

                if (matchList.size() > 0) {
                    if (matchList.size() > 1) {
                        matchList.sort(new ComparatorUtils.MatchComparator());
                    }
                    itemMatches.add(new ItemMatch(item, matchList));
                }

            }

            if (itemMatches.size() > 0) {
                itemMatches.sort(new ItemMatchComparator());
                System.out.println("Found " + itemMatches.size() + " items");
            }
        }
    }

    private static class ItemMatchComparator implements Comparator<ItemMatch> {

        @Override
        public int compare(ItemMatch m1, ItemMatch m2) {
            if (m1 == null && m2 != null) {
                return -1;
            } else if (m1 != null && m2 == null) {
                return 1;
            } else if (m1 != null){
                int res = Integer.compare(m2.getTotalWeight(), m1.getTotalWeight());
                if (res == 0) {
                    res = Integer.compare(m2.getTotalMatches(), m1.getTotalMatches());
                }
                return res;
            } else {
                return -1;
            }
        }
    }

    private static class ItemMatch {

        private final Item item;
        private final List<Match> matches;

        private int totalWeight = -1;
        private int totalMatches = -1;

        ItemMatch(Item item, List<Match> matches) {
            this.item = item;
            this.matches = matches;
        }

        public int getTotalWeight() {
            if (totalWeight < 0) {
                totalWeight = 0;
                for (Match m : matches) {
                    totalWeight += m.getWeight();
                }
            }
            return totalWeight;
        }

        public int getTotalMatches() {
            if (totalMatches < 0) {
                totalMatches = matches.size();
            }
            return totalMatches;
        }

        @Override
        public String toString() {
            return "ItemMatch{" +
                    "item=" + item +
                    ", totalWeight=" + totalWeight +
                    ", totalMatches=" + totalMatches +
                    '}';
        }
    }

}
