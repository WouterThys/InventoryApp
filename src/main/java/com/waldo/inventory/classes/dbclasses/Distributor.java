package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Distributor extends DbObject {

    public static final String TABLE_NAME = "distributors";

    private String website;
    private String orderLink;
    private long orderFileFormatId = -1;
    private OrderFileFormat orderFileFormat;
    private DistributorType distributorType;

    private List<DistributorOrderFlow> orderFlowTemplate;

    public Distributor() {
        super(TABLE_NAME);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setString(ndx++, getWebsite());
        statement.setString(ndx++, getOrderLink());
        if (orderFileFormatId < UNKNOWN_ID) {
            orderFileFormatId = UNKNOWN_ID;
        }
        statement.setLong(ndx++, getOrderFileFormatId());
        statement.setInt(ndx++, getDistributorType().getIntValue());
        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof Distributor)) {
                return false;
            }
            if (!(((Distributor)obj).getWebsite().equals(getWebsite()))) return false;
            if (!(((Distributor)obj).getOrderLink().equals(getOrderLink()))) return false;
            if (!(((Distributor)obj).getOrderFileFormatId() == getOrderFileFormatId())) return false;
            if (!(((Distributor)obj).getDistributorType().equals(getDistributorType()))) return false;
        }
        return result;
    }

    @Override
    public Distributor createCopy(DbObject copyInto) {
        Distributor distributor = (Distributor) copyInto;
        copyBaseFields(distributor);
        distributor.setWebsite(getWebsite());
        distributor.setOrderLink(getOrderLink());
        distributor.setOrderFileFormatId(getOrderFileFormatId());
        distributor.setDistributorType(getDistributorType());
        return distributor;
    }

    @Override
    public Distributor createCopy() {
        return createCopy(new Distributor());
    }

    public void moveFlowUp(DistributorOrderFlow flow) {
        if (flow != null) {
            DistributorOrderFlow prevFlow = getPreviousOrderFlow(flow);
            if (prevFlow != null) {
                switchFlowSequence(prevFlow, flow);

                prevFlow.save();
                flow.save();
                updateOrderFlowTemplate();
            }
        }
    }

    public void moveFlowDown(DistributorOrderFlow flow) {
        if (flow != null) {
            DistributorOrderFlow nextFlow = getNextOrderFlow(flow);
            if (nextFlow != null) {
                switchFlowSequence(nextFlow, flow);

                nextFlow.save();
                flow.save();
                updateOrderFlowTemplate();
            }
        }
    }

    private void switchFlowSequence(DistributorOrderFlow flow1, DistributorOrderFlow flow2) {
        if (flow1 != null && flow2 != null) {
            int f1 = flow1.getSequenceNumber();
            int f2 = flow2.getSequenceNumber();
            flow1.setSequenceNumber(f2);
            flow2.setSequenceNumber(f1);
        }
    }

    private DistributorOrderFlow getPreviousOrderFlow(DistributorOrderFlow flow) {
        int prevNdx = flow.getSequenceNumber() - 2;
        if (prevNdx >= 0) {
            return getOrderFlowTemplate().get(prevNdx);
        }
        return null;
    }

    private DistributorOrderFlow getNextOrderFlow(DistributorOrderFlow flow) {
        int nextNdx = flow.getSequenceNumber();
        if (nextNdx < getOrderFlowTemplate().size()) {
            return getOrderFlowTemplate().get(nextNdx);
        }
        return null;
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

    //
    // Getters, setters
    //
    public String getWebsite() {
        if (website == null) {
            website = "";
        }
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOrderLink() {
        if (orderLink == null) {
            orderLink = "";
        }
        return orderLink;
    }

    public void setOrderLink(String orderLink) {
        this.orderLink = orderLink;
    }

    public long getOrderFileFormatId() {
        return orderFileFormatId;
    }

    public void setOrderFileFormatId(long orderFileFormatId) {
        if (orderFileFormat != null && orderFileFormat.getId() != orderFileFormatId) {
            orderFileFormat = null;
        }
        this.orderFileFormatId = orderFileFormatId;
    }

    public OrderFileFormat getOrderFileFormat() {
        if (orderFileFormat == null) {
            orderFileFormat = SearchManager.sm().findOrderFileFormatById(orderFileFormatId);
        }
        return orderFileFormat;
    }

    public DistributorType getDistributorType() {
        if (distributorType == null) {
            distributorType = DistributorType.Items;
        }
        return distributorType;
    }

    public void setDistributorType(DistributorType distributorType) {
        this.distributorType = distributorType;
    }

    public void setDistributorType(int distributorType) {
        this.distributorType = DistributorType.fromInt(distributorType);
    }

    public List<DistributorOrderFlow> getOrderFlowTemplate() {
        if (orderFlowTemplate == null) {
            orderFlowTemplate = SearchManager.sm().findOrderFlowTemplateForDistributor(getId());
            orderFlowTemplate.sort(new ComparatorUtils.DistributorOrderFlowComparator());
        }
        return orderFlowTemplate;
    }

    public void updateOrderFlowTemplate() {
        orderFlowTemplate = null;
    }
}
