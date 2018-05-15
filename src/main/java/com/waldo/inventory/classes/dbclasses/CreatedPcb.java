package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.DateUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class CreatedPcb extends DbObject {

    public static final String TABLE_NAME = "createdpcbs";

    private long projectPcbId;
    private ProjectPcb projectPcb;

    private long orderId; // Was created when this order was received
    private Order order;

    private Date dateCreated;
    private Date dateSoldered;
    private Date dateDestroyed;

    private List<CreatedPcbLink> createdPcbLinks;

    public CreatedPcb() {
        super(TABLE_NAME);
    }

    public CreatedPcb(String name, ProjectPcb projectPcb, Order order) {
        this();
        setName(name);
        this.projectPcb = projectPcb;
        this.order = order;

        if (projectPcb != null) {
            this.projectPcbId = projectPcb.getId();
        }
        if (order != null) {
            this.orderId = order.getId();
        }
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        if (projectPcbId <= UNKNOWN_ID) {
            setProjectPcbId(UNKNOWN_ID);
        }
        statement.setLong(ndx++, getProjectPcbId());
        statement.setLong(ndx++, getOrderId());

        if (getDateCreated() != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateCreated.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }

        if (getDateSoldered() != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateSoldered.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }

        if (getDateDestroyed() != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateDestroyed.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }

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
    public CreatedPcb createCopy() {
        return createCopy(new CreatedPcb());
    }

    @Override
    public CreatedPcb createCopy(DbObject copyInto) {
        CreatedPcb cpy = new CreatedPcb();
        copyBaseFields(cpy);
        cpy.setProjectPcbId(getProjectPcbId());
        cpy.setOrderId(getOrderId());
        cpy.setDateCreated(getDateCreated());
        cpy.setDateSoldered(getDateSoldered());
        cpy.setDateDestroyed(getDateDestroyed());
        return cpy;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result && obj instanceof CreatedPcb) {
            CreatedPcb ref = (CreatedPcb) obj;

            return (ref.getProjectPcbId() == getProjectPcbId()) && (ref.getOrderId() == getOrderId());
        }
        return false;
    }

    public boolean isCreated() {
        return dateCreated != null && !dateCreated.equals(DateUtils.minDate());
    }

    public boolean isSoldered() {
        return dateSoldered != null && !dateSoldered.equals(DateUtils.minDate());
    }

    public boolean isDestroyed() {
        return dateDestroyed != null && !dateDestroyed.equals(DateUtils.minDate());
    }


    public long getProjectPcbId() {
        if (projectPcbId < UNKNOWN_ID) {
            projectPcbId = UNKNOWN_ID;
        }
        return projectPcbId;
    }

    public void setProjectPcbId(long projectPcbId) {
        if (projectPcb != null && projectPcb.getId() != projectPcbId) {
            projectPcb = null;
        }
        this.projectPcbId = projectPcbId;
    }

    public ProjectPcb getProjectPcb() {
        if (projectPcb == null && getProjectPcbId() > UNKNOWN_ID) {
            projectPcb = SearchManager.sm().findProjectPcbById(projectPcbId);
        }
        return projectPcb;
    }



    public long getOrderId() {
        if (orderId < UNKNOWN_ID) {
            orderId = UNKNOWN_ID;
        }
        return orderId;
    }

    public void setOrderId(long orderId) {
        if (order != null && order.getId() != orderId) {
            order = null;
        }
        this.orderId = orderId;
    }

    public Order getOrder() {
        if (order == null && getOrderId() > DbObject.UNKNOWN_ID) {
            order = SearchManager.sm().findOrderById(orderId);
        }
        return order;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        if (dateCreated != null) {
            this.dateCreated = new Date(dateCreated.getTime());
        }
    }


    public Date getDateSoldered() {
        return dateSoldered;
    }

    public void setDateSoldered(Date dateSoldered) {
        this.dateSoldered = dateSoldered;
    }

    public void setDateSoldered(Timestamp dateSoldered) {
        if (dateSoldered != null) {
            this.dateSoldered = new Date(dateSoldered.getTime());
        }
    }


    public Date getDateDestroyed() {
        return dateDestroyed;
    }

    public void setDateDestroyed(Date dateDestroyed) {
        this.dateDestroyed = dateDestroyed;
    }

    public void setDateDestroyed(Timestamp dateDestroyed) {
        if (dateDestroyed != null) {
            this.dateDestroyed = new Date(dateDestroyed.getTime());
        }
    }


    public List<CreatedPcbLink> getCreatedPcbLinks() {
        if (createdPcbLinks == null) {
            createdPcbLinks = SearchManager.sm().findCreatedPcbLinks(getProjectPcbId(), getId());
        }
        return createdPcbLinks;
    }

    public void updateCreatedPcbLinks() {
        createdPcbLinks = null;
    }

    public void createPcbLinks() {
        createdPcbLinks = new ArrayList<>();
        List<PcbItemProjectLink> pcbItemList = getProjectPcb().getPcbItemList();
        List<CreatedPcbLink> createdPcbLinkList = new ArrayList<>(SearchManager.sm().findCreatedPcbLinks(getProjectPcb().getId(), getId()));

        for (PcbItemProjectLink pipl : pcbItemList) {
            CreatedPcbLink link = findPcbItem(createdPcbLinkList, pipl.getPcbItemId());

            if (link != null) {
                // Ok already there..
                createdPcbLinkList.remove(link);
            } else {
                link = new CreatedPcbLink(pipl.getId(), getId());
                for (String reference : pipl.getReferences()) {
                    SolderItem solderItem = new SolderItem(reference, link);
                    link.getSolderItems().add(solderItem);
                }
            }

            createdPcbLinks.add(link);
        }

        // Save links
        for (CreatedPcbLink link : createdPcbLinks) {
            link.save();
        }
    }

    private CreatedPcbLink findPcbItem(List<CreatedPcbLink> searchList, long pcbItemId) {
        for (CreatedPcbLink cpl : searchList) {
            if (cpl.getPcbItemProjectLinkId() > DbObject.UNKNOWN_ID) {
                if (cpl.getPcbItemProjectLink().getPcbItemId() == pcbItemId) {
                    return cpl;
                }
            }
        }
        return null;
    }
}
