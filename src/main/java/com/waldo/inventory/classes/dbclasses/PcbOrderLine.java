package com.waldo.inventory.classes.dbclasses;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbOrderLine extends DbObject {

    public static final String TABLE_NAME = "pcborderlines";

    // Variables
    private long orderId;
    private PcbOrder pcbOrder;

    // If itemOrder type = Item
    private long pcbId;
    private ProjectPcb pcb;

    private DistributorPartLink distributorPartLink;

    protected int amount;

    public PcbOrderLine() {
        super(TABLE_NAME);
    }

    public PcbOrderLine(@NotNull PcbOrder pcbOrder, @NotNull ProjectPcb pcb, int amount) {
        this();
        this.pcbOrder = pcbOrder;
        this.orderId = pcbOrder.getId();

        this.pcb = pcb;
        this.pcbId = pcb.getId();

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
        statement.setLong(ndx++, getPcbId());

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
    public PcbOrderLine createCopy() {
        return createCopy(new PcbOrderLine());
    }

    @Override
    public PcbOrderLine createCopy(DbObject copyInto) {
        PcbOrderLine cpy = (PcbOrderLine) copyInto;
        copyBaseFields(cpy);
        cpy.setPcbOrderId(getOrderId());
        cpy.setAmount(getAmount());
        cpy.setPcbId(getPcbId());
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
        return getPcbOrder() != null && pcbOrder.isLocked();
    }

    public void updateLineAmount(boolean increment) {
        if (getPcb() != null) {
            int current = pcb.getAmount();
            if (increment) {
                pcb.setAmount(current + getAmount());
            } else {
                pcb.setAmount(current - getAmount());
                if (pcb.getAmount() < 0) {
                    pcb.setAmount(0);
                }
            }
            pcb.save();
        }
    }

    public DistributorPartLink getDistributorPartLink() {
        if (distributorPartLink == null && getPcbOrder() != null) {
            if (getPcb() != null) {
                distributorPartLink = SearchManager.sm().findDistributorPartLink(pcbOrder.getDistributorId(), pcb);
            }
        }
        return distributorPartLink;
    }

    public void updateOrderState() {
        if (getPcb() != null) {
            pcb.updateOrderState();
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

    public PcbOrder getPcbOrder() {
        if (pcbOrder == null && getOrderId() > UNKNOWN_ID) {
            pcbOrder = SearchManager.sm().findPcbOrderById(orderId);
        }
        return pcbOrder;
    }

    public void setPcbOrderId(long orderId) {
        if (pcbOrder != null && pcbOrder.getId() != orderId) {
            pcbOrder = null;
        }
        this.orderId = orderId;
    }


    public long getPcbId() {
        if (pcbId < UNKNOWN_ID) {
            pcbId = UNKNOWN_ID;
        }
        return pcbId;
    }

    public ProjectPcb getPcb() {
        if (pcb == null && getPcbId() > UNKNOWN_ID) {
            pcb = SearchManager.sm().findProjectPcbById(pcbId);
        }
        return pcb;
    }

    public void setPcbId(long pcbId) {
        if (pcb != null && pcb.getId() != pcbId) {
            pcb = null;
        }
        this.pcbId = pcbId;
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