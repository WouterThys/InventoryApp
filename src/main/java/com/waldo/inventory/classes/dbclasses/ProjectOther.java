package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DbManager;

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
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {

                break;
            }
            case DbManager.OBJECT_UPDATE: {

                break;
            }
            case DbManager.OBJECT_DELETE: {

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