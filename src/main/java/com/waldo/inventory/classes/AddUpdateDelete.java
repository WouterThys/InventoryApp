package com.waldo.inventory.classes;

import java.sql.Date;
import java.util.Calendar;

public class AddUpdateDelete {

    private String insertedBy;
    private Date insertedDate;

    private String updatedBy;
    private Date updatedDate;

    public void setUpdated(String updatedBy) {
        this.updatedBy = updatedBy;
        updatedDate = new Date(Calendar.getInstance().getTime().getTime());
    }

    public void setInserted(String insertedBy) {
        this.insertedBy = insertedBy;
        insertedDate = new Date(Calendar.getInstance().getTime().getTime());
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
