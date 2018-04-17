package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.OrderStates;
import com.waldo.inventory.managers.SearchManager;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.waldo.inventory.managers.CacheManager.cache;

public class DistributorOrderFlow extends DbObject {

    public static final String TABLE_NAME = "distributororderflows";

    // Variables
    private int sequenceNumber;
    private OrderStates orderState;
    private String description;
    private Date dateEntered;

    private long distributorId;
    private Distributor distributor;

    public DistributorOrderFlow() {
        super(TABLE_NAME);
    }

    public DistributorOrderFlow(Distributor distributor, int sequenceNumber) {
        this();

        this.distributor = distributor;
        this.sequenceNumber = sequenceNumber;
        if (distributor != null) {
            this.distributorId = distributor.getId();
        }
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        // Add parameters
        statement.setLong(ndx++, getDistributorId());
        statement.setInt(ndx++, getSequenceNumber());
        statement.setInt(ndx++, getOrderState().getIntValue());
        statement.setString(ndx++, getDescription());
        if (dateEntered != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateEntered.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }

        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = super.equals(obj);
        if (res) {
            if (obj instanceof DistributorOrderFlow) {
                DistributorOrderFlow ref = (DistributorOrderFlow) obj;
                return ref.getSequenceNumber() == getSequenceNumber() &&
                        ref.getDescription().equals(getDescription()) &&
                        ref.getOrderState().equals(getOrderState()) &&
                        ref.getDistributorId() == getDistributorId();
            }
        }
        return false;
    }

    @Override
    public DistributorOrderFlow createCopy(DbObject copyInto) {
        DistributorOrderFlow cpy = (DistributorOrderFlow) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setDistributorId(getDistributorId());
        cpy.setSequenceNumber(getSequenceNumber());
        cpy.setOrderState(getOrderState());
        cpy.setDescription(getDescription());
        cpy.setDateEntered(getDateEntered());

        return cpy;
    }

    @Override
    public DistributorOrderFlow createCopy() {
        return createCopy(new DistributorOrderFlow());
    }

    //
    // DbManager tells the object is updated
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

    // Getters and setters

    public long getDistributorId() {
        if (distributorId < UNKNOWN_ID) {
            distributorId = UNKNOWN_ID;
        }
        return distributorId;
    }

    public void setDistributorId(long distributorId) {
        if (distributor != null && distributor.getId() != distributorId) {
            distributor = null;
        }
        this.distributorId = distributorId;
    }

    public Distributor getDistributor() {
        if (distributor == null && getDistributorId() > UNKNOWN_ID) {
            distributor = SearchManager.sm().findDistributorById(distributorId);
        }
        return distributor;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public OrderStates getOrderState() {
        if (orderState == null) {
            orderState = OrderStates.NoOrder;
        }
        return orderState;
    }

    public void setOrderState(OrderStates orderState) {
        this.orderState = orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = OrderStates.fromInt(orderState);
    }

    public String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public void setDateEntered(Timestamp dateEntered) {
        if (dateEntered != null) {
            this.dateEntered = new Date(dateEntered.getTime());
        }
    }
}