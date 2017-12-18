package com.waldo.inventory.classes.search;

import com.waldo.inventory.classes.dbclasses.DbObject;

public class DbObjectMatch {

    protected int match; // Match counter
    private int matchCount; // Total of fields that are checked for match

    public DbObjectMatch(int matchCount) {
        this.matchCount = matchCount;
    }

    public boolean hasMatch() {
        return match > 0;
    }

    public int getMatchPercent() {
        return (match *100) / matchCount;
    }

    public void calculateMatch(String searchWord) {
        match = 0;
    }

    public void calculateMatch(DbObject dbObject) {
        match = 0;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }
}
