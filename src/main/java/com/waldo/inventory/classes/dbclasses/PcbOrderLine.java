package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbOrderLine extends AbstractOrderLine<ProjectPcb> {

    public static final String TABLE_NAME = "pcborderlines";

    public PcbOrderLine() {
        super(TABLE_NAME);
    }

    public PcbOrderLine(@NotNull PcbOrder pcbOrder, @NotNull ProjectPcb pcb, int amount) {
        super(TABLE_NAME, pcbOrder, pcb, amount);
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
    public PcbOrderLine createCopy() {
        return createCopy(new PcbOrderLine());
    }

    @Override
    public PcbOrderLine createCopy(DbObject copyInto) {
        PcbOrderLine cpy = (PcbOrderLine) copyInto;
        copyBaseFields(cpy);
        cpy.setOrderId(getOrderId());
        cpy.setAmount(getAmount());
        cpy.setLineId(getLineId());
        return cpy;
    }

    public DistributorPartLink getDistributorPartLink() {
        if (distributorPartLink == null && getOrder() != null) {
            if (getLine() != null) {
                distributorPartLink = SearchManager.sm().findDistributorPartLink(getOrder().getDistributorId(), line);
            }
        }
        return distributorPartLink;
    }

    //
    // Getters, setters
    //

    public PcbOrder getOrder() {
        if (order == null && getOrderId() > UNKNOWN_ID) {
            order = SearchManager.sm().findPcbOrderById(orderId);
        }
        return (PcbOrder) order;
    }

    @Override
    public ProjectPcb getLine() {
        if (line == null && getLineId() > DbObject.UNKNOWN_ID) {
            line = SearchManager.sm().findProjectPcbById(lineId);
        }
        return line;
    }
}