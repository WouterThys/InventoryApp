package com.waldo.inventory.classes.search;

public class SearchMatch {

    private int weight;
    private int totalWeight;
    private Object searchMe;
    private Object findMe;

    public SearchMatch(int weight, Object searchMe, Object findMe) {
        this(weight, -1, searchMe, findMe);
    }

    public SearchMatch(int weight, int totalWeight, Object searchMe, Object findMe) {
        this.weight = weight;
        this.totalWeight = totalWeight;
        this.searchMe = searchMe;
        this.findMe = findMe;
    }

    @Override
    public String toString() {
        return findMe.toString();
    }

    public static SearchMatch hasMatch(int weight, String s1, String s2) {
        SearchMatch match = null;
        if (s1 != null && s2 != null) {
            s1 = s1.toUpperCase().trim();
            s2 = s2.toUpperCase().trim();
            int searchMeLength = s1.length();
            int findMeLength = s2.length();
            for (int i = 0; i <= (searchMeLength - findMeLength); i++) {
                if (s1.regionMatches(i, s2, 0, findMeLength)) {
                    match = new SearchMatch(weight, s1, s2);
                    break;
                }
            }
        }
        return match;
    }

    public static SearchMatch hasMatch(int weight, Object o1, Object o2) {
        SearchMatch match = null;
        if (o1 != null && o2 != null) {
            match = hasMatch(weight, o1.toString(), o2.toString());
        }
        return match;
    }

    public int getWeight() {
        return weight;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public int getPercent() {
        if (totalWeight < 0) {
            return weight;
        }
        return (int)((double)(weight / totalWeight) * 100);
    }
}
