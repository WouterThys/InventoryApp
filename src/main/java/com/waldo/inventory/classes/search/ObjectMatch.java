package com.waldo.inventory.classes.search;

import com.waldo.inventory.classes.dbclasses.DbObject;

import java.util.List;

public class ObjectMatch<T extends DbObject> {

    private final T foundObject;
    private final List<SearchMatch> matches;

    private int totalWeight = -1;
    private int totalMatches = -1;
    private int maxWeight = 1;

    public ObjectMatch(T foundObject, List<SearchMatch> matches) {
        this.foundObject = foundObject;
        this.matches = matches;
    }


    public ObjectMatch(T foundObject, List<SearchMatch> matches, int maxWeight) {
        this.foundObject = foundObject;
        this.matches = matches;
        this.maxWeight = maxWeight;
    }

    public int getTotalWeight() {
        if (totalWeight < 0) {
            totalWeight = 0;
            for (SearchMatch m : matches) {
                totalWeight += m.getWeight();
            }
        }
        return totalWeight;
    }

    public int getPercent() {
        return (int)(((double)getTotalWeight() / maxWeight) * 100);
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

    public List<SearchMatch> getMatches() {
        return matches;
    }

    public String getMatchString() {
        StringBuilder result = new StringBuilder();
        if (matches != null && matches.size() > 0) {
            result.append(matches.size()).append(" found for: ");
            for (SearchMatch match : matches) {
                result.append(match.toString());
            }
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "ObjectMatch{" +
                "foundObject=" + foundObject +
                ", percent=" + getPercent() + "%" +
                '}';
    }

}
