package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
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

    private long projectPcbId;
    private ProjectPcb projectPcb;

    private String reference;
    private Price price;

    public DistributorPartLink() {
        super(TABLE_NAME);
    }

    public DistributorPartLink(@NotNull Item item) {
        this();

        this.item = item;
        this.distributor = null;
        this.distributorId = UNKNOWN_ID;
        this.projectPcb = null;
        this.projectPcbId = UNKNOWN_ID;
    }

    public DistributorPartLink(@NotNull Distributor distributor, long linkId) {
        super(TABLE_NAME);

        this.distributor = distributor;
        this.distributorId = distributor.getId();

        switch (distributor.getDistributorType()) {
            case Items:
                item = null;
                itemId = linkId;
                projectPcb = null;
                projectPcbId = UNKNOWN_ID;
                break;
            case Pcbs:
                item = null;
                itemId = UNKNOWN_ID;
                projectPcb = null;
                projectPcbId = linkId;
                break;
        }
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, getDistributorId());
        statement.setLong(ndx++, getItemId());
        statement.setLong(ndx++, getPcbId());
        statement.setString(ndx++, getReference());
        statement.setDouble(ndx++, getPrice().getValue());
        statement.setInt(ndx++, getPrice().getPriceUnits().getIntValue());
        return ndx;
    }

    @Override
    public String toString() {
        if (id == -1) {
            if (canBeSaved) {
                return getReference() + "*";
            }
        }
        if (id == UNKNOWN_ID) {
            return "";
        }
        if (Main.DEBUG_MODE) {
            return getReference() + " (" + id + ")";
        }
        return getReference();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (obj instanceof DistributorPartLink) {
                DistributorPartLink dpl = (DistributorPartLink) obj;
                return (dpl.getItemId() == getItemId()) &&
                        (dpl.getDistributorId() == getDistributorId()) &&
                        (dpl.getPcbId() == getPcbId());
            }
        }
        return false;
    }

    @Override
    public DistributorPartLink createCopy(DbObject copyInto) {
        DistributorPartLink cpy = (DistributorPartLink) copyInto;
        copyBaseFields(cpy);
        cpy.setDistributorId(getDistributorId());
        cpy.setItemId(getItemId());
        cpy.setPcbId(getPcbId());
        cpy.setReference(getReference());
        cpy.setPrice(getPrice().getValue(), getPrice().getPriceUnits());
        return cpy;
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
        if (itemId < UNKNOWN_ID) {
            itemId = UNKNOWN_ID;
        }
        return itemId;
    }

    public void setItemId(long itemId) {
        if (getDistributor() != null && distributor.getDistributorType() == Statics.DistributorType.Items) {
            if (item != null && item.getId() != itemId) {
                item = null;
            }
            this.itemId = itemId;
        }
    }

    public Item getItem() {
        if (item == null && getItemId() > UNKNOWN_ID) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }


    public long getPcbId() {
        if (projectPcbId < UNKNOWN_ID) {
            projectPcbId = UNKNOWN_ID;
        }
        return projectPcbId;
    }

    public void setPcbId(long pcbId) {
        if (getDistributor() != null && distributor.getDistributorType() == Statics.DistributorType.Pcbs) {
            if (projectPcb != null && projectPcb.getId() != pcbId) {
                projectPcb = null;
            }
            this.projectPcbId = pcbId;
        }
    }

    public ProjectPcb getPcb() {
        if (projectPcb == null && getPcbId() > UNKNOWN_ID) {
            projectPcb = SearchManager.sm().findProjectPcbById(projectPcbId);
        }
        return projectPcb;
    }


    public String getReference() {
        if (reference == null) {
            reference = "";
        }
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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
