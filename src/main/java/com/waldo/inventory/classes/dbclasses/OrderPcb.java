package com.waldo.inventory.classes.dbclasses;


import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import static com.waldo.inventory.managers.CacheManager.cache;


public class OrderPcb extends OrderLine {

    public static final String TABLE_NAME = "orderpcbs";

    public OrderPcb() {
        super(TABLE_NAME);
    }

    public OrderPcb(long orderId, long pcbId, int amount) {
        super(TABLE_NAME);

        setOrderId(orderId);
        setObjectId(pcbId);
        setAmount(amount);
    }

    @Override
    public OrderPcb createCopy(DbObject copyInto) {
        OrderPcb orderPcb = (OrderPcb) copyInto;
        copyBaseFields(orderPcb);
        orderPcb.setOrderId(getOrderId());
        orderPcb.setObjectId(getObjectId());
        orderPcb.setAmount(getAmount());
        return orderPcb;
    }

    @Override
    public OrderPcb createCopy() {
        return createCopy(new OrderPcb());
    }

    @Override
    public void removeFromOrder() {

    }

    @Override
    public ProjectPcb getObject() {
        if (object == null && getObjectId() > UNKNOWN_ID) {
            object = SearchManager.sm().findProjectPcbById(objectId);
        }
        return (ProjectPcb) object;
    }

    public ProjectPcb getPcb() {
        return getObject();
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

    @Override
    public void updateOrderState() {
        if (getObjectId() > DbObject.UNKNOWN_ID) {
            // TODO projectPcb.updateOrderState();
        }
    }

    @Override
    public void updateLineAmount(boolean increment) {
        // TODO
    }

    @Override
    public DistributorPartLink getDistributorPartLink() {
//        if (distributorPartLink == null && getOrder() != null) {
//            distributorPartLink = sm().findDistributorPartLink(getOrder().getDistributorId(), getItemId());
//        } // TODO
        return distributorPartLink;
    }
}