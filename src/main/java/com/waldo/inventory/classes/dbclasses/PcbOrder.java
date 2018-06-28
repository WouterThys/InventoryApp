package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbOrder extends AbstractOrder<ProjectPcb> {

    public static final String TABLE_NAME = "pcborders";

    public PcbOrder() {
        super(TABLE_NAME);
    }

    public PcbOrder(String name) {
        super(TABLE_NAME, name);
    }

    @Override
    public PcbOrder createCopy(DbObject copyInto) {
        PcbOrder cpy = (PcbOrder) copyInto;
        copyBaseFields(cpy);
        cpy.setDateOrdered(getDateOrdered());
        cpy.setDateModified(getDateModified());
        cpy.setDateReceived(getDateReceived());
        cpy.setDistributorId(getDistributorId());
        cpy.setVAT(getVAT());
        cpy.setOrderReference(getOrderReference());
        cpy.setTrackingNumber(getTrackingNumber());
        cpy.setAutoOrder(isAutoOrder());

        return cpy;
    }

    @Override
    public PcbOrder createCopy() {
        return createCopy(new PcbOrder());
    }

    public static PcbOrder createDummyOrder(String name) {
        PcbOrder itemOrder = new PcbOrder(name);
        itemOrder.setCanBeSaved(false);
        return itemOrder;
    }

    public static PcbOrder createAutoOrder(int autoOrderNumber, Distributor distributor) {
        PcbOrder pcbOrder = null;
        if (distributor != null) {
            pcbOrder = new PcbOrder();
            pcbOrder.setAutoOrder(true);
            pcbOrder.setName("AUTO-ORDER_" + autoOrderNumber);
            pcbOrder.setDistributorId(distributor.getId());
        }
        return pcbOrder;
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert:
                cache().add(this);
                break;

            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    public static PcbOrder getUnknownOrder() {
        PcbOrder o = new PcbOrder();
        o.setName(UNKNOWN_NAME);
        o.setId(UNKNOWN_ID);
        o.setCanBeSaved(false);
        return o;
    }

    @Override
    public List<AbstractOrderLine<ProjectPcb>> getOrderLines() {
        if (orderLines == null) {
            orderLines = new ArrayList<>();
            orderLines.addAll(SearchManager.sm().findPcbOrderLinesForOrder(getId()));
        }
        return orderLines;
    }
}