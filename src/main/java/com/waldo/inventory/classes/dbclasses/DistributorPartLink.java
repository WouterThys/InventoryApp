package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Main;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class DistributorPartLink extends DbObject {

    public static final String TABLE_NAME = "distributorPartLinks";

    private long distributorId;
    private Distributor distributor;

    private long itemId;
    private Item item;

    private String itemRef;
    private Price price;

    public DistributorPartLink() {
        super(TABLE_NAME);
    }

    public DistributorPartLink(long itemId) {
        super(TABLE_NAME);
        setItemId(itemId);
    }

    public DistributorPartLink(long distributorId, long itemId) {
        super(TABLE_NAME);
        setDistributorId(distributorId);
        setItemId(itemId);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, getDistributorId());
        statement.setLong(ndx++, getItemId());
        statement.setString(ndx++, getItemRef());
        statement.setDouble(ndx++, getPrice().getValue());
        statement.setInt(ndx++, getPrice().getPriceUnits().getIntValue());
        return ndx;
    }

    @Override
    public String toString() {
        if (id == -1) {
            if (canBeSaved) {
                return getItemRef() + "*";
            }
        }
        if (id == UNKNOWN_ID) {
            return "";
        }
        if (Main.DEBUG_MODE) {
            return getItemRef() + " (" + id + ")";
        }
        return getItemRef();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof DistributorPartLink) {
                DistributorPartLink dpl = (DistributorPartLink) obj;
                return dpl.getItemId() == getItemId() && dpl.getDistributorId() == getDistributorId();
            }
        }
        return false;
    }

    @Override
    public DistributorPartLink createCopy(DbObject copyInto) {
        DistributorPartLink distributorPartLink = (DistributorPartLink) copyInto;
        copyBaseFields(distributorPartLink);
        distributorPartLink.setDistributorId(getDistributorId());
        distributorPartLink.setItemId(getItemId());
        distributorPartLink.setItemRef(getItemRef());
        distributorPartLink.setPrice(getPrice().getValue(), getPrice().getPriceUnits());
        return distributorPartLink;
    }

    @Override
    public DistributorPartLink createCopy() {
        return createCopy(new DistributorPartLink());
    }

    //
    // DatabaseAccess tells the object is updated
    //
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


    public long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(long distributorId) {
        if (distributor != null && distributor.getId() != distributorId) {
            distributor = null;
        }
        this.distributorId = distributorId;
    }

    public Distributor getDistributor() {
        if (distributor == null) {
            distributor = SearchManager.sm().findDistributorById(distributorId);
        }
        return distributor;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        if (item != null && item.getId() != itemId) {
            item = null;
        }
        this.itemId = itemId;
    }

    public Item getItem() {
        if (item == null) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }

    public String getItemRef() {
        if (itemRef == null) {
            itemRef = "";
        }
        return itemRef;
    }

    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
    }

    public Price getPrice() {
        if (price == null) {
            price = new Price(0, Statics.PriceUnits.Euro);
        }
        return price;
    }

    public void setPrice(double price, Statics.PriceUnits priceUnits) {
        this.price = new Price(price, priceUnits);
    }

    public void setPrice(double price, int priceUnits) {
        this.price = new Price(price, Statics.PriceUnits.fromInt(priceUnits));
    }
}
