package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ItemOrderLine extends DbObject {

    public static final String TABLE_NAME = "itemorderlines";

    // Variables
    private long orderId;
    private ItemOrder itemOrder;

    // If itemOrder type = Item
    private long itemId;
    private Item item;

    private DistributorPartLink distributorPartLink;

    protected int amount;

    public ItemOrderLine() {
        super(TABLE_NAME);
    }

    public ItemOrderLine(@NotNull ItemOrder itemOrder, @NotNull Item item, int amount) {
        this();
        this.itemOrder = itemOrder;
        this.orderId = itemOrder.getId();

        this.item = item;
        this.itemId = item.getId();

        this.amount = amount;
    }

    @Override
    public String toString() {
        return getName() + super.toString();
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;//addBaseParameters(statement);

        statement.setInt(ndx++, getAmount());

        statement.setLong(ndx++, getOrderId());
        statement.setLong(ndx++, getItemId());

        return ndx;
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
    public ItemOrderLine createCopy() {
        return createCopy(new ItemOrderLine());
    }

    @Override
    public ItemOrderLine createCopy(DbObject copyInto) {
        ItemOrderLine cpy = (ItemOrderLine) copyInto;
        copyBaseFields(cpy);
        cpy.setOrderId(getOrderId());
        cpy.setAmount(getAmount());
        cpy.setItemId(getItemId());
        return cpy;
    }


    public Price getPrice() {
        if (getDistributorPartLink() != null) {
            return distributorPartLink.getPrice();
        }
        return new Price();
    }

    public Price getTotalPrice() {
        return Price.multiply(getPrice(), getAmount());
    }

    public boolean isLocked() {
        return getItemOrder() != null && itemOrder.isLocked();
    }

    public void updateLineAmount(boolean increment) {
        if (getItemOrder() != null) {
            if (getItem() != null) {
                int current = item.getAmount();
                if (increment) {
                    item.setAmount(current + getAmount());
                } else {
                    item.setAmount(current - getAmount());
                    if (item.getAmount() < 0) {
                        item.setAmount(0);
                    }
                }
                item.save();
            }
        }
    }

    public DistributorPartLink getDistributorPartLink() {
        if (distributorPartLink == null && getItemOrder() != null) {
            if (getItem() != null) {
                distributorPartLink = SearchManager.sm().findDistributorPartLink(itemOrder.getDistributorId(), item);
            }
        }
        return distributorPartLink;
    }

    public void updateOrderState() {
        if (getItemOrder() != null) {
            if (getItem() != null) {
                item.updateOrderState();
            }
        }
    }

    //
    // Getters, setters
    //
    public long getOrderId() {
        if (orderId <= UNKNOWN_ID) {
            orderId = UNKNOWN_ID;
        }
        return orderId;
    }

    public ItemOrder getItemOrder() {
        if (itemOrder == null && orderId > UNKNOWN_ID) {
            itemOrder = SearchManager.sm().findItemOrderById(getOrderId());
        }
        return itemOrder;
    }

    public void setOrderId(long orderId) {
        if (itemOrder != null && itemOrder.getId() != orderId) {
            itemOrder = null;
        }
        this.orderId = orderId;
    }


    public long getItemId() {
        if (itemId < UNKNOWN_ID) {
            itemId = UNKNOWN_ID;
        }
        return itemId;
    }

    public Item getItem() {
        if (item == null && getItemId() > UNKNOWN_ID) {
            item = SearchManager.sm().findItemById(itemId);
        }
        return item;
    }

    public void setItemId(long itemId) {
        if (item != null && item.getId() != itemId) {
            item = null;
        }
        this.itemId = itemId;
    }

    public long getDistributorPartId() {
        if (distributorPartLink == null) {
            distributorPartLink = getDistributorPartLink();
        }
        if (distributorPartLink != null) {
            return distributorPartLink.getId();
        }
        return UNKNOWN_ID;
    }

    public void updateDistributorPart() {
        distributorPartLink = null;
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}