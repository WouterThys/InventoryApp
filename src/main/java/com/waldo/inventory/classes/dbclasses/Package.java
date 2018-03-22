package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Package extends DbObject {

    public static final String TABLE_NAME = "packages";

    private String description;

    public Package() {
        super(TABLE_NAME);
        matchCount = 1;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setString(ndx++, getDescription());

        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        return result && obj instanceof Package && ((Package) obj).getDescription().equals(getDescription());
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        String search = searchTerm.toUpperCase();
        String name = getName().toUpperCase();
        return name.contains(search) || search.contains(name);
    }

    @Override
    protected int findMatch(String searchTerm) {
        int match = 0;
        String name = getName().toUpperCase();
        if( name.contains(searchTerm) || searchTerm.contains(name)) match++;
        return match;
    }

    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    @Override
    public Package createCopy() {
        return createCopy(new Package());
    }

    @Override
    public Package createCopy(DbObject copyInto) {
        Package pack = (Package) copyInto;
        copyBaseFields(pack);

        pack.setDescription(getDescription());

        return pack;
    }

    public String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
