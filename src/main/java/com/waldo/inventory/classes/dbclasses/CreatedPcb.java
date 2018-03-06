package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class CreatedPcb extends DbObject {

    public static final String TABLE_NAME = "createdpcbs";

    private long projectPcbId;
    private ProjectPcb projectPcb;

    private Date dateCreated;

    private List<Item> usedItems;

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
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<CreatedPcb> list = cache().getCreatedPcbs();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<CreatedPcb> list = cache().getCreatedPcbs();
                if (list.contains(this)) {
                    list.remove(this);
                }
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

    public List<Item> getUsedItems() {
        if (usedItems == null) {
            usedItems = SearchManager.sm().findUsedItemsByCreatedPcbId(getId());
        }
        return usedItems;
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
}
