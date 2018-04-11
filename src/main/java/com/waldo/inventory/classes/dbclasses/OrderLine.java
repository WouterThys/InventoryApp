package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.classes.Price;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class OrderLine extends DbObject {

    // Variables
    protected long orderId;
    protected Order order;

    protected long objectId;
    protected DbObject object;

    protected DistributorPartLink distributorPartLink;

    protected int amount;

    public OrderLine(String tableName) {
        super(tableName);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;//addBaseParameters(statement);

        statement.setString(ndx++, "");
        statement.setLong(ndx++, getOrderId());
        statement.setLong(ndx++, getObjectId());
        statement.setInt(ndx++, getAmount());

        return ndx;
    }


    public abstract DistributorPartLink getDistributorPartLink();
    public abstract void updateOrderState();
    public abstract void removeFromOrder();
    public abstract void updateLineAmount(boolean increment);
    public abstract DbObject getObject();

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
        return getOrder() != null && order.isLocked();
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

    public Order getOrder() {
        if (order == null && orderId > UNKNOWN_ID) {
            order = SearchManager.sm().findOrderById(getOrderId());
        }
        return order;
    }

    public void setOrderId(long orderId) {
        if (order != null && order.getId() != orderId) {
            order = null;
        }
        this.orderId = orderId;
    }


    public long getObjectId() {
        if (objectId < UNKNOWN_ID) {
            objectId = UNKNOWN_ID;
        }
        return objectId;
    }

    public void setObjectId(long id) {
        if (object != null && object.getId() != id) {
            object = null;
        }
        this.objectId = id;
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