package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ProjectOther extends ProjectObject {

    public static final String TABLE_NAME = "projectothers";

    // Variables


    public ProjectOther() {
        super(TABLE_NAME);
    }

    public ProjectOther(String name) {
        super(TABLE_NAME);
        setName(name);
    }

    public ProjectOther(long projectId) {
        super(TABLE_NAME);
        setProjectId(projectId);
    }

    @Override
    public ProjectOther createCopy() {
        return (ProjectOther) createCopy(new ProjectOther());
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
}