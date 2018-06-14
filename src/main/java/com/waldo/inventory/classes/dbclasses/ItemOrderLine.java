package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ItemOrderLine extends AbstractOrderLine<Item> {

    public static final String TABLE_NAME = "itemorderlines";

    public ItemOrderLine() {
        super(TABLE_NAME);
    }

    public ItemOrderLine(@NotNull ItemOrder itemOrder, @NotNull Item item, int amount) {
        super(TABLE_NAME, itemOrder, item, amount);
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
        cpy.setLineId(getLineId());
        return cpy;
    }


    public DistributorPartLink getDistributorPartLink() {
        if (distributorPartLink == null && getOrder() != null) {
            if (getLine() != null) {
                distributorPartLink = SearchManager.sm().findDistributorPartLink(order.getDistributorId(), line);
            }
        }
        return distributorPartLink;
    }

    @Override
    public ItemOrder getOrder() {
        if (order == null && getOrderId() > UNKNOWN_ID) {
            order = SearchManager.sm().findItemOrderById(orderId);
        }
        return (ItemOrder) order;
    }

    @Override
    public Item getLine() {
        if (line == null && getLineId() > UNKNOWN_ID) {
            line = SearchManager.sm().findItemById(lineId);
        }
        return line;
    }
}