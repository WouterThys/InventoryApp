package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class ProjectPcb extends ProjectObject {

    public static final String TABLE_NAME = "projectpcbs";

    // Variables

    // Items??


    public ProjectPcb() {
        super(TABLE_NAME);
    }

    public ProjectPcb(String name) {
        super(TABLE_NAME);
        setName(name);
    }

    @Override
    public ProjectPcb createCopy() {
        return (ProjectPcb) createCopy(new ProjectPcb());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<ProjectPcb> list = db().getProjectPcbs();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<ProjectPcb> list = db().getProjectPcbs();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onProjectPcbChangedListenerList);
    }

    public static ProjectPcb getUnknownProjectPcbs() {
        ProjectPcb u = new ProjectPcb();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }
}