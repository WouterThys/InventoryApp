package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.DateUtils;

import java.sql.Date;

public class AddUpdateDelete {

    private String insertedBy;
    private Date insertedDate;

    private String updatedBy;
    private Date updatedDate;

    public void setUpdated(String updatedBy) {
        this.updatedBy = updatedBy;
        updatedDate = DateUtils.now();
    }

    public void setInserted(String insertedBy) {
        this.insertedBy = insertedBy;
        insertedDate = DateUtils.now();
    }

    public String getInsertedBy() {
        return insertedBy;
    }

    public Date getInsertedDate() {
        return insertedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }
}
