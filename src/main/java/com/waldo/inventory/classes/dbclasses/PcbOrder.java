package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.DateUtils;
import com.waldo.utils.OpenUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbOrder extends DbObject {

    public static final String TABLE_NAME = "pcborders";

    private Date dateOrdered;
    private Date dateModified;
    private Date dateReceived;

    private List<PcbOrderLine> pcbOrderLines;

    private long distributorId;
    private Distributor distributor;

    private String orderReference;
    private String trackingNumber;

    private boolean isAutoOrder = false;

    // Runtime variables
    private boolean isLocked;

    public PcbOrder() {
        super(TABLE_NAME);
    }

    public PcbOrder(String name) {
        super(TABLE_NAME);
        this.name = name;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        dateModified = DateUtils.now();

        int ndx = addBaseParameters(statement);
        if (dateOrdered != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateOrdered.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }
        statement.setTimestamp(ndx++, new Timestamp(dateModified.getTime()), Calendar.getInstance());
        if (dateReceived != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateReceived.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }
        statement.setLong(ndx++, getDistributorId());
        statement.setString(ndx++, getOrderReference());
        statement.setString(ndx++, getTrackingNumber());
        statement.setBoolean(ndx++, isAutoOrder());
        return ndx;
    }

    @Override
    public String toString() {
        if (isOrdered()) {
            return DateUtils.formatMonthDate(dateOrdered) + " - " + super.toString();
        } else {
            if (isUnknown() || !canBeSaved()) {
                return super.toString();
            } else {
                if (dateModified != null) {
                    return DateUtils.formatMonthDate(dateModified) + " - " + super.toString();
                } else {
                    return "";
                }
            }
        }
    }

    @Override
    public PcbOrder createCopy(DbObject copyInto) {
        PcbOrder cpy = (PcbOrder) copyInto;
        copyBaseFields(cpy);
        cpy.setDateOrdered(getDateOrdered());
        cpy.setDateModified(getDateModified());
        cpy.setDateReceived(getDateReceived());
        cpy.setDistributorId(getDistributorId());
        cpy.setOrderReference(getOrderReference());
        cpy.setTrackingNumber(getTrackingNumber());
        cpy.setAutoOrder(isAutoOrder());

        return cpy;
    }

    @Override
    public PcbOrder createCopy() {
        return createCopy(new PcbOrder());
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof PcbOrder)) {
                return false;
            } else {
                PcbOrder ref = (PcbOrder) obj;

                if (!compareIfEqual(ref.getDateOrdered(), getDateOrdered())) return false;
                if (!compareIfEqual(ref.getDateReceived(), getDateReceived())) return false;
                if (!(ref.getPcbOrderLines().size() == (getPcbOrderLines().size()))) return false;
                if (!(ref.getDistributorId() == (getDistributorId()))) return false;
                if (!(ref.getOrderReference().equals(getOrderReference()))) return false;
                if (!(ref.getTrackingNumber().equals(getTrackingNumber()))) return false;
                if (!(ref.isAutoOrder() == isAutoOrder())) return false;

            }
        }
        return result;
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




    public void browseOrderPage() throws IOException {
        if (getDistributorId() > UNKNOWN_ID) {
            OpenUtils.browseLink(getDistributor().getOrderLink());
        }
    }

    public static PcbOrder getUnknownOrder() {
        PcbOrder o = new PcbOrder();
        o.setName(UNKNOWN_NAME);
        o.setId(UNKNOWN_ID);
        o.setCanBeSaved(false);
        return o;
    }

    public boolean containsOrderLineFor(ProjectPcb pcb) {
        for (PcbOrderLine ol : getPcbOrderLines()) {
            if (ol.getPcbId() == pcb.getId()) {
                return true;
            }
        }
        return false;
    }

    public PcbOrderLine findOrderLineFor(ProjectPcb pcb) {
        for (PcbOrderLine ol : getPcbOrderLines()) {
            if (ol.getPcbId() == pcb.getId()) {
                return ol;
            }
        }
        return null;
    }



    public void updateItemReferences() {
        for (PcbOrderLine oi : getPcbOrderLines()) {
            oi.updateDistributorPart();
        }
    }

    public Price getTotalPrice() {
        Price total = new Price(0, Statics.PriceUnits.Euro);
        for (PcbOrderLine oi : getPcbOrderLines()) {
            total = Price.add(total, oi.getTotalPrice());
        }
        return total;
    }

    public void updateLineStates() {
        for (PcbOrderLine oi : getPcbOrderLines()) {
            oi.updateOrderState();
        }
    }

    public void updateLineAmounts(boolean increment) {
        for (PcbOrderLine oi : getPcbOrderLines()) {
            oi.updateLineAmount(increment);
        }
    }

    private boolean compareIfEqual(Date d1, Date d2) {
        return d1 == null && d2 == null || !(d1 != null && d2 == null || d1 == null) && d1.equals(d2);
    }

    public Date getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(Date dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public void setDateOrdered(Timestamp dateOrdered) {
        if (dateOrdered != null) {
            this.dateOrdered = new Date(dateOrdered.getTime());
        }
    }

    public List<PcbOrderLine> getPcbOrderLines() {
        if (pcbOrderLines == null) {
            pcbOrderLines = SearchManager.sm().findPcbOrderLinesForOrder(getId());
        }
        return pcbOrderLines;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public void setDateModified(Timestamp dateModified) {
        if (dateModified != null) {
            this.dateModified = new Date(dateModified.getTime());
        }
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public void setDateReceived(Timestamp dateReceived) {
        if (dateReceived != null) {
            this.dateReceived = new Date(dateReceived.getTime());
        }
    }

    public Distributor getDistributor() {
        if (distributor == null && getDistributorId() > UNKNOWN_ID) {
            distributor = SearchManager.sm().findDistributorById(distributorId);
        }
        return distributor;
    }

    public long getDistributorId() {
        if (distributorId <= UNKNOWN_ID) {
            distributorId = UNKNOWN_ID;
        }
        return distributorId;
    }

    public void setDistributorId(long id) {
        if (distributor != null && distributor.getId() != id) {
            distributor = null;
            if (getPcbOrderLines().size() > 0) {
                for (PcbOrderLine line : getPcbOrderLines()) {
                    line.updateDistributorPart();
                }
            }
        }
        this.distributorId = id;
    }

    public boolean isOrdered() {
        return (dateOrdered != null);
    }

    public boolean isReceived() {
        return dateReceived != null;
    }

    public boolean isPlanned() {
        return (dateOrdered == null && dateReceived == null);
    }

    public Statics.OrderStates getOrderState() {
        if (!isOrdered() && !isReceived()) {
            return Statics.OrderStates.Planned;
        } else if (isOrdered() && !isReceived()) {
            return Statics.OrderStates.Ordered;
        } else if (isOrdered() && isReceived()) {
            return Statics.OrderStates.Received;
        } else {
            return Statics.OrderStates.NoOrder;
        }
    }

    public String getOrderReference() {
        if (orderReference == null) {
            orderReference = "";
        }
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public String getTrackingNumber() {
        if (trackingNumber == null) {
            trackingNumber = "";
        }
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Statics.DistributorType getDistributorType() {
        if (getDistributor() != null) {
            return distributor.getDistributorType();
        }
        return null;
    }


    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }


    public boolean isAutoOrder() {
        return isAutoOrder;
    }

    public void setAutoOrder(boolean autoOrder) {
        isAutoOrder = autoOrder;
    }
}