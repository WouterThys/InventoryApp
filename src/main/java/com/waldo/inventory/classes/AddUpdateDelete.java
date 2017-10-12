package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.DateUtils;

import java.sql.Date;
import java.sql.Timestamp;

public class AddUpdateDelete {

    private String insertedBy;
    private Date insertedDate;

    private String updatedBy;
    private Date updatedDate;

    public void setUpdated(String updatedBy) {
        this.updatedBy = updatedBy;
        updatedDate = DateUtils.now();
    }

    public void setUpdated(String updatedBy, Timestamp timestamp) {
        this.updatedBy = updatedBy;
        if (timestamp != null) {
            this.updatedDate = new Date(timestamp.getTime());
        }
    }

    public void setInserted(String insertedBy) {
        this.insertedBy = insertedBy;
        insertedDate = DateUtils.now();
    }

    public void setInserted(String insertedBy, Timestamp timestamp) {
        this.insertedBy = insertedBy;
        if (timestamp != null) {
            this.insertedDate = new Date(timestamp.getTime());
        }
    }

    public String getInsertedBy() {
        return insertedBy;
    }

    public Date getInsertedDate() {
        if (insertedDate == null) {
            insertedDate = DateUtils.minDate();
        }
        return insertedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Date getUpdatedDate() {
        if (updatedDate == null) {
            updatedDate = DateUtils.minDate();
        }
        return updatedDate;
    }
}
