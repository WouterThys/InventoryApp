package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;

import java.util.List;

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
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<ProjectOther> list = cache().getProjectOthers();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<ProjectOther> list = cache().getProjectOthers();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }
}