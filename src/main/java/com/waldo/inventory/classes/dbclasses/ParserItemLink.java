package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ParserItemLink extends DbObject {

    public static final String TABLE_NAME = "parseritemlinks";

    // Variables
    private String parserName;
    private String pcbItemName; // "C" or "R" or ...

    private long divisionId;
    private Division division;

    public ParserItemLink() {
        super(TABLE_NAME);
    }

    public ParserItemLink(String parserName) {
        super(TABLE_NAME);
        this.parserName = parserName;
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        // Add parameters
        statement.setString(ndx++, getParserName());
        statement.setString(ndx++, getPcbItemName());
        statement.setLong(ndx++, getDivisionId());

        return ndx;
    }

    @Override
    public ParserItemLink createCopy(DbObject copyInto) {
        ParserItemLink cpy = (ParserItemLink) copyInto;

        // Add variables
        cpy.setParserName(getParserName());
        cpy.setPcbItemName(getPcbItemName());
        cpy.setDivisionId(getDivisionId());

        return cpy;
    }

    @Override
    public ParserItemLink createCopy() {
        return createCopy(new ParserItemLink());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                List<ParserItemLink> list = cache().getParserItemLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case Delete: {
                List<ParserItemLink> list = cache().getParserItemLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    // Getters and setters

    public String getParserName() {
        if (parserName == null) {
            parserName = "";
        }
        return parserName;
    }

    public void setParserName(String parserName) {
        this.parserName = parserName;
    }

    public String getPcbItemName() {
        if (pcbItemName == null) {
            pcbItemName = "";
        }
        return pcbItemName;
    }

    public void setPcbItemName(String pcbItemName) {
        this.pcbItemName = pcbItemName;
    }

    public long getDivisionId() {
        if (divisionId < UNKNOWN_ID) {
            divisionId = UNKNOWN_ID;
        }
        return divisionId;
    }

    public void setDivisionId(long divisionId) {
        if (division != null && division.getId() != divisionId) {
            division = null;
        }
        this.divisionId = divisionId;
    }

    public Division getType() {
        if (division == null && divisionId > UNKNOWN_ID) {
            division = SearchManager.sm().findDivisionById(divisionId);
        }
        return division;
    }
}