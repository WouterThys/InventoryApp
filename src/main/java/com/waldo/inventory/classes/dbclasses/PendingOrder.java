package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PendingOrder extends DbObject {

    public static final String TABLE_NAME = "pendingorders";

    private long originalOrderId;
    private ItemOrder originalOrder;

    private List<ItemOrderLine> pendingOrderLines;

    public PendingOrder() {
        super(TABLE_NAME);
    }

    public PendingOrder(@NotNull ItemOrder originalOrder) {
        this();

        this.originalOrder = originalOrder;
        this.originalOrderId = originalOrder.getId();
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        statement.setLong(ndx++, getOriginalOrderId());

        return ndx;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PendingOrder that = (PendingOrder) o;
        return getOriginalOrderId() == that.getOriginalOrderId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getOriginalOrderId());
    }

    @Override
    public PendingOrder createCopy(DbObject copyInto) {
        PendingOrder cpy = new PendingOrder();
        copyBaseFields(cpy);

        cpy.setOriginalOrderId(getOriginalOrderId());

        return cpy;
    }

    @Override
    public PendingOrder createCopy() {
        return createCopy(new PendingOrder());
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

    public long getOriginalOrderId() {
        if (originalOrderId < UNKNOWN_ID) {
            originalOrderId = UNKNOWN_ID;
        }
        return originalOrderId;
    }

    public void setOriginalOrderId(long originalOrderId) {
        if (originalOrder != null && originalOrderId != getOriginalOrderId()) {
            originalOrder = null;
        }
        this.originalOrderId = originalOrderId;
    }

    public ItemOrder getOriginalOrder() {
        if (originalOrder == null && getOriginalOrderId() > UNKNOWN_ID) {
            originalOrder = SearchManager.sm().findItemOrderById(getId());
        }
        return originalOrder;
    }


    public List<ItemOrderLine> getPendingOrderLines() {
        if (pendingOrderLines == null && getOriginalOrder() != null) {
            pendingOrderLines = new ArrayList<>();
            for (AbstractOrderLine<Item> orderLine : originalOrder.getOrderLines()) {
                if (orderLine.isPending()) {
                    pendingOrderLines.add((ItemOrderLine) orderLine);
                }
            }
        }
        return pendingOrderLines;
    }

    public void updatePendingOrderLines() {
        pendingOrderLines = null;
    }
}
