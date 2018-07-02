package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.Price;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public abstract class AbstractOrder<T extends Orderable> extends DbObject {

    protected Date dateOrdered;
    protected Date dateModified;
    protected Date dateReceived;

    protected List<AbstractOrderLine<T>> orderLines;

    protected long distributorId;
    protected Distributor distributor;
    protected double VAT;

    protected String orderReference;
    protected String trackingNumber;

    protected boolean isLocked;
    protected boolean isAutoOrder;

    public AbstractOrder(String tableName) {
        super(tableName);
    }

    public AbstractOrder(String tableName, String name) {
        super(tableName);
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
        statement.setDouble(ndx++, getVAT());
        statement.setString(ndx++, getOrderReference());
        statement.setString(ndx++, getTrackingNumber());
        statement.setBoolean(ndx++, isAutoOrder());
        return ndx;
    }

//    @Override
//    public String toString() {
//        if (isOrdered()) {
//            return DateUtils.formatMonthDate(dateOrdered) + " - " + super.toString();
//        } else {
//            if (isUnknown() || !canBeSaved()) {
//                return super.toString();
//            } else {
//                if (dateModified != null) {
//                    return DateUtils.formatMonthDate(dateModified) + " - " + super.toString();
//                } else {
//                    return "";
//                }
//            }
//        }
//    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof ItemOrder)) {
                return false;
            } else {
                ItemOrder ref = (ItemOrder) obj;

                if (!compareIfEqual(ref.getDateOrdered(), getDateOrdered())) return false;
                if (!compareIfEqual(ref.getDateReceived(), getDateReceived())) return false;
                if (!(ref.getOrderLines().size() == (getOrderLines().size()))) return false;
                if (!(ref.getDistributorId() == (getDistributorId()))) return false;
                if (!(ref.getVAT() == (getVAT()))) return false;
                if (!(ref.getOrderReference().equals(getOrderReference()))) return false;
                if (!(ref.getTrackingNumber().equals(getTrackingNumber()))) return false;
                if (!(ref.isAutoOrder() == isAutoOrder())) return false;

            }
        }
        return result;
    }

    public void copyOrderLinesToClipboard() {
        try {
            String orderText = createOrderText();
            StringSelection selection = new StringSelection(orderText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createOrderText() {
        StringBuilder builder = new StringBuilder();
        for (AbstractOrderLine<T> orderLine : getOrderLines()) {
            builder.append(orderLine.getDistributorPartLink().getReference());
            builder.append(getDistributor().getOrderFileFormat().getSeparator());
            builder.append(orderLine.getAmount());
            builder.append("\n");
        }
        return builder.toString();
    }

    public boolean containsOrderLineFor(T line) {
        return findOrderLineFor(line) != null;
    }

    public AbstractOrderLine<T> findOrderLineFor(T line) {
        if (line != null) {
            for (AbstractOrderLine<T> ol : getOrderLines()) {
                if (ol.getLineId() == line.getId()) {
                    return ol;
                }
            }
        }
        return null;
    }

    public void addOrderLine(AbstractOrderLine<T> line) {
        if (line != null) {
            if (!orderLines.contains(line)) {
                orderLines.add(line);
                setDateModified(new Date(System.currentTimeMillis()));
            }
        }
    }

    public void removeOrderLine(AbstractOrderLine<T> line) {
        if (line != null) {
            if (orderLines.contains(line)) {
                // Remove OrderItem from db
                line.delete();
                // Remove from list
                orderLines.remove(line);
                // Update modification date
                setDateModified(new Date(System.currentTimeMillis()));
                // Update the item of the order item
                SwingUtilities.invokeLater(line::updateOrderState);
            }
        }
    }

    public void updateLineReferences() {
        for (AbstractOrderLine<T> oi : getOrderLines()) {
            oi.updateDistributorPart();
        }
    }

    public Price getTotalPriceExc() {
        Price total = new Price(0, Statics.PriceUnits.Euro);
        for (AbstractOrderLine<T> oi : getOrderLines()) {
            total = Price.add(total, oi.getTotalPrice());
        }
        return total;
    }

    public Price getTotalPriceInc() {
        Price inc = getTotalPriceExc();
        if (getVAT() > 0) {
            double vat = inc.getValue() * (getVAT() / 100);
            inc = Price.add(inc, vat);
        }
        return inc;
    }


    public void updateLineStates() {
        for (AbstractOrderLine<T> oi : getOrderLines()) {
            oi.updateOrderState();
        }
    }

    public void updateLineAmounts(boolean increment) {
        for (AbstractOrderLine<T> oi : getOrderLines()) {
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
        if (dateOrdered != null && DateUtils.getYear(dateOrdered) < 1980) {
            dateOrdered = null;
        }
        this.dateOrdered = dateOrdered;
    }

    public void setDateOrdered(Timestamp dateOrdered) {
        if (dateOrdered != null) {
            this.dateOrdered = new Date(dateOrdered.getTime());
        }
    }

    public abstract List<AbstractOrderLine<T>> getOrderLines();

    public void updateOrderLines() {
        orderLines = null;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        if (dateModified != null && DateUtils.getYear(dateModified) < 1980) {
            dateModified = null;
        }
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
        if (dateReceived != null && DateUtils.getYear(dateReceived) < 1980) {
            dateReceived = null;
        }
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
            if (getOrderLines().size() > 0) {
                for (AbstractOrderLine<T> orderItem : getOrderLines()) {
                    orderItem.updateDistributorPart();
                }
            }
        }
        this.distributorId = id;
    }

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
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


    public boolean canCreateOrderFile() {
        return getDistributorType() != null && getDistributorType().equals(Statics.DistributorType.Items);
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
