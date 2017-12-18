package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Distributor extends DbObject {

    public static final String TABLE_NAME = "distributors";

    private String website;
    private String orderLink;
    private long orderFileFormatId = -1;
    private OrderFileFormat orderFileFormat;

    public Distributor() {
        super(TABLE_NAME);
        matchCount = 3;
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setString(ndx++, getWebsite());
        statement.setString(ndx++, getOrderLink());
        if (orderFileFormatId < UNKNOWN_ID) {
            orderFileFormatId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, getOrderFileFormatId());
        return ndx;
    }

    @Override
    protected int findMatch(String searchTerm) {
        int match = super.findMatch(searchTerm);
        if (searchTerm.contains(getWebsite().toUpperCase())) match++;
        return match;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof Distributor)) {
                return false;
            }
            if (!(((Distributor)obj).getWebsite().equals(getWebsite()))) return false;
            if (!(((Distributor)obj).getOrderLink().equals(getOrderLink()))) return false;
            if (!(((Distributor)obj).getOrderFileFormatId() == getOrderFileFormatId())) return false;
        }
        return result;
    }

    @Override
    public Distributor createCopy(DbObject copyInto) {
        Distributor distributor = (Distributor) copyInto;
        copyBaseFields(distributor);
        distributor.setWebsite(getWebsite());
        distributor.setOrderLink(getOrderLink());
        distributor.setOrderFileFormatId(getOrderFileFormatId());
        return distributor;
    }

    @Override
    public Distributor createCopy() {
        return createCopy(new Distributor());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<Distributor> distributors = cache().getDistributors();
                if (!distributors.contains(this)) {
                    distributors.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<Distributor> distributors = cache().getDistributors();
                if (distributors.contains(this)) {
                    distributors.remove(this);
                }
                break;
            }
        }
    }

    public String getWebsite() {
        if (website == null) {
            website = "";
        }
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOrderLink() {
        if (orderLink == null) {
            orderLink = "";
        }
        return orderLink;
    }

    public void setOrderLink(String orderLink) {
        this.orderLink = orderLink;
    }

    public long getOrderFileFormatId() {
        return orderFileFormatId;
    }

    public void setOrderFileFormatId(long orderFileFormatId) {
        if (orderFileFormat != null && orderFileFormat.getId() != orderFileFormatId) {
            orderFileFormat = null;
        }
        this.orderFileFormatId = orderFileFormatId;
    }

    public OrderFileFormat getOrderFileFormat() {
        if (orderFileFormat == null) {
            orderFileFormat = SearchManager.sm().findOrderFileFormatById(orderFileFormatId);
        }
        return orderFileFormat;
    }
}
