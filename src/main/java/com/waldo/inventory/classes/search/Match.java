package com.waldo.inventory.classes.search;

public class Match {

    private int weight;
    private Object searchMe;
    private Object findMe;

    private Match(int weight, Object searchMe, Object findMe) {
        this.weight = weight;
        this.searchMe = searchMe;
        this.findMe = findMe;
    }

    @Override
    public String toString() {
        return "Match{" +
                "weight=" + weight +
                ", searchMe=" + searchMe +
                ", findMe=" + findMe +
                '}';
    }

    public static Match hasMatch(int weight, String s1, String s2) {
        Match match = null;
        if (s1 != null && s2 != null) {
            s1 = s1.toUpperCase().trim();
            s2 = s2.toUpperCase().trim();
            int searchMeLength = s1.length();
            int findMeLength = s2.length();
            for (int i = 0; i <= (searchMeLength - findMeLength); i++) {
                if (s1.regionMatches(i, s2, 0, findMeLength)) {
                    match = new Match(weight, s1, s2);
                    break;
                }
            }
        }
        return match;
    }

    public static Match hasMatch(int weight, Object o1, Object o2) {
        Match match = null;
        if (o1 != null && o2 != null) {
            match = hasMatch(weight, o1.toString(), o2.toString());
        }
        return match;
    }

    public int getWeight() {
        return weight;
    }
}
