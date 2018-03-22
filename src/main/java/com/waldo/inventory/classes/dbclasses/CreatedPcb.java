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

    private Date dateCreated;

    private List<CreatedPcbLink> createdPcbLinks;

    public CreatedPcb() {
        super(TABLE_NAME);
    }

    public CreatedPcb(String name, ProjectPcb projectPcb) {
        this();
        setName(name);
        this.projectPcb = projectPcb;
        if (projectPcb != null) {
            this.projectPcbId = projectPcb.getId();
        }
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        if (projectPcbId <= UNKNOWN_ID) {
            setProjectPcbId(UNKNOWN_ID);
        }
        statement.setLong(ndx++, getProjectPcbId());

        if (dateCreated != null) {
            statement.setTimestamp(ndx++, new Timestamp(dateCreated.getTime()));
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
        return cpy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CreatedPcb) {
            CreatedPcb ref = (CreatedPcb) obj;

            return ref.getProjectPcbId() == getProjectPcbId();
        }
        return false;
    }

    public boolean isCreated() {
        return dateCreated != null && !dateCreated.equals(DateUtils.minDate());
    }


    public long getProjectPcbId() {
        return projectPcbId;
    }

    public void setProjectPcbId(long projectPcbId) {
        if (projectPcb != null && projectPcb.getId() != projectPcbId) {
            projectPcb = null;
        }
        this.projectPcbId = projectPcbId;
    }

    public ProjectPcb getProjectPcb() {
        if (projectPcb == null) {
            projectPcb = SearchManager.sm().findProjectPcbById(projectPcbId);
        }
        return projectPcb;
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

    public List<CreatedPcbLink> getCreatedPcbLinks() {
        if (createdPcbLinks == null) {
            createdPcbLinks = new ArrayList<>();
            List<PcbItemProjectLink> pcbItemList = getProjectPcb().getPcbItemList();
            List<CreatedPcbLink> createdPcbLinkList = new ArrayList<>(SearchManager.sm().findCreatedPcbLinks(getProjectPcb().getId(), getId()));

            for (PcbItemProjectLink pipl : pcbItemList) {
                CreatedPcbLink link = findPcbItem(createdPcbLinkList, pipl.getPcbItemId());
                if (link != null) {
                    createdPcbLinkList.remove(link);
                } else {
                    link = new CreatedPcbLink(pipl.getId(), getId(), 0);
                }
                createdPcbLinks.add(link);
            }
        }
        return createdPcbLinks;
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

    public void updateCreatedPcbLinks() {
        createdPcbLinks = null;
    }
}
