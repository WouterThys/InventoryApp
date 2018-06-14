package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.classes.Price;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractOrderLine<T extends Orderable> extends DbObject {


    // Variables
    protected long orderId;
    protected AbstractOrder<T> order;

    // Order element
    protected long lineId;
    protected T line;

    protected DistributorPartLink distributorPartLink;

    protected int amount;

    public AbstractOrderLine(String tableName) {
        super(tableName);
    }

    public AbstractOrderLine(String tableName, @NotNull AbstractOrder<T> order, @NotNull T line, int amount) {
        this(tableName);
        this.order = order;
        this.orderId = order.getId();

        this.line = line;
        this.lineId = line.getId();

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
        statement.setLong(ndx++, getLineId());

        return ndx;
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
        return getOrder() != null && order.isLocked();
    }

    public void updateLineAmount(boolean increment) {
        if (getOrder() != null) {
            if (getLine() != null) {
                int current = line.getAmount();
                if (increment) {
                    line.setAmount(current + getAmount());
                } else {
                    line.setAmount(current - getAmount());
                    if (line.getAmount() < 0) {
                        line.setAmount(0);
                    }
                }
                line.save();
            }
        }
    }

    public abstract DistributorPartLink getDistributorPartLink();

    public void updateOrderState() {
        if (getLine() != null) {
            line.updateOrderState();
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

    public abstract AbstractOrder<T> getOrder();

    public void setOrderId(long orderId) {
        if (order != null && order.getId() != orderId) {
            order = null;
        }
        this.orderId = orderId;
    }


    public long getLineId() {
        if (lineId < UNKNOWN_ID) {
            lineId = UNKNOWN_ID;
        }
        return lineId;
    }

    public abstract T getLine();

    public void setLineId(long lineId) {
        if (line != null && line.getId() != lineId) {
            line = null;
        }
        this.lineId = lineId;
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
