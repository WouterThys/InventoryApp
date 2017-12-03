package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;

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

                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {

                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {

                break;
            }
        }
    }

    public static ProjectOther getUnknownProjectOther() {
        ProjectOther u = new ProjectOther();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    // Getters and setters
}