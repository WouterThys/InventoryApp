package com.waldo.inventory.classes.search;

import com.waldo.inventory.classes.dbclasses.DbObject;

import java.util.List;

public class ObjectMatch<T extends DbObject> {

    private final T foundObject;
    private final List<Match> matches;

    private int totalWeight = -1;
    private int totalMatches = -1;

    public ObjectMatch(T foundObject, List<Match> matches) {
        this.foundObject = foundObject;
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

    public T getFoundObject() {
        return foundObject;
    }

    @Override
    public String toString() {
        return "ItemMatch{" +
                "foundObject=" + foundObject +
                ", totalWeight=" + totalWeight +
                ", totalMatches=" + totalMatches +
                '}';
    }


    public static class Match {

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

}
