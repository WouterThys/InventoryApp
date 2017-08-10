package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class Distributor extends DbObject {

    public static final String TABLE_NAME = "distributors";

    private String website;
    private String orderLink;
    private long orderFileFormatId = -1;
    private OrderFileFormat orderFileFormat;

    public Distributor() {
        super(TABLE_NAME);
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
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            if (searchTerm.contains(getWebsite().toUpperCase())) {
                return true;
            }
        }
        return false;
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
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<Distributor> distributors = db().getDistributors();
                if (!distributors.contains(this)) {
                    distributors.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<Distributor> distributors = db().getDistributors();
                if (distributors.contains(this)) {
                    distributors.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onDistributorsChangedListenerList);
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
        this.orderFileFormatId = orderFileFormatId;
        orderFileFormat = null;
    }

    public void setOrderFileFormatId(String id) {
        try {
            this.orderFileFormatId = Long.valueOf(id);
            orderFileFormat = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OrderFileFormat getOrderFileFormat() {
        if (orderFileFormat == null) {
            orderFileFormat = SearchManager.sm().findOrderFileFormatById(orderFileFormatId);
        }
        return orderFileFormat;
    }
}
