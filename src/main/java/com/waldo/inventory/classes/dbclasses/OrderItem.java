package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;

import static com.waldo.inventory.database.DatabaseAccess.db;
import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class OrderItem extends OrderLine {

    public static final String TABLE_NAME = "orderitems";

    public OrderItem() {
        super(TABLE_NAME);
    }

    public OrderItem(long orderId, long itemId, int amount) {
        super(TABLE_NAME);

        setOrderId(orderId);
        setObjectId(itemId);
        setAmount(amount);

        setName(getOrder().toString() + " - " + getItem().toString());
    }

    @Override
    public OrderItem createCopy(DbObject copyInto) {
        OrderItem orderItem = (OrderItem) copyInto;
        copyBaseFields(orderItem);
        orderItem.setOrderId(getOrderId());
        orderItem.setObjectId(getObjectId());
        orderItem.setAmount(getAmount());
        return orderItem;
    }

    @Override
    public OrderItem createCopy() {
        return createCopy(new OrderItem());
    }

    @Override
    public void removeFromOrder() {
        db().removeItemFromOrder(this);
    }

    @Override
    public Item getObject() {
        if (object == null) {
            object = sm().findItemById(getObjectId());
        }
        return (Item) object;
    }

    public Item getItem() {
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
            getItem().updateOrderState();
        }
    }

    @Override
    public void updateLineAmount(boolean increment) {
        if (getItem() != null) {
            Item item = getItem();
            int current = item.getAmount();
            item.setAmountType(Statics.ItemAmountTypes.Exact);
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

    @Override
    public DistributorPartLink getDistributorPartLink() {
        if (distributorPartLink == null && getOrder() != null) {
            distributorPartLink = sm().findDistributorPartLink(getOrder().getDistributorId(), getObjectId());
        }
        return distributorPartLink;
    }
}
